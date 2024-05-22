document.querySelector('form').addEventListener('submit', function (event) {
    event.preventDefault(); // prevent the form from submitting normally

    let width = document.querySelector('#width').value;
    let height = document.querySelector('#height').value;
    let method = document.querySelector('#method').value;

    fetch('/generate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            width: width,
            height: height,
            method: method
        }),
    })
        .then(response => response.ok ? console.log('Maze generated successfully') : console.error('Error generating maze'))
        .catch((error) => {
            console.error('Error:', error);
        });
});
