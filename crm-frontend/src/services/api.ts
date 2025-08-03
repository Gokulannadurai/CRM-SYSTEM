import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import API_CONFIG from '../config/api';
import { authApi } from './authApi';
import TokenManager from '../utils/tokenManager';
import { isTokenExpiredError, handleTokenExpiration, handleForbiddenError } from '../utils/authUtils';

// Token refresh state
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: any) => void;
  reject: (reason?: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  
  failedQueue = [];
};

// Create axios instances for each service
const createApiInstance = (baseURL: string): AxiosInstance => {
  const api: AxiosInstance = axios.create({
    baseURL: `${baseURL}/api/${API_CONFIG.API_VERSION}`,
    timeout: API_CONFIG.TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor to add auth token
  api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const authHeader = TokenManager.getAuthHeader();
      if (authHeader && config.headers) {
        config.headers.Authorization = authHeader;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle errors and token refresh
  api.interceptors.response.use(
    (response: AxiosResponse) => {
      return response;
    },
    async (error) => {
      const originalRequest = error.config;

      // Handle 401 Unauthorized with token refresh
      if (error.response?.status === API_CONFIG.HTTP_STATUS.UNAUTHORIZED && !originalRequest._retry) {
        // Check if it's a token expiration error
        if (isTokenExpiredError(error)) {
          // Token is expired, handle it properly
          handleTokenExpiration();
          return Promise.reject(error);
        }
        
        // Handle other 401 errors with token refresh
        if (isRefreshing) {
          // If already refreshing, queue the request
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          }).then(token => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          }).catch(err => {
            return Promise.reject(err);
          });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken = TokenManager.getRefreshToken();
        
        if (!refreshToken) {
          // No refresh token available, redirect to login
          TokenManager.clearTokens();
          window.location.href = '/login';
          return Promise.reject(error);
        }

        try {
          const response = await authApi.refreshToken(refreshToken);
          const newAccessToken = response.access_token;
          const newRefreshToken = response.refresh_token;
          
          TokenManager.updateTokens(newAccessToken, newRefreshToken, response.expires_in);
          
          processQueue(null, newAccessToken);
          
          // Retry the original request with new token
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return api(originalRequest);
        } catch (refreshError) {
          // Refresh failed, clear tokens and redirect to login
          processQueue(refreshError, null);
          TokenManager.clearTokens();
          window.location.href = '/login';
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }
      
      // Handle 403 Forbidden - User doesn't have permission, clear tokens and redirect to login
      if (error.response?.status === API_CONFIG.HTTP_STATUS.FORBIDDEN) {
        handleForbiddenError();
        return Promise.reject(error);
      }
      
      // Handle rate limiting
      if (error.response?.status === API_CONFIG.HTTP_STATUS.TOO_MANY_REQUESTS) {
        console.error('Rate limit exceeded');
      }
      
      return Promise.reject(error);
    }
  );

  return api;
};

// Create API instances for each service
export const userApi = createApiInstance(API_CONFIG.USER_SERVICE_URL);
export const customerApi = createApiInstance(API_CONFIG.CUSTOMER_SERVICE_URL);
export const salesApi = createApiInstance(API_CONFIG.SALES_SERVICE_URL);

// Default export for backward compatibility (uses user service as default)
const api = userApi;
export default api; 