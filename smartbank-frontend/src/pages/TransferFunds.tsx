import React, { useState } from "react";
import axios from "../services/api";
import toast from "react-hot-toast";

const TransferFunds = () => {
  const [form, setForm] = useState({ fromAccount: "", toAccount: "", amount: "" });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axios.post("/transactions/transfer", form);
      toast.success("Transfer successful");
    } catch {
      toast.error("Transfer failed");
    }
  };

  return (
    <div className="p-6 max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">Transfer Funds</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="fromAccount"
          placeholder="From Account ID"
          className="w-full p-2 border mb-4 rounded"
          onChange={handleChange}
        />
        <input
          name="toAccount"
          placeholder="To Account ID"
          className="w-full p-2 border mb-4 rounded"
          onChange={handleChange}
        />
        <input
          name="amount"
          type="number"
          placeholder="Amount"
          className="w-full p-2 border mb-4 rounded"
          onChange={handleChange}
        />
        <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded">
          Transfer
        </button>
      </form>
    </div>
  );
};

export default TransferFunds;
