import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

interface AuthRequest {
  email: string;
  password: string;
  confirmPassword: string;
}

interface RegisterRequest {
  firstName: string;
  lastName: string;
  emailId: string;
  authRequest: AuthRequest;
}

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState<RegisterRequest>({
    firstName: "",
    lastName: "",
    emailId: "",
    authRequest: {
      email: "",
      password: "",
      confirmPassword: ""
    }
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = e.target;
    if (name === "password" || name === "confirmPassword") {
      setForm({
        ...form,
        authRequest: {
          ...form.authRequest,
          [name]: value
        }
      });
    } else {
      setForm({
        ...form,
        [name]: value
      });
    }
    if (name === "emailId") {
      setForm(prev => ({
        ...prev,
        [name]: value,                  // Update emailId
        authRequest: {
          ...prev.authRequest,
          email: value                // Also update authRequest.emailId
        }
      }));
    }
    
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.authRequest.password !== form.authRequest.confirmPassword) {
      toast.error("Passwords do not match");
      return;
    }

    try {
      const res = await axios.post("http://localhost:8080/api/auth/register", form);
      const {
        accessToken,
        refreshToken,
        firstName,
        email,
        refreshTokenExpiresAt
      } = res.data;

      // Set cookies (or use localStorage if preferred)
      document.cookie = `accessToken=${accessToken}; path=/; max-age=86400`;
      document.cookie = `refreshToken=${refreshToken}; path=/; max-age=86400`;
      document.cookie = `userFirstName=${firstName}; path=/; max-age=86400`;
      document.cookie = `userEmail=${email}; path=/; max-age=86400`;

      toast.success("Registration successful!");
      navigate("/dashboard");
    } catch (error: any) {
      console.error(error);
      toast.error(error?.response?.data?.message || "Registration failed");
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border shadow">
      <h2 className="text-2xl font-bold mb-4">Register</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          name="firstName"
          placeholder="First Name"
          value={form.firstName}
          onChange={handleChange}
          className="w-full px-3 py-2 border rounded"
          required
        />
        <input
          type="text"
          name="lastName"
          placeholder="Last Name"
          value={form.lastName}
          onChange={handleChange}
          className="w-full px-3 py-2 border rounded"
        />
        <input
          type="email"
          name="emailId"
          placeholder="Email"
          value={form.emailId}
          onChange={handleChange}
          className="w-full px-3 py-2 border rounded"
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.authRequest.password}
          onChange={handleChange}
          className="w-full px-3 py-2 border rounded"
          required
        />
        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          value={form.authRequest.confirmPassword}
          onChange={handleChange}
          className="w-full px-3 py-2 border rounded"
          required
        />
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
        >
          Register
        </button>
      </form>
    </div>
  );
}
