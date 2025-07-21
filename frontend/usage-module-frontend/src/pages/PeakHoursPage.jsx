// src/pages/PeakHoursPage.jsx
import React, { useState } from "react";
import { usePeakHours } from "../hooks/useAnalytics";
import { format } from "date-fns";
import CustomBarChart from "../components/charts/BarChart";

const PeakHoursPage = () => {
  const [selectedDate, setSelectedDate] = useState(format(new Date(), "yyyy-MM-dd"));
  const { data, loading, error } = usePeakHours(selectedDate);

  const peakData = data?.peakHours?.map(hour => ({
    time: hour,
    count: data.maxBookings,
  })) || [];

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Peak Hours</h2>
      <input
        type="date"
        value={selectedDate}
        onChange={e => setSelectedDate(e.target.value)}
        className="mb-4 p-2 border rounded"
      />
      {loading && <div>Loading...</div>}
      {error && <div className="text-red-500">{error}</div>}
      {data && (
        <CustomBarChart
          data={peakData}
          title="Peak Hours"
          xKey="time"
          yKey="count"
          color="#10b981"
        />
      )}
    </div>
  );
};

export default PeakHoursPage;
