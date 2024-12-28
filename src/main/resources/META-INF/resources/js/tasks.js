const fmtOpts = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    second: 'numeric',
    timeZoneName: 'short'
};

function fetchTasks() {
    fetch('/task?state=OPEN')
    .then(response => response.json())
    .then(data => {
        const tasks = document.getElementById('tasks');
        if (data.length > 0)
            tasks.style.display = 'block';
        else
            tasks.style.display = 'none';

        const taskList = document.getElementById('taskList');

        taskList.innerHTML = '';
        data.forEach(task => {
            const tr = document.createElement('tr');
            let dateTime = new Date(task.dateTime);
            let dateTimeFormatted = dateTime.toLocaleDateString("en-uk", fmtOpts);

            tr.innerHTML = `<td>${task.role}</td><td>${dateTimeFormatted}</td><td>${task.description}</td><td>${task.input}</td><td><input type="text" class="result"></td><td><button class="button">Complete</button></td>`;

            let button = tr.querySelector(".button");
            button.addEventListener('click', () => {
                let inputF = tr.querySelector(".result");
                completeTask(task.id, inputF.value);
            });
            taskList.appendChild(tr);
        });
    })
    .catch(error => {
        console.error('Error fetching tasks:', error);
        // Handle errors, e.g., display an error message to the user
    });
}

function completeTask(taskId, result) {
    fetch(`/task/${taskId}`, {
        method: 'PATCH',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ result : result })
    })
    .then(data => {
        console.log(data);
        fetchTasks();
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// Fetch tasks on page load
fetchTasks();