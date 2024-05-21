document.addEventListener('DOMContentLoaded', (event) => {
    document.getElementById('mazeForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const mazeSize = document.getElementById('mazeSize').value;
        const mazeMethod = document.getElementById('mazeMethod').value;
        generateMaze(mazeSize, mazeMethod);
    });

    function generateMaze(size, method) {
    // Send a request to the server to generate the maze.
    fetch('/generate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({size: size, method: method})
    })
        .then(response => response.json())
        .then(maze => {
            if (maze && maze.length > 0) {
                displayMaze(maze);
            } else {
                console.error('Error: Received invalid maze data');
            }
        })
        .catch(error => console.error('Error:', error));
}

    function displayMaze(maze) {
    // Create a table to represent the maze.
    const table = document.createElement('table');

    // Append the table to the mazeContainer.
    const mazeContainer = document.getElementById('mazeContainer');
    mazeContainer.appendChild(table);

    const tableWidth = table.offsetWidth;
    const cellSize = tableWidth / maze[0].length;

    for (let i = 0; i < maze.length; i++) {
        const row = document.createElement('tr');

        for (let j = 0; j < maze[i].length; j++) {
            const cell = document.createElement('td');
            cell.style.width = `${cellSize}px`;
            cell.style.height = `${cellSize}px`;
            cell.style.backgroundColor = maze[i][j] === 0 ? 'white' : 'black';
            row.appendChild(cell);
        }

        table.appendChild(row);
    }

    // Remove the old maze from the page, if it exists.
    const oldMaze = document.querySelector('table');
    if (oldMaze) {
        oldMaze.remove();
    }
}
});