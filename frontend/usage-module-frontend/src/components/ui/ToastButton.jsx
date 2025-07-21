import React from 'react';
import toast from 'react-hot-toast';
import { Mail, Download } from 'lucide-react';

const ToastButton = ({ onAction, title, icon: Icon = Mail, variant = 'primary' }) => {
  const handleClick = async () => {
    const loadingToast = toast.loading('Processing request...');
    
    try {
      await onAction();
      toast.success('Action completed successfully!', {
        id: loadingToast,
        duration: 3000,
      });
    } catch (error) {
      toast.error('An error occurred. Please try again.', {
        id: loadingToast,
        duration: 4000,
      });
    }
  };

  const getButtonStyles = () => {
    const baseStyles = 'inline-flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2';
    
    if (variant === 'primary') {
      return `${baseStyles} bg-blue-600 hover:bg-blue-700 text-white focus:ring-blue-500`;
    }
    
    return `${baseStyles} bg-gray-200 hover:bg-gray-300 text-gray-800 focus:ring-gray-500`;
  };

  return (
    <button
      onClick={handleClick}
      className={getButtonStyles()}
    >
      <Icon className="w-4 h-4 mr-2" />
      {title}
    </button>
  );
};

export default ToastButton;
