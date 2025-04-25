import { useState, useEffect } from 'react';
import { useAuth } from './useAuth';
import * as api from '../services/api';

export function useFetch(fn, ...params) {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    fn(...params.concat(token))
      .then(res => mounted && setData(res))
      .catch(err => mounted && setError(err))
      .finally(() => mounted && setLoading(false));
    return () => { mounted = false; };
  }, [fn, token, ...params]);

  return { data, error, loading };
}