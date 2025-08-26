// userService.js

function getUserDetails() {
    apiRequest('http://localhost:8080/api/user/details')
        .then(data => {
            console.log('User details:', data);
        });
}
