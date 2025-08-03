// API Configuration for CRM Microservices
export const API_CONFIG = {
  // Microservice URLs
  USER_SERVICE_URL: process.env.REACT_APP_USER_SERVICE_URL || 'http://localhost:8081',
  CUSTOMER_SERVICE_URL: process.env.REACT_APP_CUSTOMER_SERVICE_URL || 'http://localhost:8082',
  SALES_SERVICE_URL: process.env.REACT_APP_SALES_SERVICE_URL || 'http://localhost:8083',
  
  // API Settings
  TIMEOUT: parseInt(process.env.REACT_APP_API_TIMEOUT || '10000'),
  API_VERSION: 'v1',
  
  // Endpoints
  ENDPOINTS: {
    // User Service Endpoints
    USER: {
      LOGIN: '/users/login',
      REFRESH: '/users/refresh',
      ME: '/users/me',
      USERS: '/users',
      ROLES: '/roles',
      FORGOT_PASSWORD: '/users/forgot-password',
    },
    
    // Customer Service Endpoints
    CUSTOMER: {
      CUSTOMERS: '/customers',
      CUSTOMER_BY_ID: (id: number) => `/customers/${id}`,
      CUSTOMER_BY_EMAIL: (email: string) => `/customers/email/${email}`,
      CUSTOMER_STATUS: (id: number) => `/customers/${id}/status`,
      CUSTOMER_STATS: '/customers/stats',
      SEARCH: '/customers/search',
      INTERACTION_HISTORY: '/interaction-history',
      INTERACTION_BY_CUSTOMER: (customerId: number) => `/interaction-history/customer/${customerId}`,
      INTERACTION_RECENT: (customerId: number) => `/interaction-history/customer/${customerId}/recent`,
    },
    
    // Sales Service Endpoints
    SALES: {
      LEADS: '/leads',
      LEAD_BY_ID: (id: number) => `/leads/${id}`,
      LEAD_NEXT_STAGE: (id: number) => `/leads/${id}/next-stage`,
      LEAD_PREVIOUS_STAGE: (id: number) => `/leads/${id}/previous-stage`,
      LEAD_ASSIGN: (id: number) => `/leads/${id}/assign`,
      LEAD_BY_CUSTOMER: (customerId: number) => `/leads/customer/${customerId}`,
      LEAD_BY_ASSIGNED: (assignedTo: number) => `/leads/assigned/${assignedTo}`,
      LEAD_BY_STATUS: (status: string) => `/leads/status/${status}`,
      LEAD_BY_STAGE: (stageId: number) => `/leads/stage/${stageId}`,
      LEAD_OVERDUE: '/leads/overdue',
      LEAD_DUE_SOON: '/leads/due-soon',
      LEAD_STATS: '/leads/stats',
      LEAD_PIPELINE_STATS: '/leads/pipeline/stats',
      
      LEAD_STAGES: '/lead-stages',
      LEAD_STAGE_BY_ID: (id: number) => `/lead-stages/${id}`,
      
      TASKS: '/tasks',
      TASK_BY_ID: (id: number) => `/tasks/${id}`,
      TASK_COMPLETE: (id: number) => `/tasks/${id}/complete`,
      TASK_INCOMPLETE: (id: number) => `/tasks/${id}/incomplete`,
      TASK_STATUS: (id: number) => `/tasks/${id}/status`,
      TASK_ASSIGN: (id: number) => `/tasks/${id}/assign`,
      TASK_BY_LEAD: (leadId: number) => `/tasks/lead/${leadId}`,
      TASK_BY_CUSTOMER: (customerId: number) => `/tasks/customer/${customerId}`,
      TASK_BY_ASSIGNED: (assignedTo: number) => `/tasks/assigned/${assignedTo}`,
      TASK_BY_TYPE: (taskTypeId: number) => `/tasks/type/${taskTypeId}`,
      TASK_BY_STATUS: (isCompleted: boolean) => `/tasks/status/${isCompleted}`,
      TASK_OVERDUE: '/tasks/overdue',
      TASK_DUE_SOON: '/tasks/due-soon',
      TASK_STATS: '/tasks/stats',
      
      TASK_TYPES: '/task-types',
      TASK_TYPE_BY_ID: (id: number) => `/task-types/${id}`,
      TASK_TYPE_SEARCH: '/task-types/search',
      TASK_TYPE_EXISTS: (name: string) => `/task-types/exists/${name}`,
    },
  },
  
  // Response Formats
  RESPONSE_FORMATS: {
    SUCCESS: {
      success: true,
      message: '',
      data: null,
    },
    ERROR: {
      success: false,
      message: '',
      error: '',
      timestamp: '',
      path: '',
      details: {},
    },
  },
  
  // Error Codes
  ERROR_CODES: {
    VALIDATION_ERROR: 'VALIDATION_ERROR',
    UNAUTHORIZED: 'UNAUTHORIZED',
    FORBIDDEN: 'FORBIDDEN',
    NOT_FOUND: 'NOT_FOUND',
    CONFLICT: 'CONFLICT',
    UNPROCESSABLE_ENTITY: 'UNPROCESSABLE_ENTITY',
    INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR',
    RATE_LIMIT_EXCEEDED: 'RATE_LIMIT_EXCEEDED',
  },
  
  // HTTP Status Codes
  HTTP_STATUS: {
    OK: 200,
    CREATED: 201,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    CONFLICT: 409,
    UNPROCESSABLE_ENTITY: 422,
    TOO_MANY_REQUESTS: 429,
    INTERNAL_SERVER_ERROR: 500,
  },
};

export default API_CONFIG; 