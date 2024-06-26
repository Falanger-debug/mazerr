let socket;
let mazeSize;


document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('mazeForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const size = document.getElementById('size').value;
    const speed = document.getElementById('speed').value;
    const algorithm = document.getElementById('algorithm').value;
    mazeSize = size;

    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.close();
      document.getElementById('maze').innerHTML = '';
    }

    generateMaze(size, speed, algorithm);
  });

  document.getElementById('pauseButton').addEventListener('click', () => {
    console.log('Pause button clicked');
    if (socket.readyState === WebSocket.OPEN) {
      console.log('Sending pause to server');
      socket.send('pause');
    }
  });

  document.getElementById('continueButton').addEventListener('click', () => {
    if (socket.readyState === WebSocket.OPEN) {
      socket.send('continue');
    }
  });

  document.getElementById('stopButton').addEventListener('click', () => {
    if (socket.readyState === WebSocket.OPEN) {
      socket.send('stop');
    }
  });
});


function generateMaze(size, speed, algorithm) {
  console.log(`Generating maze with size=${size}, speed=${speed}, algorithm=${algorithm}`); // Logowanie

  socket = new WebSocket(`ws://localhost:8080/maze-generation?size=${size}&speed=${speed}&algorithm=${algorithm}`);

  socket.onopen = function() {
    console.log('WebSocket is connected.');
    socket.send("start");
  };

  socket.onmessage = function(event) {
    const data = JSON.parse(event.data);
    if (Array.isArray(data)) {
      if (data[0].hasOwnProperty("x") && data[0].hasOwnProperty("y")) {
        drawSolution(data);
      } else {
        drawMaze(data, size);
      }
    }
  };

  socket.onclose = function() {
    console.log('WebSocket is closed.');
  };

  socket.onerror = function(error) {
    console.log('WebSocket error:', error);
  };
}

function drawMaze(mazeData, size) {
  const mazeContainer = document.getElementById('maze');
  mazeContainer.innerHTML = '';
  const cellSize = Math.min(20, 400 / size);
  mazeContainer.style.gridTemplateColumns = `repeat(${size}, ${cellSize}px)`;

  mazeData.forEach(row => {
    row.forEach(cell => {
      const cellDiv = document.createElement('div');
      cellDiv.classList.add('cell');
      cellDiv.style.width = `${cellSize}px`;
      cellDiv.style.height = `${cellSize}px`;
      cellDiv.setAttribute('data-x', cell.x);
      cellDiv.setAttribute('data-y', cell.y);
      if (cell.top) cellDiv.classList.add('wall-top');
      if (cell.bottom) cellDiv.classList.add('wall-bottom');
      if (cell.left) cellDiv.classList.add('wall-left');
      if (cell.right) cellDiv.classList.add('wall-right');
      if (cell.entry) cellDiv.classList.add('entry');
      if (cell.exit) cellDiv.classList.add('exit');
      mazeContainer.appendChild(cellDiv);
    });
  });
}

function drawSolution(solutionPath) {
  solutionPath.forEach((cell) => {
    const cellDiv = document.querySelector(`div[data-x='${cell.x}'][data-y='${cell.y}']`);
    if (cellDiv && !cellDiv.classList.contains('entry') && !cellDiv.classList.contains('exit')) {
      cellDiv.classList.add('solution');
    }
  });
}
