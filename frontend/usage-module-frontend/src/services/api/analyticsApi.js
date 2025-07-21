import axios from 'axios';

const API_BASE_URL = 'http://localhost:9090/analytics';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API service functions
export const analyticsApi = {
  // Get occupancy rate for a specific date
  getOccupancyRate: (date) => 
    apiClient.get(`/occupancy-rate?date=${date}`),
  
  // Get peak hours for a specific date
  getPeakHours: (date) => 
    apiClient.get(`/peak-hours?date=${date}`),
  
  // Get usage heatmap data
  getHeatmapData: (date) => 
    apiClient.get(`/heatmap?date=${date}`),
  
  // Get monthly trends
  getMonthlyTrend: (month) => 
    apiClient.get(`/monthly-trend?month=${month}`),
  
  // Get most booked desks
  getMostBookedDesks: (top = 5) => 
    apiClient.get(`/most-booked-desks?top=${top}`),
  
  // Get inactive employees
  getInactiveEmployees: (since) => 
    apiClient.get(`/employee-inactive?since=${since}`),
  
  // Get suggested best days
  getSuggestedDays: () => 
    apiClient.get('/suggestions/best-days'),
  
  // Get daily summary
  getDailySummary: (date) => 
    apiClient.get(`/daily-summary?date=${date}`),
  
  // Export CSV
  exportSummaryCSV: (date) => 
    apiClient.get(`/export-summary-csv?date=${date}`, {
      responseType: 'blob'
    }),
};

export default analyticsApi;
