// apiInterceptor.js

function apiRequest(url, options = {}) {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    // Set Authorization header with access token
    options.headers = options.headers || {};
    options.headers['Authorization'] = `Bearer ${accessToken}`;

    return fetch(url, options)
        .then(response => {
            if (response.status === 401) {
                // If access token expired (401 Unauthorized), refresh the token
                return refreshAccessToken(refreshToken)
                    .then(newAccessToken => {
                        // Retry the request with the new access token
                        options.headers['Authorization'] = `Bearer ${newAccessToken}`;
                        return fetch(url, options);
                    });
            }
            return response;
        })
        .then(response => response.json())
        .catch(error => {
            console.error('API request failed:', error);
        });
}

function refreshAccessToken(refreshToken) {
    return fetch('http://localhost:8080/api/auth/refresh-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            refreshToken: refreshToken
        })
    })
    .then(response => response.json())
    .then(data => {
        const newAccessToken = data.credentials.accessToken;
        // Save the new access token
        localStorage.setItem('accessToken', newAccessToken);
        return newAccessToken;
    })
    .catch(error => {
        console.error('Token refresh failed:', error);
        throw error;
    });
}
