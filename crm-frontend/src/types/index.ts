// User Types
export interface User {
  id?: number; // For frontend compatibility
  user_id?: number; // Backend response field
  username: string;
  email: string;
  firstName?: string; // For frontend compatibility
  lastName?: string; // For frontend compatibility
  first_name?: string; // Backend response field
  last_name?: string; // Backend response field
  mobileNumber?: string; // For frontend compatibility
  mobile_number?: string; // Backend response field
  isActive?: boolean; // For frontend compatibility
  is_active?: boolean; // Backend response field
  isDeleted?: boolean; // For frontend compatibility
  is_deleted?: boolean; // Backend response field
  lastLoginAt?: string;
  last_login_at?: string; // Backend response field
  roles: Role[];
  createdAt: string;
  created_at?: string; // Backend response field
  updatedAt: string;
  updated_at?: string; // Backend response field
}

export interface Role {
  id: number;
  name: string;
  level: number;
}

// Customer Types
export interface Customer {
  id: number;
  name: string;
  email?: string;
  phone?: string;
  company?: string;
  source?: string;
  createdBy?: number;
  isActive: boolean;
  interactionHistory?: InteractionHistory[];
  createdAt: string;
  updatedAt: string;
}

export interface InteractionHistory {
  id: number;
  customerId: number;
  userId?: number;
  interactionType: string;
  type: string; // Add this for backward compatibility
  notes?: string;
  interactionDate: string;
  createdAt: string;
  updatedAt: string;
}

// Sales Types
export interface Lead {
  id: number;
  title: string;
  customerId: number;
  customerName?: string;
  assignedTo?: number;
  userName?: string;
  leadId?: number;
  status: string;
  currentStageId?: number;
  expectedCloseDate?: string;
  value?: number;
  source?: string;
  additionalNotes?: string;
  attachments?: Attachment[];
  createdAt: string;
  updatedAt: string;
  // Optional customer and user data that might be populated by the backend
  customer?: Customer;
  assignedUser?: User;
  currentStage?: LeadStage;
  // Legacy properties for backward compatibility (will be populated from customer data)
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  company?: string;
  stage?: string;
  estimatedValue?: number;
  tasks?: Task[];
  relatedTasks?: TaskSummary[];
}

export interface LeadStage {
  id: number;
  name: string;
  position: number;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: number;
  leadId?: number;
  customerId?: number;
  assignedTo?: string;
  taskTypeId?: number;
  title: string;
  description?: string;
  dueDate?: string;
  isCompleted: boolean;
  isStarted?: boolean;
  isCancelled?: boolean;
  attachments?: Attachment[];
  // Additional properties that components expect
  status: string;
  priority?: string;
  createdAt: string;
  updatedAt: string;
  lead?: Lead;
  customer?: Customer;
  assignedUser?: User;
  taskType?: TaskType;
  relatedLead?: LeadSummary;
}

export interface TaskType {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
}

// Summary interfaces for related data
export interface TaskSummary {
  id: number;
  title: string;
  description?: string;
  taskTypeId?: number;
  taskTypeName?: string;
  assignedTo?: number;
  assignedUserName?: string;
  dueDate?: string;
  isCompleted?: boolean;
  isStarted?: boolean;
  status: string;
  createdAt: string;
}

export interface LeadSummary {
  id: number;
  title: string;
  customerId: number;
  customerName?: string;
  status: string;
  expectedCloseDate?: string;
  value?: number;
  createdAt: string;
}

export interface Attachment {
  id?: number;
  url: string;
  fileName: string;
  fileType: string;
  displayOrder: number;
  bytes?: string | number[]; // Base64 string or byte array for file download
  createdAt?: string;
  updatedAt?: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Auth Types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  email: string;
  roles: string;
  message: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ForgotPasswordResponse {
  success: boolean;
  message: string;
  email: string;
}

// UI Types
export interface LoadingState {
  isLoading: boolean;
  error?: string;
}

export interface TableColumn<T> {
  key: keyof T;
  label: string;
  sortable?: boolean;
  render?: (value: any, record: T) => React.ReactNode;
}

export interface FilterOption {
  label: string;
  value: string | number;
}

export interface SortOption {
  field: string;
  direction: 'asc' | 'desc';
}

// Form Types
export interface CustomerFormData {
  name: string;
  email?: string;
  phone?: string;
  company?: string;
  source?: string;
  notes?: string;
  isActive: boolean;
}

export interface LeadFormData {
  id?: number;
  title: string;
  customerId: number;
  assignedTo?: number;
  status: string;
  currentStageId?: number;
  expectedCloseDate?: string;
  value?: number;
  source?: string;
  additionalNotes?: string;
  // Legacy properties for backward compatibility
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  company?: string;
  stage?: string;
  estimatedValue?: number;
  notes?: string;
}

export interface TaskFormData {
  id?: number;
  leadId?: number;
  customerId?: number;
  assignedTo?: string;
  taskTypeId?: number;
  title: string;
  description?: string;
  dueDate?: string;
  isStarted?: boolean;
  // Additional properties that components expect
  status: string;
  priority?: string;
  // New boolean fields for status mapping
  isCancelled?: boolean;
}

// Statistics Types
export interface CustomerStats {
  totalCustomers: number;
  activeCustomers: number;
  inactiveCustomers: number;
  newCustomersThisMonth: number;
}

export interface LeadStats {
  totalLeads: number;
  qualifiedLeads: number;
  convertedLeads: number;
  pipelineValue: number;
  stageBreakdown: { [key: string]: number };
}

export interface TaskStats {
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
  dueSoonTasks: number;
  typeBreakdown: { [key: string]: number };
} 