import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/layout/Layout';
import Dashboard from './pages/Dashboard';
import OccupancyPage from './pages/OccupancyPage';
import PeakHoursPage from './pages/PeakHoursPage';
import HeatmapPage from './pages/HeatmapPage';
import TrendsPage from './pages/TrendsPage';
import DesksPage from './pages/DesksPage';
import EmployeesPage from './pages/EmployeesPage';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Dashboard />} />
            <Route path="occupancy" element={<OccupancyPage />} />
                      <Route path="peak-hours" element={<PeakHoursPage />} />
          <Route path="heatmap" element={<HeatmapPage />} />
          <Route path="trends" element={<TrendsPage />} />
          <Route path="desks" element={<DesksPage />} />
          <Route path="employees" element={<EmployeesPage />} />
          {/* Optionally add a "not found" fallback */}
          <Route path="*" element={<div>Page not found</div>} />
            {/* Add more routes as needed */}
          </Route>
        </Routes>
        
        {/* Toast notifications */}
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#363636',
              color: '#fff',
            },
            success: {
              style: {
                background: '#10b981',
              },
            },
            error: {
              style: {
                background: '#ef4444',
              },
            },
          }}
        />
      </div>
    </Router>
  );
}

export default App;
