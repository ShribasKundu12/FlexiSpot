// src/pages/TrendsPage.jsx
import React, { useState, useEffect } from "react";
import { format } from "date-fns";
import CustomBarChart from "../components/charts/BarChart";
import { analyticsApi } from "../services/api/analyticsApi";

const TrendsPage = () => {
  const [selectedMonth, setSelectedMonth] = useState(format(new Date(), "yyyy-MM"));
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    analyticsApi.getMonthlyTrend(selectedMonth)
      .then(res => {
        const arr = Object.entries(res.data.dailyBookingTrend).map(([date, count]) => ({
          date,
          bookings: count,
        }));
        setData(arr);
      })
      .finally(() => setLoading(false));
  }, [selectedMonth]);

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Monthly Booking Trends</h2>
      <input
        type="month"
        value={selectedMonth}
        onChange={e => setSelectedMonth(e.target.value)}
        className="mb-4 p-2 border rounded"
      />
      {loading && <div>Loading...</div>}
      <CustomBarChart
        data={data}
        title="Monthly Booking Trend"
        xKey="date"
        yKey="bookings"
        color="#8b5cf6"
      />
    </div>
  );
};

export default TrendsPage;
