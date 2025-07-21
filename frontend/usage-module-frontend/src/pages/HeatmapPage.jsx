import React, { useState, useEffect } from "react";
import CalendarHeatmap from "../components/charts/CalendarHeatmap";
import { analyticsApi } from "../services/api/analyticsApi";
import { format } from "date-fns";

const HeatmapPage = () => {
  const [selectedMonth, setSelectedMonth] = useState(format(new Date(), "yyyy-MM"));
  const [heatmapData, setHeatmapData] = useState({ heatmap: {} });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    analyticsApi.getMonthlyTrend(selectedMonth)
      .then(res => {
        const trend = res.data.dailyBookingTrend;
        setHeatmapData({ heatmap: trend });
      })
      .finally(() => setLoading(false));
  }, [selectedMonth]);

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Usage Heatmap</h2>
      <input
        type="month"
        value={selectedMonth}
        onChange={e => setSelectedMonth(e.target.value)}
        className="mb-4 p-2 border rounded"
      />
      {loading && <div>Loading...</div>}
      <CalendarHeatmap data={heatmapData} title="Usage Heatmap" selectedDate={selectedMonth + "-01"} />
    </div>
  );
};

export default HeatmapPage;
