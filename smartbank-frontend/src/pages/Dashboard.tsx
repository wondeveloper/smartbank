import React from "react";

export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-5xl mx-auto bg-white rounded-xl shadow p-8">
        <h1 className="text-3xl font-bold mb-4 text-blue-700">Welcome to SmartBank</h1>
        <p className="text-gray-600">
          This is your dashboard. You’ll see your account summary, recent activity,
          and more here.
        </p>
      </div>
    </div>
  );
}
