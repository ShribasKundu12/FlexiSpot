import React from 'react';
import { ChevronUp, ChevronDown } from 'lucide-react';

const DataTable = ({ data, columns, title, showRanking = false }) => {
  const getRankingColor = (index) => {
    if (index === 0) return 'text-yellow-600 bg-yellow-50';
    if (index === 1) return 'text-gray-600 bg-gray-50';
    if (index === 2) return 'text-orange-600 bg-orange-50';
    return 'text-blue-600 bg-blue-50';
  };

  return (
    <div className="w-full">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">{title}</h3>
      <div className="overflow-x-auto">
        <table className="min-w-full table-auto">
          <thead>
            <tr className="bg-gray-50">
              {showRanking && (
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Rank
                </th>
              )}
              {columns.map((column) => (
                <th
                  key={column.key}
                  className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((row, index) => (
              <tr key={index} className="hover:bg-gray-50">
                {showRanking && (
                  <td className="px-4 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getRankingColor(index)}`}>
                      #{index + 1}
                    </span>
                  </td>
                )}
                {columns.map((column) => (
                  <td
                    key={column.key}
                    className="px-4 py-4 whitespace-nowrap text-sm text-gray-900"
                  >
                    {column.render ? column.render(row[column.key], row) : row[column.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DataTable;
