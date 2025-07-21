import React, { useState } from "react";
import { useOccupancyRate } from "../hooks/useAnalytics";
import { format } from "date-fns";
import ProgressCircle from "../components/charts/ProgressCircle";

const OccupancyPage = () => {
  const [selectedDate, setSelectedDate] = useState(format(new Date(), "yyyy-MM-dd"));
  const { data, loading, error } = useOccupancyRate(selectedDate);

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Occupancy Rate</h2>
      <input
        type="date"
        value={selectedDate}
        onChange={e => setSelectedDate(e.target.value)}
        className="mb-4 p-2 border rounded"
      />

      {loading && <div>Loading...</div>}
      {error && <div className="text-red-500">{error}</div>}
      {data && (
        <ProgressCircle
          percentage={parseFloat((data.occupancyRate || "0").replace("%", ""))}
          title="Occupancy Rate"
        />
      )}
    </div>
  );
};

export default OccupancyPage;
