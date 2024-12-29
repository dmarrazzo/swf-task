package com.redhat.example.services;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.SubmissionPublisher;

import org.jboss.resteasy.reactive.RestStreamElementType;

import com.redhat.example.clients.CloudEventEmitter;
import com.redhat.example.model.Task;
import com.redhat.example.model.Task.State;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/task")
public class TaskResource {

    @Inject
    CloudEventEmitter cloudEventEmitter;

    SubmissionPublisher<Task> publisher = new SubmissionPublisher<>();

    private Map<String, Task> tasks = Collections.synchronizedMap(new LinkedHashMap<String, Task>());

    @GET
    public Collection<Task> list(@QueryParam("state") Optional<String> state) {
        if (state.isPresent())
            return tasks.values().stream().filter(t -> t.state == State.valueOf(state.get())).toList();
        else
            return tasks.values();
    }

    @GET
    @Path("{id}")
    public Task getTask(@PathParam("id") String id) {
        return tasks.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(@HeaderParam("kogitoprocinstanceid") String wfId, Task task) {
        task.id = UUID.randomUUID().toString();
        task.dateTime = OffsetDateTime.now();
        task.correlation = wfId;
        tasks.put(task.id, task);
        publisher.submit(task);
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void complete(@PathParam("id") String id, String payload) {
        Task task = tasks.get(id);

        if (task == null)
            throw new NotFoundException("Unknown task: " + id);

        if (task.state == Task.State.OPEN) {
            cloudEventEmitter.send(task.correlation, "callbackEvent", payload);
        }
        task.state = Task.State.COMPLETE;
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") String id) {
        tasks.remove(id);
    }

    @Path("sse")
    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Task> stream() {
        return Multi.createBy().concatenating().<Task>streams(publisher);
    }
}
