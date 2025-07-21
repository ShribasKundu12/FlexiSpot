import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  BarChart3,
  Calendar,
  Clock,
  Users,
  TrendingUp,
  Settings,
  Home,
} from 'lucide-react';

const Sidebar = ({ isOpen, onToggle }) => {
  const location = useLocation();

  const navItems = [
    { path: '/', icon: Home, label: 'Dashboard' },
    { path: '/occupancy', icon: BarChart3, label: 'Occupancy Rate' },
    { path: '/peak-hours', icon: Clock, label: 'Peak Hours' },
    { path: '/heatmap', icon: Calendar, label: 'Usage Heatmap' },
    { path: '/trends', icon: TrendingUp, label: 'Monthly Trends' },
    { path: '/desks', icon: Settings, label: 'Desk Analytics' },
    { path: '/employees', icon: Users, label: 'Employee Insights' },
  ];

  return (
    <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform ${isOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform duration-300 lg:translate-x-0`}>
      <div className="flex items-center justify-center h-16 bg-blue-600">
        <h1 className="text-white text-xl font-bold">Analytics Dashboard</h1>
      </div>
      
      <nav className="mt-8">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center px-6 py-3 text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors ${
                isActive ? 'bg-blue-50 text-blue-600 border-r-2 border-blue-600' : ''
              }`}
            >
              <Icon className="w-5 h-5 mr-3" />
              {item.label}
            </Link>
          );
        })}
      </nav>
    </div>
  );
};

export default Sidebar;
