import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState, useAppDispatch } from './store/store';
import { getCurrentUser } from './store/slices/authSlice';
import { addNotification } from './store/slices/uiSlice';
import TokenManager from './utils/tokenManager';
import styled from 'styled-components';

// Layout Components
import Sidebar from './components/layout/Sidebar';
import Header from './components/layout/Header';

// Auth Components
import LoginPage from './pages/auth/LoginPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';

// Dashboard Components
import DashboardPage from './pages/dashboard/DashboardPage';

// Customer Components
import CustomerListPage from './pages/customers/CustomerListPage';
import CustomerDetailPage from './pages/customers/CustomerDetailPage';
import CustomerFormPage from './pages/customers/CustomerFormPage';

// Sales Components
import LeadListPage from './pages/sales/LeadListPage';
import LeadDetailPage from './pages/sales/LeadDetailPage';
import LeadFormPage from './pages/sales/LeadFormPage';
import TaskListPage from './pages/sales/TaskListPage';
import TaskDetailPage from './pages/sales/TaskDetailPage';
import TaskFormPage from './pages/sales/TaskFormPage';
import UserListPage from './pages/users/UserListPage';
import UserDetailPage from './pages/users/UserDetailPage';
import UserFormPage from './pages/users/UserFormPage';

// Common Components
import LoadingSpinner from './components/common/LoadingSpinner';
import NotificationContainer from './components/common/NotificationContainer';
import TokenExpirationNotification from './components/common/TokenExpirationNotification';

// Styled components for the new layout
const AppContainer = styled.div`
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
`;

const MainLayout = styled.div`
  display: flex;
  flex: 1;
  overflow: hidden;
`;

const ContentArea = styled.main<{ sidebarOpen: boolean }>`
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f5f5;
  margin-left: ${props => props.sidebarOpen ? '250px' : '60px'};
  margin-top: 60px;
  height: calc(100vh - 60px);
`;

const App: React.FC = () => {
  const dispatch = useAppDispatch();
  const { isAuthenticated, isLoading } = useSelector((state: RootState) => state.auth);
  const { sidebarOpen } = useSelector((state: RootState) => state.ui);
  const [showTokenExpiration, setShowTokenExpiration] = React.useState(false);

  useEffect(() => {
    // Check if user is authenticated on app load
    if (TokenManager.isAuthenticated() && !isAuthenticated) {
      dispatch(getCurrentUser());
    }
  }, [dispatch, isAuthenticated]);

  useEffect(() => {
    // Add welcome notification on first load
    if (isAuthenticated) {
      dispatch(addNotification({
        type: 'success',
        message: 'Welcome to CRM System!',
        title: 'Login Successful',
        duration: 3000,
      }));
    }
  }, [dispatch, isAuthenticated]);

  // Listen for token expiration events
  useEffect(() => {
    const handleTokenExpiration = () => {
      setShowTokenExpiration(true);
    };

    // Add event listener for token expiration
    window.addEventListener('tokenExpired', handleTokenExpiration);

    return () => {
      window.removeEventListener('tokenExpired', handleTokenExpiration);
    };
  }, []);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <AppContainer>
      <NotificationContainer />
      
      {/* Token Expiration Notification */}
      {showTokenExpiration && (
        <TokenExpirationNotification
          message="Your session has expired. Please log in again."
          onClose={() => setShowTokenExpiration(false)}
        />
      )}
      
      {isAuthenticated ? (
        <>
          <Header />
          <MainLayout>
            <Sidebar isOpen={sidebarOpen} />
            <ContentArea sidebarOpen={sidebarOpen}>
              <Routes>
                <Route path="/" element={<DashboardPage />} />
                
                {/* Customer Routes */}
                <Route path="/customers" element={<CustomerListPage />} />
                <Route path="/customers/new" element={<CustomerFormPage />} />
                <Route path="/customers/:id" element={<CustomerDetailPage />} />
                <Route path="/customers/:id/edit" element={<CustomerFormPage />} />
                
                {/* Sales Routes */}
                <Route path="/leads" element={<LeadListPage />} />
                <Route path="/leads/new" element={<LeadFormPage />} />
                <Route path="/leads/:id" element={<LeadDetailPage />} />
                <Route path="/leads/:id/edit" element={<LeadFormPage />} />
                
                <Route path="/tasks" element={<TaskListPage />} />
                <Route path="/tasks/new" element={<TaskFormPage />} />
                <Route path="/tasks/:id" element={<TaskDetailPage />} />
                <Route path="/tasks/:id/edit" element={<TaskFormPage />} />
                
                {/* User Routes */}
                <Route path="/users" element={<UserListPage />} />
                <Route path="/users/new" element={<UserFormPage />} />
                <Route path="/users/:id" element={<UserDetailPage />} />
                <Route path="/users/:id/edit" element={<UserFormPage />} />
                
                {/* Catch all route */}
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </ContentArea>
          </MainLayout>
        </>
      ) : (
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      )}
    </AppContainer>
  );
};

export default App; 