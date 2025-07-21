
// At the top of your file, import axios:
import axios from "axios";

import React, { useState, useEffect } from 'react';
import { format } from 'date-fns';
import ProgressCircle from '../components/charts/ProgressCircle';
import CustomBarChart from '../components/charts/BarChart';
import CalendarHeatmap from '../components/charts/CalendarHeatmap';
import DataTable from '../components/ui/DataTable';
import TagPills from '../components/ui/TagPills';
import ToastButton from '../components/ui/ToastButton';
import { useOccupancyRate, usePeakHours, useSuggestedDays } from '../hooks/useAnalytics';
import { analyticsApi } from '../services/api/analyticsApi';

const Dashboard = () => {
  const [selectedDate, setSelectedDate] = useState(format(new Date(), 'yyyy-MM-dd'));
  const [heatmapData, setHeatmapData] = useState(null);
  const [monthlyTrends, setMonthlyTrends] = useState([]);
  const [topDesks, setTopDesks] = useState([]);
  const [inactiveEmployees, setInactiveEmployees] = useState([]);

  // Using custom hooks
  const { data: occupancyData, loading: occupancyLoading } = useOccupancyRate(selectedDate);
  const { data: peakHoursData, loading: peakHoursLoading } = usePeakHours(selectedDate);
  const { data: suggestedDaysData, loading: suggestedDaysLoading } = useSuggestedDays();

  // Fetch additional data
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Heatmap data
        const heatmapResponse = await analyticsApi.getHeatmapData(selectedDate);
        setHeatmapData(heatmapResponse.data);

        // Monthly trends
        const currentMonth = format(new Date(), 'yyyy-MM');
        const trendsResponse = await analyticsApi.getMonthlyTrend(currentMonth);
        const trendsArray = Object.entries(trendsResponse.data.dailyBookingTrend).map(([date, count]) => ({
          date: format(new Date(date), 'MMM dd'),
          bookings: count,
        }));
        setMonthlyTrends(trendsArray);

        // Top desks
        const desksResponse = await analyticsApi.getMostBookedDesks(5);
        setTopDesks(desksResponse.data.mostBookedDesks);

        // Inactive employees
        const inactiveResponse = await analyticsApi.getInactiveEmployees(selectedDate);
        setInactiveEmployees(inactiveResponse.data.inactiveEmployees.map(name => ({ name })));

      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      }
    };

    fetchData();
  }, [selectedDate]);

  // Transform peak hours data for chart
  const peakHoursChartData = peakHoursData?.peakHours?.map(hour => ({
    time: hour,
    bookings: peakHoursData.maxBookings || 0,
  })) || [];

  // Table columns for top desks
  const deskColumns = [
    { key: 'desk', label: 'Desk Name' },
    { key: 'bookings', label: 'Total Bookings' },
  ];

  // Table columns for inactive employees
  const employeeColumns = [
    { key: 'name', label: 'Employee Name' },
  ];

  // Add this function inside your Dashboard component:
const handleExportCSV = async () => {
  try {
    const response = await axios.get(
      `http://localhost:9090/analytics/export-summary-csv?date=${selectedDate}`,
      { responseType: "blob" }
    );
    // browser download trick
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `usage-summary-${selectedDate}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    alert("Error exporting CSV: " + (error.message || "Unknown error"));
  }
};

const handleSendReport = async () => {
  const userEmail = window.prompt("Enter recipient email address:");
  if (!userEmail) return;
  try {
    await axios.post(
      `http://localhost:9090/analytics/send-report?date=${selectedDate}&email=${userEmail}`
    );
    alert("Report sent!");
  } catch (err) {
    alert("Failed to send email: " + err.message);
  }
};


  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Workspace Analytics Dashboard</h1>
        <input
          type="date"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* First Row - Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          {occupancyLoading ? (
            <div className="flex justify-center items-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
          ) : (
            <ProgressCircle
              percentage={parseFloat(occupancyData?.occupancyRate?.replace('%', '')) || 0}
              title="Occupancy Rate"
              color="#3b82f6"
            />
          )}
        </div>

        <div className="card">
          {peakHoursLoading ? (
            <div className="flex justify-center items-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
          ) : (
            <CustomBarChart
              data={peakHoursChartData}
              title="Peak Hours"
              xKey="time"
              yKey="bookings"
              color="#10b981"
            />
          )}
        </div>

        <div className="card">
          {suggestedDaysLoading ? (
            <div className="flex justify-center items-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
          ) : (
            suggestedDaysData && (
              <TagPills
                data={suggestedDaysData}
                title="AI Suggested Days"
              />
            )
          )}
        </div>
      </div>

      {/* Second Row - Heatmap and Trends */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          {heatmapData ? (
            <CalendarHeatmap
              data={heatmapData}
              title="Usage Heatmap"
              selectedDate={selectedDate}
            />
          ) : (
            <div className="flex justify-center items-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
          )}
        </div>

        <div className="card">
          <CustomBarChart
            data={monthlyTrends}
            title="Monthly Booking Trends"
            xKey="date"
            yKey="bookings"
            color="#8b5cf6"
          />
        </div>
      </div>

      {/* Third Row - Tables and Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <DataTable
            data={topDesks}
            columns={deskColumns}
            title="Most Booked Desks"
            showRanking={true}
          />
        </div>

        <div className="card">
          <DataTable
            data={inactiveEmployees}
            columns={employeeColumns}
            title="Inactive Employees"
          />
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex justify-center space-x-4">
<ToastButton
  onAction={handleSendReport}
  title="Send Email Report"
  icon={({ className }) => <span className={className}>📧</span>}
/>
<ToastButton
  onAction={handleExportCSV}
  title="Export CSV"
  variant="secondary"
  icon={({ className }) => <span className={className}>📊</span>}
/>

</div>

    </div>
  );
};





export default Dashboard;
