import api from './api';
import { Role } from '../types';

const ROLE_BASE_URL = '/roles';

export const roleApi = {
  // Get all roles
  getAllRoles: async (): Promise<Role[]> => {
    try {
      console.log('Fetching roles from:', ROLE_BASE_URL);
      const response = await api.get(ROLE_BASE_URL);
      console.log('Roles response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('Error fetching roles:', error);
      console.error('Error details:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        message: error.message,
        url: error.config?.url
      });
      return [];
    }
  },

  // Get role by ID
  getRoleById: async (roleId: number): Promise<Role> => {
    const response = await api.get(`${ROLE_BASE_URL}/${roleId}`);
    return response.data;
  },

  // Get role by name
  getRoleByName: async (name: string): Promise<Role> => {
    const response = await api.get(`${ROLE_BASE_URL}/name/${name}`);
    return response.data;
  },

  // Create new role
  createRole: async (roleData: Partial<Role>): Promise<Role> => {
    const response = await api.post(ROLE_BASE_URL, roleData);
    return response.data;
  },

  // Update role
  updateRole: async (roleId: number, roleData: Partial<Role>): Promise<Role> => {
    const response = await api.put(`${ROLE_BASE_URL}/${roleId}`, roleData);
    return response.data;
  },

  // Delete role
  deleteRole: async (roleId: number): Promise<void> => {
    await api.delete(`${ROLE_BASE_URL}/${roleId}`);
  },

  // Get roles by level
  getRolesByLevel: async (level: number): Promise<Role[]> => {
    const response = await api.get(`${ROLE_BASE_URL}/level/${level}`);
    return response.data;
  },

  // Get roles with level greater than or equal to specified level
  getRolesByLevelGreaterThanEqual: async (level: number): Promise<Role[]> => {
    const response = await api.get(`${ROLE_BASE_URL}/level/gte/${level}`);
    return response.data;
  },

  // Get roles with level less than or equal to specified level
  getRolesByLevelLessThanEqual: async (level: number): Promise<Role[]> => {
    const response = await api.get(`${ROLE_BASE_URL}/level/lte/${level}`);
    return response.data;
  }
}; 