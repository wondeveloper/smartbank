import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "../services/api";

const AccountDetails = () => {
  const { id } = useParams();
  const [account, setAccount] = useState<any>(null);

  useEffect(() => {
    axios.get(`/accounts/${id}`).then((res) => setAccount(res.data));
  }, [id]);

  if (!account) return <p className="p-4">Loading...</p>;

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">Account #{account.id}</h2>
      <p>Owner: {account.owner}</p>
      <p>Balance: ₹{account.balance}</p>
      <p>Status: {account.status}</p>
    </div>
  );
};

export default AccountDetails;
