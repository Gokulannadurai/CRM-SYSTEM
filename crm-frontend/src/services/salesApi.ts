import { salesApi as salesServiceApi } from './api';
import { 
  Lead, 
  LeadStage, 
  Task, 
  TaskType, 
  LeadStats, 
  TaskStats, 
  PaginatedResponse 
} from '../types';

export const salesApi = {
  // Lead Management
  getLeads: async (params: {
    page?: number;
    size?: number;
    search?: string;
    status?: string;
    stageId?: number;
    customerId?: number;
    assignedTo?: number;
  }): Promise<PaginatedResponse<Lead>> => {
    const response = await salesServiceApi.get('/leads', { params });
    return response.data;
  },

  getLeadById: async (id: number): Promise<Lead> => {
    const response = await salesServiceApi.get(`/leads/${id}`);
    return response.data;
  },

  createLead: async (formData: FormData): Promise<Lead> => {
    const response = await salesServiceApi.post('/leads/update', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  updateLead: async (id: number, formData: FormData): Promise<Lead> => {
    const response = await salesServiceApi.post('/leads/update', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  deleteLead: async (id: number): Promise<void> => {
    await salesServiceApi.delete(`/leads/${id}`);
  },

  moveLeadToNextStage: async (id: number): Promise<Lead> => {
    const response = await salesServiceApi.patch(`/leads/${id}/next-stage`);
    return response.data;
  },

  moveLeadToPreviousStage: async (id: number): Promise<Lead> => {
    const response = await salesServiceApi.patch(`/leads/${id}/previous-stage`);
    return response.data;
  },

  assignLead: async (id: number, assignedTo: number): Promise<Lead> => {
    const response = await salesServiceApi.patch(`/leads/${id}/assign?assignedTo=${assignedTo}`);
    return response.data;
  },

  getLeadsByCustomer: async (customerId: number): Promise<Lead[]> => {
    const response = await salesServiceApi.get(`/leads/customer/${customerId}`);
    return response.data;
  },

  getLeadsByAssignedUser: async (assignedTo: number): Promise<Lead[]> => {
    const response = await salesServiceApi.get(`/leads/assigned/${assignedTo}`);
    return response.data;
  },

  getLeadsByStatus: async (status: string): Promise<Lead[]> => {
    const response = await salesServiceApi.get(`/leads/status/${status}`);
    return response.data;
  },

  getLeadsByStage: async (stageId: number): Promise<Lead[]> => {
    const response = await salesServiceApi.get(`/leads/stage/${stageId}`);
    return response.data;
  },

  getOverdueLeads: async (): Promise<Lead[]> => {
    const response = await salesServiceApi.get('/leads/overdue');
    return response.data;
  },

  getLeadsDueSoon: async (days: number = 7): Promise<Lead[]> => {
    const response = await salesServiceApi.get('/leads/due-soon', { params: { days } });
    return response.data;
  },

  getLeadStats: async (): Promise<LeadStats> => {
    const response = await salesServiceApi.get('/leads/stats');
    return response.data;
  },

  getPipelineStats: async (): Promise<any> => {
    const response = await salesServiceApi.get('/leads/pipeline/stats');
    return response.data;
  },

  // Lead Stage Management
  getLeadStages: async (): Promise<LeadStage[]> => {
    const response = await salesServiceApi.get('/lead-stages');
    return response.data;
  },

  getLeadStageById: async (id: number): Promise<LeadStage> => {
    const response = await salesServiceApi.get(`/lead-stages/${id}`);
    return response.data;
  },

  createLeadStage: async (leadStageData: Omit<LeadStage, 'id' | 'createdAt' | 'updatedAt'>): Promise<LeadStage> => {
    const response = await salesServiceApi.post('/lead-stages', leadStageData);
    return response.data;
  },

  updateLeadStage: async (id: number, leadStageData: Partial<LeadStage>): Promise<LeadStage> => {
    const response = await salesServiceApi.put(`/lead-stages/${id}`, leadStageData);
    return response.data;
  },

  deleteLeadStage: async (id: number): Promise<void> => {
    await salesServiceApi.delete(`/lead-stages/${id}`);
  },

  // Task Management
  getTasks: async (params: {
    page?: number;
    size?: number;
    leadId?: number;
    customerId?: number;
    assignedTo?: number;
    taskTypeId?: number;
    isCompleted?: boolean;
    overdue?: boolean;
    dueSoon?: boolean;
  }): Promise<PaginatedResponse<Task>> => {
    const response = await salesServiceApi.get('/tasks', { params });
    return response.data;
  },

  getTaskById: async (id: number): Promise<Task> => {
    const response = await salesServiceApi.get(`/tasks/${id}`);
    return response.data;
  },

  createTask: async (formData: FormData): Promise<Task> => {
    const response = await salesServiceApi.post('/tasks/update', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  updateTask: async (id: number, formData: FormData): Promise<Task> => {
    const response = await salesServiceApi.post('/tasks/update', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  deleteTask: async (id: number): Promise<void> => {
    await salesServiceApi.delete(`/tasks/${id}`);
  },

  markTaskAsCompleted: async (id: number): Promise<Task> => {
    const response = await salesServiceApi.patch(`/tasks/${id}/complete`);
    return response.data;
  },

  markTaskAsIncomplete: async (id: number): Promise<Task> => {
    const response = await salesServiceApi.patch(`/tasks/${id}/incomplete`);
    return response.data;
  },

  updateTaskStatus: async (id: number, status: string): Promise<Task> => {
    const response = await salesServiceApi.patch(`/tasks/${id}/status`, { status });
    return response.data;
  },

  assignTask: async (id: number, assignedTo: number): Promise<Task> => {
    const response = await salesServiceApi.patch(`/tasks/${id}/assign?assignedTo=${assignedTo}`);
    return response.data;
  },

  getTasksByLead: async (leadId: number): Promise<Task[]> => {
    const response = await salesServiceApi.get(`/tasks/lead/${leadId}`);
    return response.data;
  },

  getTasksByCustomer: async (customerId: number): Promise<Task[]> => {
    const response = await salesServiceApi.get(`/tasks/customer/${customerId}`);
    return response.data;
  },

  getTasksByAssignedUser: async (assignedTo: number): Promise<Task[]> => {
    const response = await salesServiceApi.get(`/tasks/assigned/${assignedTo}`);
    return response.data;
  },

  getTasksByType: async (taskTypeId: number): Promise<Task[]> => {
    const response = await salesServiceApi.get(`/tasks/type/${taskTypeId}`);
    return response.data;
  },

  getTasksByStatus: async (isCompleted: boolean): Promise<Task[]> => {
    const response = await salesServiceApi.get(`/tasks/status/${isCompleted}`);
    return response.data;
  },

  getOverdueTasks: async (): Promise<Task[]> => {
    const response = await salesServiceApi.get('/tasks/overdue');
    return response.data;
  },

  getTasksDueSoon: async (hours: number = 24): Promise<Task[]> => {
    const response = await salesServiceApi.get('/tasks/due-soon', { params: { hours } });
    return response.data;
  },

  getTaskStats: async (): Promise<TaskStats> => {
    const response = await salesServiceApi.get('/tasks/stats');
    return response.data;
  },

  // Task Type Management
  getTaskTypes: async (): Promise<TaskType[]> => {
    const response = await salesServiceApi.get('/task-types');
    return response.data;
  },

  getTaskTypeById: async (id: number): Promise<TaskType> => {
    const response = await salesServiceApi.get(`/task-types/${id}`);
    return response.data;
  },

  createTaskType: async (taskTypeData: Omit<TaskType, 'id' | 'createdAt' | 'updatedAt'>): Promise<TaskType> => {
    const response = await salesServiceApi.post('/task-types', taskTypeData);
    return response.data;
  },

  updateTaskType: async (id: number, taskTypeData: Partial<TaskType>): Promise<TaskType> => {
    const response = await salesServiceApi.put(`/task-types/${id}`, taskTypeData);
    return response.data;
  },

  deleteTaskType: async (id: number): Promise<void> => {
    await salesServiceApi.delete(`/task-types/${id}`);
  },

  searchTaskTypes: async (name: string): Promise<TaskType[]> => {
    const response = await salesServiceApi.get('/task-types/search', { params: { name } });
    return response.data;
  },

  checkTaskTypeExists: async (name: string): Promise<boolean> => {
    const response = await salesServiceApi.get(`/task-types/exists/${name}`);
    return response.data;
  },

  // Get all leads for dropdown
  getLeadsForDropdown: async (): Promise<{ success: boolean; data: Array<{ id: number; title: string }>; message: string }> => {
    const response = await salesServiceApi.get('/leads/dropdown');
    return response.data;
  },
}; 