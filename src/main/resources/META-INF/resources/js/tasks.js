const fmtOpts = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    second: 'numeric',
    timeZoneName: 'short'
};

const tasks = document.getElementById('tasks');
const taskList = document.getElementById('taskList');

function fetchTasks() {
    fetch('/task?state=OPEN')
    .then(response => response.json())
    .then(data => {
        if (data.length > 0)
            tasks.style.display = 'block';
        else
            tasks.style.display = 'none';

        taskList.innerHTML = '';
        data.forEach(task => appendTask(taskList, task));
    })
    .catch(error => {
        console.error('Error fetching tasks:', error);
        // Handle errors, e.g., display an error message to the user
    });
}

function appendTask(taskList, task) {
    const tr = document.createElement('tr');
    tr.id = task.id;
    let dateTime = new Date(task.dateTime);
    let dateTimeFormatted = dateTime.toLocaleDateString("en-uk", fmtOpts);

    tr.innerHTML = `<td>${task.role}</td><td>${dateTimeFormatted}</td><td>${task.description}</td><td>${task.input}</td><td><input type="text" class="result"></td><td><button class="button">Complete</button></td>`;

    let button = tr.querySelector(".button");
    button.addEventListener('click', () => {
        let inputF = tr.querySelector(".result");
        completeTask(task.id, inputF.value);
        taskList.removeChild(tr);
        if (taskList.childElementCount == 0)
            tasks.style.display = 'none';
    });
    taskList.appendChild(tr);
    if (taskList.childElementCount > 0) {
        tasks.style.display = 'block';
    }
}

function completeTask(taskId, result) {
    fetch(`/task/${taskId}`, {
        method: 'PATCH',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ result : result })
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// Fetch tasks on page load
fetchTasks();

var source = new EventSource("/task/sse");
source.onmessage = (event) => {
    var task = JSON.parse(event.data);
    if (task != null) { 
        console.log(task);
        appendTask(taskList, task);
    } else
        console.log("nothing");
};