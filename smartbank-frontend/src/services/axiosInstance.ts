import axios from 'axios';

// Create an instance of Axios
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',  // Your backend base URL
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to add the access token to headers
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle 401 (unauthorized) error and refresh the token
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // If the error is due to token expiration (401) and we haven't retried already
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Attempt to refresh the token
        const newToken = await refreshAccessToken();
        
        // If token refresh is successful, retry the original request with the new token
        if (newToken) {
          axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
          return axiosInstance(originalRequest);
        }
      } catch (err) {
        // Handle any error while refreshing the token (e.g., navigate to login page)
        console.error('Failed to refresh token', err);
        // You might want to log out the user or redirect to the login page
        // redirectToLogin();
      }
    }

    return Promise.reject(error);
  }
);

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

export default axiosInstance;
