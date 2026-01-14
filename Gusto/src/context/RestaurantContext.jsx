import React, { createContext, useState, useEffect } from 'react';
import { getRestaurantSettings } from '../services/api';

export const RestaurantContext = createContext({ restaurantInfo: null, loading: true });

export const RestaurantProvider = ({ children }) => {
  const [restaurantInfo, setRestaurantInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      try {
        const resp = await getRestaurantSettings();
        if (mounted) setRestaurantInfo(resp.data);
      } catch (e) {
        console.warn('Restaurant settings load failed', e);
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, []);

  return (
    <RestaurantContext.Provider value={{ restaurantInfo, loading }}>
      {children}
    </RestaurantContext.Provider>
  );
};

export default RestaurantContext;
