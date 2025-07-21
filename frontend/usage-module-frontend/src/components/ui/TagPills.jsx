import React from 'react';
import { Calendar, Clock } from 'lucide-react';

const TagPills = ({ data, title }) => {
  const getTagColor = (day) => {
    const colors = {
      'MONDAY': 'bg-blue-100 text-blue-800',
      'TUESDAY': 'bg-green-100 text-green-800',
      'WEDNESDAY': 'bg-purple-100 text-purple-800',
      'THURSDAY': 'bg-yellow-100 text-yellow-800',
      'FRIDAY': 'bg-red-100 text-red-800',
      'SATURDAY': 'bg-indigo-100 text-indigo-800',
      'SUNDAY': 'bg-pink-100 text-pink-800',
    };
    return colors[day] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="w-full">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">{title}</h3>
      <div className="space-y-4">
        <div className="flex items-center space-x-2 text-sm text-gray-600">
          <Clock className="w-4 h-4" />
          <span>{data.basedOn}</span>
        </div>
        <div className="flex flex-wrap gap-2">
          {data.suggestedDays.map((day, index) => (
            <span
              key={index}
              className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${getTagColor(day)}`}
            >
              <Calendar className="w-4 h-4 mr-1" />
              {day}
            </span>
          ))}
        </div>
        <p className="text-sm text-gray-500">
          These are the recommended days for workspace booking based on historical usage patterns.
        </p>
      </div>
    </div>
  );
};

export default TagPills;
