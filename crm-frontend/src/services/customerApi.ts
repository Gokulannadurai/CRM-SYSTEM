import { customerApi as customerServiceApi } from './api';
import { Customer, InteractionHistory, CustomerFormData, CustomerStats, PaginatedResponse } from '../types';

export const customerApi = {
  // Get all customers with pagination and filters
  getCustomers: async (params: {
    page?: number;
    size?: number;
    search?: string;
    isActive?: boolean;
    company?: string;
    source?: string;
  }): Promise<PaginatedResponse<Customer>> => {
    const response = await customerServiceApi.get('/customers', { params });
    return response.data;
  },

  // Get customer by ID
  getCustomerById: async (id: number): Promise<Customer> => {
    const response = await customerServiceApi.get(`/customers/${id}`);
    return response.data;
  },

  // Create new customer
  createCustomer: async (customerData: CustomerFormData): Promise<Customer> => {
    const response = await customerServiceApi.post('/customers', customerData);
    return response.data;
  },

  // Update customer
  updateCustomer: async (id: number, customerData: CustomerFormData): Promise<Customer> => {
    const response = await customerServiceApi.put(`/customers/${id}`, customerData);
    return response.data;
  },

  // Delete customer
  deleteCustomer: async (id: number): Promise<void> => {
    await customerServiceApi.delete(`/customers/${id}`);
  },

  // Change customer active status
  changeCustomerActiveStatus: async (id: number, isActive: boolean): Promise<Customer> => {
    const response = await customerServiceApi.patch(`/customers/${id}/status`, { isActive });
    return response.data;
  },

  // Get customer statistics
  getCustomerStats: async (): Promise<CustomerStats> => {
    const response = await customerServiceApi.get('/customers/stats');
    return response.data;
  },

  // Get interaction history for a customer
  getInteractionHistory: async (customerId: number, params?: { page?: number; size?: number }): Promise<PaginatedResponse<InteractionHistory>> => {
    const response = await customerServiceApi.get(`/interaction-history/customer/${customerId}`, { params });
    return response.data;
  },

  // Create interaction for a customer
  createInteraction: async (interactionData: Omit<InteractionHistory, 'id' | 'createdAt' | 'updatedAt'>): Promise<InteractionHistory> => {
    const response = await customerServiceApi.post('/interaction-history', interactionData);
    return response.data;
  },

  // Get recent interactions for a customer
  getRecentInteractions: async (customerId: number, limit: number = 10): Promise<InteractionHistory[]> => {
    const response = await customerServiceApi.get(`/interaction-history/customer/${customerId}/recent`, { params: { limit } });
    return response.data;
  },

  // Update interaction history
  updateInteraction: async (interactionId: number, interactionData: Partial<InteractionHistory>): Promise<InteractionHistory> => {
    const response = await customerServiceApi.put(`/interaction-history/${interactionId}`, interactionData);
    return response.data;
  },

  // Delete interaction history
  deleteInteraction: async (interactionId: number): Promise<void> => {
    await customerServiceApi.delete(`/interaction-history/${interactionId}`);
  },

  // Search customers
  searchCustomers: async (query: string, params?: { page?: number; size?: number }): Promise<PaginatedResponse<Customer>> => {
    const response = await customerServiceApi.get('/customers/search', { params: { query, ...params } });
    return response.data;
  },

  // Get customer by email
  getCustomerByEmail: async (email: string): Promise<Customer> => {
    const response = await customerServiceApi.get(`/customers/email/${email}`);
    return response.data;
  },

  // Get all customers for dropdown
  getCustomersForDropdown: async (): Promise<{ success: boolean; data: Array<{ id: number; name: string }>; message: string }> => {
    const response = await customerServiceApi.get('/customers/dropdown');
    return response.data;
  },
}; 