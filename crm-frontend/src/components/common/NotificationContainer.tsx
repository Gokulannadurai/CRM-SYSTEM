import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import styled, { keyframes } from 'styled-components';
import { RootState } from '../../store/store';
import { removeNotification } from '../../store/slices/uiSlice';

const slideIn = keyframes`
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
`;

const slideOut = keyframes`
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
`;

const NotificationWrapper = styled.div`
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const NotificationItem = styled.div<{ type: string; isVisible: boolean }>`
  background: ${props => {
    switch (props.type) {
      case 'success': return '#4caf50';
      case 'error': return '#f44336';
      case 'warning': return '#ff9800';
      case 'info': return '#2196f3';
      default: return '#333';
    }
  }};
  color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 300px;
  max-width: 400px;
  animation: ${props => props.isVisible ? slideIn : slideOut} 0.3s ease-in-out;
  display: flex;
  align-items: flex-start;
  gap: 10px;
`;

const NotificationIcon = styled.span`
  font-size: 1.2rem;
  margin-top: 2px;
`;

const NotificationContent = styled.div`
  flex: 1;
`;

const NotificationTitle = styled.div`
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 0.9rem;
`;

const NotificationMessage = styled.div`
  font-size: 0.85rem;
  opacity: 0.9;
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0;
  margin: 0;
  opacity: 0.7;
  transition: opacity 0.2s;

  &:hover {
    opacity: 1;
  }
`;

const NotificationContainer: React.FC = () => {
  const dispatch = useDispatch();
  const { notifications } = useSelector((state: RootState) => state.ui);

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'success': return '✅';
      case 'error': return '❌';
      case 'warning': return '⚠️';
      case 'info': return 'ℹ️';
      default: return '📢';
    }
  };

  const handleClose = (id: string) => {
    dispatch(removeNotification(id));
  };

  useEffect(() => {
    notifications.forEach(notification => {
      if (notification.duration) {
        const timer = setTimeout(() => {
          dispatch(removeNotification(notification.id));
        }, notification.duration);

        return () => clearTimeout(timer);
      }
    });
  }, [notifications, dispatch]);

  return (
    <NotificationWrapper>
      {notifications.map((notification) => (
        <NotificationItem
          key={notification.id}
          type={notification.type}
          isVisible={true}
        >
          <NotificationIcon>
            {getNotificationIcon(notification.type)}
          </NotificationIcon>
          <NotificationContent>
            {notification.title && (
              <NotificationTitle>{notification.title}</NotificationTitle>
            )}
            <NotificationMessage>{notification.message}</NotificationMessage>
          </NotificationContent>
          <CloseButton onClick={() => handleClose(notification.id)}>
            ×
          </CloseButton>
        </NotificationItem>
      ))}
    </NotificationWrapper>
  );
};

export default NotificationContainer; 