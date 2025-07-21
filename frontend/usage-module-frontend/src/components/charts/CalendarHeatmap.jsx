import React from 'react';
import { format, eachDayOfInterval, startOfMonth, endOfMonth } from 'date-fns';

const CalendarHeatmap = ({ data, title, selectedDate }) => {
  const startDate = startOfMonth(new Date(selectedDate));
  const endDate = endOfMonth(new Date(selectedDate));
  
  const days = eachDayOfInterval({ start: startDate, end: endDate });
  
  const getIntensity = (date) => {
    const dateStr = format(date, 'yyyy-MM-dd');
    const dayData = data?.heatmap?.[dateStr] || 0;
    
    if (dayData === 0) return 0;
    if (dayData <= 2) return 1;
    if (dayData <= 4) return 2;
    if (dayData <= 6) return 3;
    return 4;
  };

  const getColor = (intensity) => {
    const colors = [
      '#ebedf0', // 0 - no activity
      '#9be9a8', // 1 - low activity
      '#40c463', // 2 - medium activity
      '#30a14e', // 3 - high activity
      '#216e39', // 4 - very high activity
    ];
    return colors[intensity];
  };

  return (
    <div className="w-full">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">{title}</h3>
      <div className="grid grid-cols-7 gap-1">
        {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day) => (
          <div key={day} className="text-xs text-gray-500 p-1 text-center">
            {day}
          </div>
        ))}
        {days.map((day, index) => {
          const intensity = getIntensity(day);
          const color = getColor(intensity);
          
          return (
            <div
              key={index}
              className="w-4 h-4 rounded-sm border border-gray-200 cursor-pointer hover:border-gray-400 transition-colors"
              style={{ backgroundColor: color }}
              title={`${format(day, 'MMM dd')}: ${data?.heatmap?.[format(day, 'yyyy-MM-dd')] || 0} bookings`}
            />
          );
        })}
      </div>
      <div className="flex items-center justify-between mt-4 text-xs text-gray-500">
        <span>Less</span>
        <div className="flex space-x-1">
          {[0, 1, 2, 3, 4].map((level) => (
            <div
              key={level}
              className="w-3 h-3 rounded-sm border border-gray-200"
              style={{ backgroundColor: getColor(level) }}
            />
          ))}
        </div>
        <span>More</span>
      </div>
    </div>
  );
};

export default CalendarHeatmap;
