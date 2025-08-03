import { userApi, customerApi, salesApi } from '../services/api';
import API_CONFIG from '../config/api';

export interface ServiceStatus {
  service: string;
  url: string;
  status: 'online' | 'offline' | 'error';
  responseTime?: number;
  error?: string;
}

export interface ApiTestResult {
  timestamp: string;
  services: ServiceStatus[];
  overallStatus: 'all_online' | 'partial_offline' | 'all_offline';
}

/**
 * Test connectivity to all microservices
 */
export const testApiConnectivity = async (): Promise<ApiTestResult> => {
  const services: ServiceStatus[] = [];

  // Test User Service
  try {
    const userStartTime = Date.now();
    await userApi.get('/actuator/health');
    const userResponseTime = Date.now() - userStartTime;
    
    services.push({
      service: 'User Service',
      url: API_CONFIG.USER_SERVICE_URL,
      status: 'online',
      responseTime: userResponseTime,
    });
  } catch (error) {
    services.push({
      service: 'User Service',
      url: API_CONFIG.USER_SERVICE_URL,
      status: 'offline',
      error: error instanceof Error ? error.message : 'Unknown error',
    });
  }

  // Test Customer Service
  try {
    const customerStartTime = Date.now();
    await customerApi.get('/actuator/health');
    const customerResponseTime = Date.now() - customerStartTime;
    
    services.push({
      service: 'Customer Service',
      url: API_CONFIG.CUSTOMER_SERVICE_URL,
      status: 'online',
      responseTime: customerResponseTime,
    });
  } catch (error) {
    services.push({
      service: 'Customer Service',
      url: API_CONFIG.CUSTOMER_SERVICE_URL,
      status: 'offline',
      error: error instanceof Error ? error.message : 'Unknown error',
    });
  }

  // Test Sales Service
  try {
    const salesStartTime = Date.now();
    await salesApi.get('/actuator/health');
    const salesResponseTime = Date.now() - salesStartTime;
    
    services.push({
      service: 'Sales Service',
      url: API_CONFIG.SALES_SERVICE_URL,
      status: 'online',
      responseTime: salesResponseTime,
    });
  } catch (error) {
    services.push({
      service: 'Sales Service',
      url: API_CONFIG.SALES_SERVICE_URL,
      status: 'offline',
      error: error instanceof Error ? error.message : 'Unknown error',
    });
  }

  // Determine overall status
  const onlineServices = services.filter(s => s.status === 'online').length;
  let overallStatus: 'all_online' | 'partial_offline' | 'all_offline';
  
  if (onlineServices === services.length) {
    overallStatus = 'all_online';
  } else if (onlineServices === 0) {
    overallStatus = 'all_offline';
  } else {
    overallStatus = 'partial_offline';
  }

  return {
    timestamp: new Date().toISOString(),
    services,
    overallStatus,
  };
};

/**
 * Test authentication flow
 */
export const testAuthentication = async (username: string, password: string) => {
  try {
    const response = await userApi.post('/users/login', { username, password });
    return {
      success: true,
      data: response.data,
      message: 'Authentication successful',
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.response?.data?.message || error.message,
      status: error.response?.status,
    };
  }
};

/**
 * Test API endpoints with authentication
 */
export const testAuthenticatedEndpoints = async (token: string) => {
  const results: any[] = [];
  
  // Store token temporarily
  localStorage.setItem('token', token);

  try {
    // Test User Service endpoints
    try {
      const userResponse = await userApi.get('/users/me');
      results.push({
        service: 'User Service',
        endpoint: '/users/me',
        status: 'success',
        data: userResponse.data,
      });
    } catch (error: any) {
      results.push({
        service: 'User Service',
        endpoint: '/users/me',
        status: 'error',
        error: error.response?.data?.message || error.message,
      });
    }

    // Test Customer Service endpoints
    try {
      const customerResponse = await customerApi.get('/customers');
      results.push({
        service: 'Customer Service',
        endpoint: '/customers',
        status: 'success',
        data: customerResponse.data,
      });
    } catch (error: any) {
      results.push({
        service: 'Customer Service',
        endpoint: '/customers',
        status: 'error',
        error: error.response?.data?.message || error.message,
      });
    }

    // Test Sales Service endpoints
    try {
      const salesResponse = await salesApi.get('/leads');
      results.push({
        service: 'Sales Service',
        endpoint: '/leads',
        status: 'success',
        data: salesResponse.data,
      });
    } catch (error: any) {
      results.push({
        service: 'Sales Service',
        endpoint: '/leads',
        status: 'error',
        error: error.response?.data?.message || error.message,
      });
    }

  } finally {
    // Clean up
    localStorage.removeItem('token');
  }

  return results;
};

/**
 * Get service URLs for display
 */
export const getServiceUrls = () => {
  return {
    userService: API_CONFIG.USER_SERVICE_URL,
    customerService: API_CONFIG.CUSTOMER_SERVICE_URL,
    salesService: API_CONFIG.SALES_SERVICE_URL,
  };
};

/**
 * Validate API configuration
 */
export const validateApiConfig = () => {
  const issues: string[] = [];

  if (!API_CONFIG.USER_SERVICE_URL) {
    issues.push('User service URL is not configured');
  }

  if (!API_CONFIG.CUSTOMER_SERVICE_URL) {
    issues.push('Customer service URL is not configured');
  }

  if (!API_CONFIG.SALES_SERVICE_URL) {
    issues.push('Sales service URL is not configured');
  }

  if (API_CONFIG.TIMEOUT <= 0) {
    issues.push('API timeout must be greater than 0');
  }

  return {
    isValid: issues.length === 0,
    issues,
  };
}; 