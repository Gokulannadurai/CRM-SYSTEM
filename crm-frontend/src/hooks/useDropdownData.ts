import { useState, useEffect, useCallback } from 'react';
// Removed unused import
import { customerApi } from '../services/customerApi';
import { salesApi } from '../services/salesApi';
import { userApi } from '../services/userApi';
import { roleApi } from '../services/roleApi';

interface DropdownOption {
  id: number;
  name: string;
}

interface UserDropdownOption extends DropdownOption {
  firstName?: string;
  lastName?: string;
  username?: string;
  user_id?: number;
}

interface LeadDropdownOption {
  id: number;
  title: string;
}

interface DropdownData {
  users: UserDropdownOption[];
  customers: DropdownOption[];
  leads: LeadDropdownOption[];
  roles: DropdownOption[];
  loading: {
    users: boolean;
    customers: boolean;
    leads: boolean;
    roles: boolean;
  };
  error: {
    users: string | null;
    customers: string | null;
    leads: string | null;
    roles: string | null;
  };
}

// Global cache for dropdown data to prevent multiple API calls across components
let globalDropdownCache: DropdownData | null = null;
let globalLoadingState = {
  users: false,
  customers: false,
  leads: false,
  roles: false,
};

export const useDropdownData = () => {
  const [dropdownData, setDropdownData] = useState<DropdownData>(() => {
    // Initialize with cached data if available
    if (globalDropdownCache) {
      return globalDropdownCache;
    }
    
    return {
    users: [],
    customers: [],
    leads: [],
      roles: [],
    loading: {
      users: false,
      customers: false,
      leads: false,
        roles: false,
    },
    error: {
      users: null,
      customers: null,
      leads: null,
        roles: null,
      },
    };
  });

  // Update global cache when local state changes
  useEffect(() => {
    globalDropdownCache = dropdownData;
  }, [dropdownData]);

  // Update global loading state
  useEffect(() => {
    globalLoadingState = dropdownData.loading;
  }, [dropdownData.loading]);

  const fetchUsers = useCallback(async () => {
    // Check if data is already loaded and not in error state
    if (dropdownData.users.length > 0 && !dropdownData.error.users) {
      console.log('Skipping users fetch - data already loaded');
      return;
    }
    
    // Use a simple flag to prevent multiple simultaneous requests
    if (globalLoadingState.users) {
      console.log('Skipping users fetch - already loading');
      return;
    }

    console.log('Fetching users for dropdown...');
    globalLoadingState.users = true;
    
    setDropdownData(prev => ({
      ...prev,
      loading: { ...prev.loading, users: true },
      error: { ...prev.error, users: null },
    }));

    try {
      const users = await userApi.getUsersForDropdown();
      console.log('Users fetched successfully:', users);
      
      const userOptions = users.map((user: any) => ({
        id: user.id,
        name: user.name || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username || 'Unknown User',
        firstName: user.firstName || user.first_name,
        lastName: user.lastName || user.last_name,
        username: user.username,
        user_id: user.user_id,
      }));
      
      console.log('Mapped user options:', userOptions);
      
      setDropdownData(prev => ({
        ...prev,
        users: userOptions,
        loading: { ...prev.loading, users: false },
      }));
    } catch (error) {
      console.error('Error fetching users:', error);
      
      setDropdownData(prev => ({
        ...prev,
        users: [],
        loading: { ...prev.loading, users: false },
        error: { ...prev.error, users: 'Failed to fetch users' },
      }));
    } finally {
      globalLoadingState.users = false;
    }
  }, [dropdownData.users.length, dropdownData.error.users]);

  const fetchCustomers = useCallback(async () => {
    // Check if data is already loaded
    if (dropdownData.customers.length > 0 && !dropdownData.error.customers) {
      console.log('Skipping customers fetch - data already loaded');
      return;
    }
    
    // Use a simple flag to prevent multiple simultaneous requests
    if (globalLoadingState.customers) {
      console.log('Skipping customers fetch - already loading');
      return;
    }

    console.log('Fetching customers for dropdown...');
    globalLoadingState.customers = true;
    setDropdownData(prev => ({
      ...prev,
      loading: { ...prev.loading, customers: true },
      error: { ...prev.error, customers: null },
    }));

    try {
      const response = await customerApi.getCustomersForDropdown();
      if (response.success) {
        console.log('Customers fetched successfully:', response.data.length, 'customers');
        setDropdownData(prev => ({
          ...prev,
          customers: response.data,
          loading: { ...prev.loading, customers: false },
        }));
      } else {
        throw new Error(response.message || 'Failed to fetch customers');
      }
    } catch (error) {
      console.error('Error fetching customers:', error);
      setDropdownData(prev => ({
        ...prev,
        loading: { ...prev.loading, customers: false },
        error: { ...prev.error, customers: error instanceof Error ? error.message : 'Failed to fetch customers' },
      }));
    } finally {
      globalLoadingState.customers = false;
    }
  }, []); // Remove dependencies to prevent recreation

  const fetchLeads = useCallback(async () => {
    // Check if data is already loaded
    if (dropdownData.leads.length > 0 && !dropdownData.error.leads) {
      console.log('Skipping leads fetch - data already loaded');
      return;
    }
    
    // Use a simple flag to prevent multiple simultaneous requests
    if (dropdownData.loading.leads) {
      console.log('Skipping leads fetch - already loading');
      return;
    }

    setDropdownData(prev => ({
      ...prev,
      loading: { ...prev.loading, leads: true },
      error: { ...prev.error, leads: null },
    }));

    try {
      const response = await salesApi.getLeadsForDropdown();
      if (response.success) {
        setDropdownData(prev => ({
          ...prev,
          leads: response.data,
          loading: { ...prev.loading, leads: false },
        }));
      } else {
        throw new Error(response.message || 'Failed to fetch leads');
      }
    } catch (error) {
      setDropdownData(prev => ({
        ...prev,
        loading: { ...prev.loading, leads: false },
        error: { ...prev.error, leads: error instanceof Error ? error.message : 'Failed to fetch leads' },
      }));
    }
  }, []); // Remove dependencies to prevent recreation

  const fetchRoles = useCallback(async () => {
    // Check if data is already loaded
    if (dropdownData.roles.length > 0 && !dropdownData.error.roles) {
      console.log('Skipping roles fetch - data already loaded');
      return;
    }
    
    // Use a simple flag to prevent multiple simultaneous requests
    if (globalLoadingState.roles) {
      console.log('Skipping roles fetch - already loading');
      return;
    }

    console.log('Fetching roles for dropdown...');
    globalLoadingState.roles = true;
    
    setDropdownData(prev => ({
      ...prev,
      loading: { ...prev.loading, roles: true },
      error: { ...prev.error, roles: null },
    }));

    try {
      const roles = await roleApi.getAllRoles();
      console.log('Roles fetched successfully:', roles);
      
      const roleOptions = roles.map(role => ({
        id: role.id,
        name: role.name,
      }));
      
      setDropdownData(prev => ({
        ...prev,
        roles: roleOptions,
        loading: { ...prev.loading, roles: false },
      }));
    } catch (error) {
      console.error('Error fetching roles:', error);
      
      setDropdownData(prev => ({
        ...prev,
        roles: [],
        loading: { ...prev.loading, roles: false },
        error: { ...prev.error, roles: 'Failed to fetch roles' },
      }));
    } finally {
      globalLoadingState.roles = false;
    }
  }, []); // Remove dependency to allow fetching when called

  const fetchAll = useCallback(async () => {
    await Promise.all([fetchUsers(), fetchCustomers(), fetchLeads(), fetchRoles()]);
  }, []); // Remove dependencies to prevent recreation

  const refreshUsers = useCallback(() => fetchUsers(), []);
  const refreshCustomers = useCallback(() => fetchCustomers(), []);
  const refreshLeads = useCallback(() => fetchLeads(), []);
  const refreshRoles = useCallback(() => fetchRoles(), []);

  // Function to clear global cache
  const clearCache = useCallback(() => {
    globalDropdownCache = null;
    globalLoadingState = {
      users: false,
      customers: false,
      leads: false,
      roles: false,
    };
    setDropdownData({
      users: [],
      customers: [],
      leads: [],
      roles: [],
      loading: {
        users: false,
        customers: false,
        leads: false,
        roles: false,
      },
      error: {
        users: null,
        customers: null,
        leads: null,
        roles: null,
      },
    });
  }, []);

  // Function to refresh all data (clear cache and refetch)
  const refreshAll = useCallback(async () => {
    clearCache();
    await Promise.all([fetchUsers(), fetchCustomers(), fetchLeads(), fetchRoles()]);
  }, []); // Remove dependencies to prevent recreation

  return {
    ...dropdownData,
    fetchUsers,
    fetchCustomers,
    fetchLeads,
    fetchRoles,
    fetchAll,
    refreshUsers,
    refreshCustomers,
    refreshLeads,
    refreshRoles,
    clearCache,
    refreshAll,
  };
}; 