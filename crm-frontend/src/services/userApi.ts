import api from './api';
import { User, PaginatedResponse, ApiResponse } from '../types';

const USER_BASE_URL = '/users';

export const userApi = {
  // Get all users with pagination and filters
  getAllUsers: async (page: number = 0, size: number = 10, isActive?: boolean, role?: string, searchTerm?: string): Promise<PaginatedResponse<User>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    if (isActive !== undefined) {
      params.append('isActive', isActive.toString());
    }
    if (role) {
      params.append('role', role);
    }
    if (searchTerm) {
      params.append('searchTerm', searchTerm);
    }
    
    console.log('userApi.getAllUsers - role parameter:', role);
    console.log('userApi.getAllUsers - searchTerm parameter:', searchTerm);
    console.log('userApi.getAllUsers - role filter applied:', !!role);
    console.log('userApi.getAllUsers - search filter applied:', !!searchTerm);
    console.log('userApi.getAllUsers - final URL params:', params.toString());
    
    if (!role) {
      console.log('userApi.getAllUsers - NO role filter applied, will fetch ALL users');
    } else {
      console.log('userApi.getAllUsers - role filter applied for role:', role);
    }
    
    const response = await api.get(`${USER_BASE_URL}?${params.toString()}`);
    return response.data;
  },

  // Get user by ID
  getUserById: async (userId: number): Promise<User> => {
    const response = await api.get(`${USER_BASE_URL}/${userId}`);
    return response.data;
  },

  // Create new user
  createUser: async (userData: Partial<User>): Promise<User> => {
    // Map frontend fields to backend fields
    const backendData: any = {
      username: userData.username,
      email: userData.email,
      first_name: userData.firstName,
      last_name: userData.lastName,
      mobile_number: userData.mobileNumber,
      is_active: userData.isActive,
      roles: userData.roles
    };
    
    // Add password if provided
    if ((userData as any).password) {
      backendData.password = (userData as any).password;
    }
    const response = await api.post(USER_BASE_URL, backendData);
    return response.data;
  },

  // Update user
  updateUser: async (userId: number, userData: Partial<User>): Promise<User> => {
    // Map frontend fields to backend fields
    const backendData: any = {
      username: userData.username,
      email: userData.email,
      first_name: userData.firstName,
      last_name: userData.lastName,
      mobile_number: userData.mobileNumber,
      is_active: userData.isActive,
      roles: userData.roles
    };
    
    // Add password if provided
    if ((userData as any).password) {
      backendData.password = (userData as any).password;
    }
    
    try {
      const response = await api.post(`${USER_BASE_URL}/${userId}/update`, backendData);
      return response.data;
    } catch (error: any) {
      console.error('User update failed:', error.response?.data);
      if (error.response?.data?.details) {
        // Handle validation errors
        const validationErrors = error.response.data.details;
        const errorMessage = Object.entries(validationErrors)
          .map(([field, message]) => `${field}: ${message}`)
          .join(', ');
        throw new Error(`Validation failed: ${errorMessage}`);
      }
      throw error;
    }
  },

  // Delete user (soft delete)
  deleteUser: async (userId: number): Promise<void> => {
    await api.post(`${USER_BASE_URL}/${userId}/delete`);
  },

  // Search users
  searchUsers: async (searchTerm: string, page: number = 0, size: number = 10): Promise<PaginatedResponse<User>> => {
    const response = await api.get(`${USER_BASE_URL}/search?searchTerm=${encodeURIComponent(searchTerm)}&page=${page}&size=${size}`);
    return response.data;
  },

  // Get users for dropdown
  getUsersForDropdown: async (): Promise<User[]> => {
    console.log('Attempting to fetch users for dropdown...');
    
    // Debug: Check if we have authentication
    const authHeader = localStorage.getItem('accessToken');
    console.log('Auth token available:', !!authHeader);
    console.log('Auth token:', authHeader ? authHeader.substring(0, 20) + '...' : 'None');
    
    try {
      // Try the dropdown endpoint first
      console.log('Trying dropdown endpoint:', `${USER_BASE_URL}/dropdown`);
      const response = await api.get(`${USER_BASE_URL}/dropdown`);
      console.log('Dropdown endpoint response:', response.data);
      
      // Backend returns { success: true, data: [...], message: "..." }
      if (response.data && response.data.success && response.data.data && Array.isArray(response.data.data)) {
        console.log('Successfully got users from dropdown endpoint:', response.data.data.length);
        return response.data.data;
      } else if (response.data && Array.isArray(response.data)) {
        console.log('Successfully got users from dropdown endpoint (direct array):', response.data.length);
        return response.data;
      } else {
        console.error('Unexpected dropdown response format:', response.data);
        return [];
      }
    } catch (error: any) {
      console.error('Dropdown endpoint failed:', error);
      console.error('Error details:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        message: error.message,
        url: error.config?.url,
        headers: error.config?.headers
      });
      
      // Return empty array on error
      return [];
    }
  },

  // Get user by username
  getUserByUsername: async (username: string): Promise<User> => {
    const response = await api.get(`${USER_BASE_URL}/username/${username}`);
    return response.data;
  },

  // Health check
  healthCheck: async (): Promise<ApiResponse<any>> => {
    try {
      const response = await api.get(`${USER_BASE_URL}/health`);
      console.log('User service health check response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('User service health check failed:', error);
      console.error('Health check error details:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        message: error.message,
        url: error.config?.url
      });
      throw error;
    }
  },

  // Simple test endpoint to check if service is reachable
  testConnection: async (): Promise<boolean> => {
    try {
      const response = await api.get(`${USER_BASE_URL}/health`);
      return response.status === 200;
    } catch (error) {
      console.error('User service connection test failed:', error);
      return false;
    }
  },

  // Test dropdown endpoint specifically
  testDropdownEndpoint: async (): Promise<any> => {
    try {
      console.log('Testing dropdown endpoint directly...');
      const response = await api.get(`${USER_BASE_URL}/dropdown`);
      console.log('Dropdown test response:', response);
      return response.data;
    } catch (error) {
      console.error('Dropdown endpoint test failed:', error);
      throw error;
    }
  }
}; 