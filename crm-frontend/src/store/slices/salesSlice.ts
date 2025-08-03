import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Lead, LeadStage, Task, TaskType, LeadStats, TaskStats } from '../../types';
import { salesApi } from '../../services/salesApi';

interface SalesState {
  leads: Lead[];
  leadStages: LeadStage[];
  tasks: Task[];
  taskTypes: TaskType[];
  selectedLead: Lead | null;
  selectedTask: Task | null;
  leadStats: LeadStats | null;
  taskStats: TaskStats | null;
  isLoading: boolean;
  loading: boolean; // Add this for backward compatibility
  error: string | null;
  totalElements: number;
  currentPage: number;
  pageSize: number;
}

const initialState: SalesState = {
  leads: [],
  leadStages: [],
  tasks: [],
  taskTypes: [],
  selectedLead: null,
  selectedTask: null,
  leadStats: null,
  taskStats: null,
  isLoading: false,
  loading: false, // Add this for backward compatibility
  error: null,
  totalElements: 0,
  currentPage: 0,
  pageSize: 10,
};

// Lead Async Thunks
export const fetchLeads = createAsyncThunk(
  'sales/fetchLeads',
  async (params: { page?: number; size?: number; search?: string; status?: string; stageId?: number } = {}, { rejectWithValue }) => {
    try {
      const response = await salesApi.getLeads(params);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch leads');
    }
  }
);

export const fetchLeadById = createAsyncThunk(
  'sales/fetchLeadById',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.getLeadById(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch lead');
    }
  }
);

export const createLead = createAsyncThunk(
  'sales/createLead',
  async (formData: FormData, { rejectWithValue }) => {
    try {
      const response = await salesApi.createLead(formData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create lead');
    }
  }
);

export const updateLead = createAsyncThunk(
  'sales/updateLead',
  async ({ id, formData }: { id: number; formData: FormData }, { rejectWithValue }) => {
    try {
      const response = await salesApi.updateLead(id, formData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to update lead');
    }
  }
);

export const deleteLead = createAsyncThunk(
  'sales/deleteLead',
  async (id: number, { rejectWithValue }) => {
    try {
      await salesApi.deleteLead(id);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to delete lead');
    }
  }
);

export const moveLeadToNextStage = createAsyncThunk(
  'sales/moveLeadToNextStage',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.moveLeadToNextStage(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to move lead to next stage');
    }
  }
);

export const moveLeadToPreviousStage = createAsyncThunk(
  'sales/moveLeadToPreviousStage',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.moveLeadToPreviousStage(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to move lead to previous stage');
    }
  }
);

// Lead Stage Async Thunks
export const fetchLeadStages = createAsyncThunk(
  'sales/fetchLeadStages',
  async (_, { rejectWithValue }) => {
    try {
      const response = await salesApi.getLeadStages();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch lead stages');
    }
  }
);

// Task Async Thunks
export const fetchTasks = createAsyncThunk(
  'sales/fetchTasks',
  async (params: { page?: number; size?: number; leadId?: number; customerId?: number; assignedTo?: number; isCompleted?: boolean } = {}, { rejectWithValue }) => {
    try {
      const response = await salesApi.getTasks(params);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch tasks');
    }
  }
);

export const fetchTaskById = createAsyncThunk(
  'sales/fetchTaskById',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.getTaskById(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch task');
    }
  }
);

export const updateTaskStatus = createAsyncThunk(
  'sales/updateTaskStatus',
  async ({ taskId, status }: { taskId: number; status: string }, { rejectWithValue }) => {
    try {
      const response = await salesApi.updateTaskStatus(taskId, status);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to update task status');
    }
  }
);

export const createTask = createAsyncThunk(
  'sales/createTask',
  async (formData: FormData, { rejectWithValue }) => {
    try {
      const response = await salesApi.createTask(formData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create task');
    }
  }
);

export const updateTask = createAsyncThunk(
  'sales/updateTask',
  async ({ id, formData }: { id: number; formData: FormData }, { rejectWithValue }) => {
    try {
      const response = await salesApi.updateTask(id, formData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to update task');
    }
  }
);

export const deleteTask = createAsyncThunk(
  'sales/deleteTask',
  async (id: number, { rejectWithValue }) => {
    try {
      await salesApi.deleteTask(id);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to delete task');
    }
  }
);

export const markTaskAsCompleted = createAsyncThunk(
  'sales/markTaskAsCompleted',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.markTaskAsCompleted(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to mark task as completed');
    }
  }
);

export const markTaskAsIncomplete = createAsyncThunk(
  'sales/markTaskAsIncomplete',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await salesApi.markTaskAsIncomplete(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to mark task as incomplete');
    }
  }
);

// Task Type Async Thunks
export const fetchTaskTypes = createAsyncThunk(
  'sales/fetchTaskTypes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await salesApi.getTaskTypes();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch task types');
    }
  }
);

// Stats Async Thunks
export const fetchLeadStats = createAsyncThunk(
  'sales/fetchLeadStats',
  async (_, { rejectWithValue }) => {
    try {
      const response = await salesApi.getLeadStats();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch lead stats');
    }
  }
);

export const fetchTaskStats = createAsyncThunk(
  'sales/fetchTaskStats',
  async (_, { rejectWithValue }) => {
    try {
      const response = await salesApi.getTaskStats();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch task stats');
    }
  }
);

const salesSlice = createSlice({
  name: 'sales',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSelectedLead: (state, action: PayloadAction<Lead | null>) => {
      state.selectedLead = action.payload;
    },
    setSelectedTask: (state, action: PayloadAction<Task | null>) => {
      state.selectedTask = action.payload;
    },
    clearSelectedLead: (state) => {
      state.selectedLead = null;
    },
    clearSelectedTask: (state) => {
      state.selectedTask = null;
    },
    clearTasks: (state) => {
      state.tasks = [];
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Leads
      .addCase(fetchLeads.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchLeads.fulfilled, (state, action) => {
        state.isLoading = false;
        state.leads = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.currentPage = action.payload.number;
        state.pageSize = action.payload.size;
      })
      .addCase(fetchLeads.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Fetch Lead By ID
      .addCase(fetchLeadById.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchLeadById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.selectedLead = action.payload;
      })
      .addCase(fetchLeadById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Create Lead
      .addCase(createLead.fulfilled, (state, action) => {
        state.leads.unshift(action.payload);
        state.totalElements += 1;
      })
      .addCase(createLead.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Update Lead
      .addCase(updateLead.fulfilled, (state, action) => {
        const index = state.leads.findIndex(l => l.id === action.payload.id);
        if (index !== -1) {
          state.leads[index] = action.payload;
        }
        if (state.selectedLead?.id === action.payload.id) {
          state.selectedLead = action.payload;
        }
      })
      .addCase(updateLead.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Delete Lead
      .addCase(deleteLead.fulfilled, (state, action) => {
        state.leads = state.leads.filter(l => l.id !== action.payload);
        state.totalElements -= 1;
        if (state.selectedLead?.id === action.payload) {
          state.selectedLead = null;
        }
      })
      .addCase(deleteLead.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Move Lead Stages
      .addCase(moveLeadToNextStage.fulfilled, (state, action) => {
        const index = state.leads.findIndex(l => l.id === action.payload.id);
        if (index !== -1) {
          state.leads[index] = action.payload;
        }
        if (state.selectedLead?.id === action.payload.id) {
          state.selectedLead = action.payload;
        }
      })
      .addCase(moveLeadToPreviousStage.fulfilled, (state, action) => {
        const index = state.leads.findIndex(l => l.id === action.payload.id);
        if (index !== -1) {
          state.leads[index] = action.payload;
        }
        if (state.selectedLead?.id === action.payload.id) {
          state.selectedLead = action.payload;
        }
      })
      // Fetch Lead Stages
      .addCase(fetchLeadStages.fulfilled, (state, action) => {
        state.leadStages = action.payload;
      })
      .addCase(fetchLeadStages.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Fetch Tasks
      .addCase(fetchTasks.fulfilled, (state, action) => {
        console.log('Redux: Setting tasks from API response:', {
          contentLength: action.payload.content?.length || 0,
          totalElements: action.payload.totalElements,
          pageNumber: action.payload.number,
          pageSize: action.payload.size
        });
        state.tasks = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.currentPage = action.payload.number;
        state.pageSize = action.payload.size;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Fetch Task By ID
      .addCase(fetchTaskById.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchTaskById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.selectedTask = action.payload;
      })
      .addCase(fetchTaskById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Update Task Status
      .addCase(updateTaskStatus.fulfilled, (state, action) => {
        const index = state.tasks.findIndex(t => t.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        if (state.selectedTask?.id === action.payload.id) {
          state.selectedTask = action.payload;
        }
      })
      .addCase(updateTaskStatus.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Create Task
      .addCase(createTask.fulfilled, (state, action) => {
        // Clear the tasks array to force a refresh from the server
        state.tasks = [];
        state.totalElements = 0;
      })
      .addCase(createTask.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Update Task
      .addCase(updateTask.fulfilled, (state, action) => {
        const index = state.tasks.findIndex(t => t.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        if (state.selectedTask?.id === action.payload.id) {
          state.selectedTask = action.payload;
        }
      })
      .addCase(updateTask.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Delete Task
      .addCase(deleteTask.fulfilled, (state, action) => {
        state.tasks = state.tasks.filter(t => t.id !== action.payload);
        state.totalElements -= 1;
        if (state.selectedTask?.id === action.payload) {
          state.selectedTask = null;
        }
      })
      .addCase(deleteTask.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Mark Task Complete/Incomplete
      .addCase(markTaskAsCompleted.fulfilled, (state, action) => {
        const index = state.tasks.findIndex(t => t.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        if (state.selectedTask?.id === action.payload.id) {
          state.selectedTask = action.payload;
        }
      })
      .addCase(markTaskAsIncomplete.fulfilled, (state, action) => {
        const index = state.tasks.findIndex(t => t.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        if (state.selectedTask?.id === action.payload.id) {
          state.selectedTask = action.payload;
        }
      })
      // Fetch Task Types
      .addCase(fetchTaskTypes.fulfilled, (state, action) => {
        state.taskTypes = action.payload;
      })
      .addCase(fetchTaskTypes.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Fetch Stats
      .addCase(fetchLeadStats.fulfilled, (state, action) => {
        state.leadStats = action.payload;
      })
      .addCase(fetchTaskStats.fulfilled, (state, action) => {
        state.taskStats = action.payload;
      });
  },
});

export const { 
  clearError, 
  setSelectedLead, 
  setSelectedTask, 
  clearSelectedLead, 
  clearSelectedTask,
  clearTasks
} = salesSlice.actions;
export default salesSlice.reducer; 