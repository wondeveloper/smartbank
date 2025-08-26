import React, { useEffect, useState } from "react";
import axios from "../services/api";

const Transactions = () => {
  const [transactions, setTransactions] = useState<any[]>([]);

  useEffect(() => {
    axios.get("/transactions").then((res) => setTransactions(res.data));
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">Transaction History</h2>
      <table className="w-full border">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 border">ID</th>
            <th className="p-2 border">From</th>
            <th className="p-2 border">To</th>
            <th className="p-2 border">Amount</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx) => (
            <tr key={tx.id}>
              <td className="p-2 border">{tx.id}</td>
              <td className="p-2 border">{tx.fromAccount}</td>
              <td className="p-2 border">{tx.toAccount}</td>
              <td className="p-2 border">₹{tx.amount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Transactions;
