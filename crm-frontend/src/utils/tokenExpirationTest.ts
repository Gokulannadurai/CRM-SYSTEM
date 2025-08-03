import { handleTokenExpiration } from './authUtils';

/**
 * Test utility to simulate token expiration
 * This can be used for testing the token expiration flow
 */
export const testTokenExpiration = (): void => {
  console.log('Testing token expiration...');
  handleTokenExpiration();
};

// Add to window for testing in browser console
declare global {
  interface Window {
    testTokenExpiration: () => void;
  }
}

if (typeof window !== 'undefined') {
  window.testTokenExpiration = testTokenExpiration;
} 