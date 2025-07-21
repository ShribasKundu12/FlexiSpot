import { useState, useEffect } from 'react';
import { analyticsApi } from '../services/api/analyticsApi';
import toast from 'react-hot-toast';

export const useAnalytics = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = async (apiCall, onSuccess, onError) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await apiCall();
      onSuccess(response.data);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'An error occurred';
      setError(errorMessage);
      if (onError) onError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return { loading, error, fetchData };
};

// Specific hooks for each data type
export const useOccupancyRate = (date) => {
  const [data, setData] = useState(null);
  const { loading, error, fetchData } = useAnalytics();

  useEffect(() => {
    if (date) {
      fetchData(
        () => analyticsApi.getOccupancyRate(date),
        setData
      );
    }
  }, [date]);

  return { data, loading, error };
};

export const usePeakHours = (date) => {
  const [data, setData] = useState(null);
  const { loading, error, fetchData } = useAnalytics();

  useEffect(() => {
    if (date) {
      fetchData(
        () => analyticsApi.getPeakHours(date),
        setData
      );
    }
  }, [date]);

  return { data, loading, error };
};

export const useSuggestedDays = () => {
  const [data, setData] = useState(null);
  const { loading, error, fetchData } = useAnalytics();

  useEffect(() => {
    fetchData(
      () => analyticsApi.getSuggestedDays(),
      setData
    );
  }, []);

  return { data, loading, error };
};
