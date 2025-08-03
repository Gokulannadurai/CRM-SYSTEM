import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Customer, InteractionHistory, CustomerFormData, CustomerStats } from '../../types';
import { customerApi } from '../../services/customerApi';

interface CustomerState {
  customers: Customer[];
  selectedCustomer: Customer | null;
  interactionHistory: InteractionHistory[];
  stats: CustomerStats | null;
  isLoading: boolean;
  loading: boolean; // Add this for backward compatibility
  error: string | null;
  totalElements: number;
  currentPage: number;
  pageSize: number;
}

const initialState: CustomerState = {
  customers: [],
  selectedCustomer: null,
  interactionHistory: [],
  stats: null,
  isLoading: false,
  loading: false, // Add this for backward compatibility
  error: null,
  totalElements: 0,
  currentPage: 0,
  pageSize: 10,
};

// Async thunks
export const fetchCustomers = createAsyncThunk(
  'customer/fetchCustomers',
  async (params: { page?: number; size?: number; search?: string; isActive?: boolean } = {}, { rejectWithValue }) => {
    try {
      const response = await customerApi.getCustomers(params);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch customers');
    }
  }
);

export const fetchCustomerById = createAsyncThunk(
  'customer/fetchCustomerById',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await customerApi.getCustomerById(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch customer');
    }
  }
);

export const createCustomer = createAsyncThunk(
  'customer/createCustomer',
  async (customerData: CustomerFormData, { rejectWithValue }) => {
    try {
      const response = await customerApi.createCustomer(customerData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create customer');
    }
  }
);

export const updateCustomer = createAsyncThunk(
  'customer/updateCustomer',
  async ({ id, customerData }: { id: number; customerData: CustomerFormData }, { rejectWithValue }) => {
    try {
      const response = await customerApi.updateCustomer(id, customerData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to update customer');
    }
  }
);

export const deleteCustomer = createAsyncThunk(
  'customer/deleteCustomer',
  async (id: number, { rejectWithValue }) => {
    try {
      await customerApi.deleteCustomer(id);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to delete customer');
    }
  }
);

export const getCustomerById = createAsyncThunk(
  'customer/getCustomerById',
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await customerApi.getCustomerById(id);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch customer');
    }
  }
);

export const fetchInteractionHistory = createAsyncThunk(
  'customer/fetchInteractionHistory',
  async (customerId: number, { rejectWithValue }) => {
    try {
      const response = await customerApi.getInteractionHistory(customerId);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch interaction history');
    }
  }
);

export const createInteraction = createAsyncThunk(
  'customer/createInteraction',
  async (interactionData: Omit<InteractionHistory, 'id' | 'createdAt' | 'updatedAt'>, { rejectWithValue }) => {
    try {
      const response = await customerApi.createInteraction(interactionData);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create interaction');
    }
  }
);

export const fetchCustomerStats = createAsyncThunk(
  'customer/fetchCustomerStats',
  async (_, { rejectWithValue }) => {
    try {
      const response = await customerApi.getCustomerStats();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch customer stats');
    }
  }
);

const customerSlice = createSlice({
  name: 'customer',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSelectedCustomer: (state, action: PayloadAction<Customer | null>) => {
      state.selectedCustomer = action.payload;
    },
    clearSelectedCustomer: (state) => {
      state.selectedCustomer = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Customers
      .addCase(fetchCustomers.pending, (state) => {
        state.isLoading = true;
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomers.fulfilled, (state, action) => {
        state.isLoading = false;
        state.loading = false;
        state.customers = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.currentPage = action.payload.number;
        state.pageSize = action.payload.size;
      })
      .addCase(fetchCustomers.rejected, (state, action) => {
        state.isLoading = false;
        state.loading = false;
        state.error = action.payload as string;
      })
      // Fetch Customer By ID
      .addCase(fetchCustomerById.pending, (state) => {
        state.isLoading = true;
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomerById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.loading = false;
        state.selectedCustomer = action.payload;
      })
      .addCase(fetchCustomerById.rejected, (state, action) => {
        state.isLoading = false;
        state.loading = false;
        state.error = action.payload as string;
      })
      // Create Customer
      .addCase(createCustomer.fulfilled, (state, action) => {
        state.customers.unshift(action.payload);
        state.totalElements += 1;
      })
      .addCase(createCustomer.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Update Customer
      .addCase(updateCustomer.fulfilled, (state, action) => {
        const index = state.customers.findIndex(c => c.id === action.payload.id);
        if (index !== -1) {
          state.customers[index] = action.payload;
        }
        if (state.selectedCustomer?.id === action.payload.id) {
          state.selectedCustomer = action.payload;
        }
      })
      .addCase(updateCustomer.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Delete Customer
      .addCase(deleteCustomer.fulfilled, (state, action) => {
        state.customers = state.customers.filter(c => c.id !== action.payload);
        state.totalElements -= 1;
        if (state.selectedCustomer?.id === action.payload) {
          state.selectedCustomer = null;
        }
      })
      .addCase(deleteCustomer.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Get Customer By ID
      .addCase(getCustomerById.fulfilled, (state, action) => {
        state.selectedCustomer = action.payload;
      })
      .addCase(getCustomerById.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Fetch Interaction History
      .addCase(fetchInteractionHistory.fulfilled, (state, action) => {
        state.interactionHistory = action.payload.content;
      })
      .addCase(fetchInteractionHistory.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Create Interaction
      .addCase(createInteraction.fulfilled, (state, action) => {
        state.interactionHistory.unshift(action.payload);
      })
      .addCase(createInteraction.rejected, (state, action) => {
        state.error = action.payload as string;
      })
      // Fetch Customer Stats
      .addCase(fetchCustomerStats.fulfilled, (state, action) => {
        state.stats = action.payload;
      })
      .addCase(fetchCustomerStats.rejected, (state, action) => {
        state.error = action.payload as string;
      });
  },
});

export const { clearError, setSelectedCustomer, clearSelectedCustomer } = customerSlice.actions;
export default customerSlice.reducer; 