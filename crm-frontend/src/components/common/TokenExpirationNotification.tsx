import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { logout } from '../../store/slices/authSlice';
import { AppDispatch } from '../../store/store';

interface TokenExpirationNotificationProps {
  message?: string;
  onClose?: () => void;
}

/**
 * Token Expiration Notification Component
 * 
 * Displays a notification when the user's token has expired
 * and automatically redirects to login after a delay.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
const TokenExpirationNotification: React.FC<TokenExpirationNotificationProps> = ({
  message = "Your session has expired. Please log in again.",
  onClose
}) => {
  const dispatch = useDispatch<AppDispatch>();
  const [countdown, setCountdown] = useState(5);

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          // Redirect to login
          dispatch(logout());
          window.location.href = '/login';
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [dispatch]);

  const handleClose = () => {
    if (onClose) {
      onClose();
    } else {
      dispatch(logout());
      window.location.href = '/login';
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
        <div className="flex items-center mb-4">
          <div className="flex-shrink-0">
            <svg 
              className="h-6 w-6 text-red-600" 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" 
              />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-lg font-medium text-gray-900">
              Session Expired
            </h3>
          </div>
        </div>
        
        <div className="mb-4">
          <p className="text-sm text-gray-600">
            {message}
          </p>
          <p className="text-sm text-gray-500 mt-2">
            Redirecting to login in {countdown} seconds...
          </p>
        </div>
        
        <div className="flex justify-end space-x-3">
          <button
            onClick={handleClose}
            className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            Login Now
          </button>
        </div>
      </div>
    </div>
  );
};

export default TokenExpirationNotification; 