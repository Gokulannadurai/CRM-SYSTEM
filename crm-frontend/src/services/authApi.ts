import { userApi } from './api';
import { LoginRequest, LoginResponse, User, ForgotPasswordRequest, ForgotPasswordResponse } from '../types';

export const authApi = {
  // Login user
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await userApi.post('/users/login', credentials);
    return response.data;
  },

  // Get current user
  getCurrentUser: async (): Promise<User> => {
    const response = await userApi.get('/users/me');
    return response.data;
  },

  // Forgot password
  forgotPassword: async (request: ForgotPasswordRequest): Promise<ForgotPasswordResponse> => {
    const response = await userApi.post('/users/forgot-password', request);
    return response.data;
  },

  // Logout (client-side only)
  logout: async (): Promise<void> => {
    // This is handled client-side by removing the token
    // The backend doesn't need a logout endpoint for JWT
    return Promise.resolve();
  },

  // Validate token
  validateToken: async (): Promise<boolean> => {
    try {
      await userApi.get('/users/me');
      return true;
    } catch {
      return false;
    }
  },

  // Refresh token
  refreshToken: async (refreshToken: string): Promise<LoginResponse> => {
    const response = await userApi.post('/users/refresh', { refreshToken });
    return response.data;
  },

  // Get all users for dropdown
  getUsersForDropdown: async (): Promise<{ success: boolean; data: Array<{ id: number; name: string }>; message: string }> => {
    const response = await userApi.get('/users/dropdown');
    return response.data;
  },
}; 