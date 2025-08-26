import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import axios from "../services/api";
import getSetCookie  from "../components/Cookie"

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8080/api/auth/login", { email, password });
      // Destructure the response based on the new structure
      const { accessToken, refreshToken, hash, accessTokenExpiresAt, refreshTokenExpiresAt } = res.data.credentials;
      const { firstName, email: userEmail } = res.data.userDetails;
  
      // Store tokens and other relevant information in localStorage
      getSetCookie.setCookie("accessToken", accessToken)

      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("hash", hash);
      localStorage.setItem("accessTokenExpiresAt", accessTokenExpiresAt);
      localStorage.setItem("refreshTokenExpiresAt", refreshTokenExpiresAt);
  
      // Optionally, store user details if needed
      localStorage.setItem("userFirstName", firstName);
      localStorage.setItem("userEmail", userEmail);
  
      toast.success("Login successful");
      navigate("/dashboard");
    } catch (err) {
      toast.error("Invalid credentials");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-tr from-blue-100 to-purple-200 flex items-center justify-center px-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
        <h2 className="text-3xl font-bold text-center mb-6 text-blue-600">
          SmartBank Login
        </h2>
        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email address
            </label>
            <input
              type="email"
              id="email"
              className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <input
              type="password"
              id="password"
              className="w-full mt-1 px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-md transition duration-200"
          >
            Sign in
          </button>
        </form>

        <p className="mt-4 text-center text-sm text-gray-500">
          Don’t have an account?{" "}
          <a href="/register" className="text-blue-600 hover:underline">
            Register here
          </a>
        </p>
      </div>
    </div>
  );
};

export default Login;
