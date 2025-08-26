// authService.js

function loginUser(authRequest) {
    fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(authRequest)
    })
    .then(response => response.json())
    .then(data => {
        // Save access and refresh tokens
        localStorage.setItem('accessToken', data.credentials.accessToken);
        localStorage.setItem('refreshToken', data.credentials.refreshToken);
    })
    .catch(error => {
        console.error('Login failed:', error);
    });
}

const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem('refreshToken');
  
    if (!refreshToken) {
      console.error("No refresh token available.");
      return null;
    }
  
    try {
      // Make a request to the refresh token API
      const response = await axios.post("http://localhost:8080/api/auth/refresh", { refreshToken });
  
      // Store the new access token in localStorage
      const newAccessToken = response.data.accessToken;
      localStorage.setItem('accessToken', newAccessToken);
  
      // Return the new access token
      return newAccessToken;
    } catch (error) {
      console.error("Failed to refresh the access token:", error);
      return null;
    }
  };
  

function logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    // Additional cleanup actions
}
