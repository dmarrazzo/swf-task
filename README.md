# External Task list for Serverless Workflow

This project demonstrates an integration strategy for delegating tasks to an external system for processing. The workflow seamlessly incorporates the results returned by the external system to dynamically adjust subsequent execution steps.

The core of this project is a single-page application (SPA) that simulates the behavior of the external system. This SPA features a user-friendly task list interface. Users can select a task from the list and then provide a string-based result upon completion. This simulated environment allows for thorough testing and evaluation of the task delegation and result handling mechanisms within the workflow.

> [!CAUTION]
> This application is a Proof of Concept and deliberately designed not to scale out.
> It must run as a single instance and isn't resilient to restart.
> In fact, the data store and event distribution (SSE) are kept in memory.

## Run the demo locally

### Launch Serverless Workflow engine inside a container

Launch the Serverless Workflow engine:

```sh
podman kube play podman-kube-play.yaml
```

Alternatively, you can use this command:

```sh
podman run --network=host --rm --name swf-devmode -v ./swf:/home/kogito/serverless-workflow-project/src/main/resources:Z registry.redhat.io/openshift-serverless-1/logic-swf-devmode-rhel8:1.34.0
```
To inspect the engine logs:

```sh
podman logs -f swf-devmode-pod-swf-devmode
```

To shutdown the engine:

```sh
podman kube down podman-kube-play.yaml
```

### Launch the task list in dev mode

```sh
./mvnw quarkus:dev -Ddebug=5006 -DskipTests
```

> [!NOTE]
> to avoid port conflicts, the application uses the port `8081` for HTTP protocol and `5006` for the debugging agent.

## Showcase

1. **Open** the Serverless Workflow [web console](http://localhost:8080/q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui/workflows).

2. **Select** `Workflow Definitions` tab

   - Optionally, set input information to be passed down to the external task application. E.g. `{ "input": "approve the related documentation" }` 

3. **Start** `tasker` workflow definition using the _"play"_ icon.

   - Check the workflow instance status: it's waiting for the `external task` execution.

4. **Open** the [task list application](http://localhost:8081/)

   - **Type** `ok` in the _result_ field.
   - **Click** the `complete` button.

5. In the Serverless Workflow web console, **check** the workflow progress: the external task is completed and the workflow follow the correct path.

6. Optionally, **check** the Serverless Workflow log to verify the result of the external task.

## Powered by Quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.
