import TokenManager from './tokenManager';

/**
 * Handle token expiration by clearing tokens and redirecting to login
 */
export const handleTokenExpiration = (): void => {
  console.log('Token expired, clearing tokens and redirecting to login...');
  
  // Clear all tokens
  TokenManager.clearTokens();
  
  // Dispatch logout action if store is available
  if (window.store) {
    window.store.dispatch({ type: 'auth/logout/fulfilled' });
  }
  
  // Dispatch custom event for UI notification
  window.dispatchEvent(new CustomEvent('tokenExpired'));
  
  // Redirect to login page after a short delay to show notification
  setTimeout(() => {
    window.location.href = '/login';
  }, 2000);
};

/**
 * Check if error response indicates token expiration
 */
export const isTokenExpiredError = (error: any): boolean => {
  const errorData = error.response?.data;
  return errorData?.error === "TOKEN_EXPIRED" || 
         errorData?.errorType === "TOKEN_EXPIRED" ||
         errorData?.message?.includes("expired");
};

/**
 * Handle 403 Forbidden error - User doesn't have permission
 */
export const handleForbiddenError = (): void => {
  console.log('Access denied - User does not have permission, clearing tokens and redirecting to login...');
  
  // Clear all tokens
  TokenManager.clearTokens();
  
  // Dispatch logout action if store is available
  if (window.store) {
    window.store.dispatch({ type: 'auth/logout/fulfilled' });
  }
  
  // Redirect to login page
  window.location.href = '/login';
};

/**
 * Handle authentication errors
 */
export const handleAuthError = (error: any): void => {
  if (isTokenExpiredError(error)) {
    handleTokenExpiration();
  }
};

// Make store available globally
declare global {
  interface Window {
    store: any;
  }
} 