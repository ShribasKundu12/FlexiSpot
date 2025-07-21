// src/pages/DesksPage.jsx
import React, { useState, useEffect } from "react";
import DataTable from "../components/ui/DataTable";
import { analyticsApi } from "../services/api/analyticsApi";

const DesksPage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    analyticsApi.getMostBookedDesks(10)
      .then(res => setData(res.data.mostBookedDesks))
      .finally(() => setLoading(false));
  }, []);

  const columns = [
    { key: 'desk', label: 'Desk Name' },
    { key: 'bookings', label: 'Total Bookings' }
  ];

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Most Booked Desks (Leaderboard)</h2>
      {loading && <div>Loading...</div>}
      <DataTable data={data} columns={columns} title="Most Booked Desks" showRanking={true} />
    </div>
  );
};

export default DesksPage;
