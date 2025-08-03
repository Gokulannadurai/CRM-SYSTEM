import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchUserById, deleteUser, refreshList } from '../../store/slices/userSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';

const PageContainer = styled.div`
  padding: 24px;
  background: #f8f9fa;
  min-height: calc(100vh - 80px);
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
`;

const BackButton = styled(Link)`
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;

  &:hover {
    color: #5a6fd8;
    text-decoration: none;
  }
`;

const PageTitle = styled.h1`
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const ActionButtons = styled.div`
  display: flex;
  gap: 12px;
`;

const ActionButton = styled(Link)`
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
`;

const EditButton = styled(ActionButton)`
  background: #fff3e0;
  color: #f57c00;

  &:hover {
    background: #ffe0b2;
    text-decoration: none;
    color: #f57c00;
  }
`;

const DeleteButton = styled.button`
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  background: #ffebee;
  color: #d32f2f;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #ffcdd2;
  }
`;

const ContentGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const Card = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const CardTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
`;

const InfoGrid = styled.div`
  display: grid;
  gap: 16px;
`;

const InfoRow = styled.div`
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
`;

const InfoLabel = styled.span`
  font-weight: 600;
  color: #6c757d;
  min-width: 120px;
  margin-right: 16px;
`;

const InfoValue = styled.span`
  color: #2c3e50;
  flex: 1;
`;

const StatusBadge = styled.span<{ isActive: boolean }>`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: ${props => props.isActive ? '#d4edda' : '#f8d7da'};
  color: ${props => props.isActive ? '#155724' : '#721c24'};
`;

const RoleBadge = styled.span`
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  background: #e3f2fd;
  color: #1976d2;
  margin-right: 8px;
  margin-bottom: 4px;
  display: inline-block;
`;

const RolesContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
`;

const ErrorContainer = styled.div`
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
`;

const ErrorIcon = styled.div`
  font-size: 4rem;
  margin-bottom: 16px;
  opacity: 0.5;
`;

const ErrorTitle = styled.h2`
  font-size: 1.8rem;
  margin-bottom: 8px;
  color: #2c3e50;
`;

const ErrorMessage = styled.p`
  font-size: 1.1rem;
  margin-bottom: 24px;
`;

const UserDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedUser, loading, error } = useSelector((state: RootState) => state.user);
  const [deleteDialog, setDeleteDialog] = useState(false);

  useEffect(() => {
    if (id) {
      dispatch(fetchUserById(parseInt(id)));
    }
  }, [dispatch, id]);

  const handleDeleteUser = async () => {
    if (!selectedUser) return;
    setDeleteDialog(true);
  };

  const confirmDeleteUser = async () => {
    if (!selectedUser) return;

    try {
      await dispatch(deleteUser(selectedUser.user_id || selectedUser.id || 0));
      dispatch(addNotification({
        type: 'success',
        message: 'User deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
      // Navigate to users list and trigger a refresh
      navigate('/users', { replace: true });
      // Dispatch a refresh action to reload the user list
      dispatch(refreshList());
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete user',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog(false);
    }
  };

  const cancelDeleteUser = () => {
    setDeleteDialog(false);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <PageContainer>
        <ErrorContainer>
          <ErrorIcon>⚠️</ErrorIcon>
          <ErrorTitle>Error loading user</ErrorTitle>
          <ErrorMessage>{error}</ErrorMessage>
          <BackButton to="/users">← Back to Users</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  if (!selectedUser) {
    return (
      <PageContainer>
        <ErrorContainer>
          <ErrorIcon>👤</ErrorIcon>
          <ErrorTitle>User not found</ErrorTitle>
          <ErrorMessage>The user you're looking for doesn't exist or has been removed.</ErrorMessage>
          <BackButton to="/users">← Back to Users</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <div>
          <BackButton to="/users">← Back to Users</BackButton>
                     <PageTitle>{selectedUser.first_name || selectedUser.firstName || ''} {selectedUser.last_name || selectedUser.lastName || ''}</PageTitle>
        </div>
        <ActionButtons>
                     <EditButton to={`/users/${selectedUser.user_id || selectedUser.id}/edit`}>
            Edit User
          </EditButton>
          <DeleteButton onClick={handleDeleteUser}>
            Delete User
          </DeleteButton>
        </ActionButtons>
      </PageHeader>

      <ContentGrid>
        <Card>
          <CardTitle>👤 Personal Information</CardTitle>
          <InfoGrid>
                         <InfoRow>
               <InfoLabel>Full Name:</InfoLabel>
               <InfoValue>{selectedUser.first_name || selectedUser.firstName || ''} {selectedUser.last_name || selectedUser.lastName || ''}</InfoValue>
             </InfoRow>
            <InfoRow>
              <InfoLabel>Username:</InfoLabel>
              <InfoValue>{selectedUser.username}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Email:</InfoLabel>
              <InfoValue>{selectedUser.email}</InfoValue>
            </InfoRow>
                         <InfoRow>
               <InfoLabel>Mobile:</InfoLabel>
               <InfoValue>{selectedUser.mobile_number || selectedUser.mobileNumber || 'Not provided'}</InfoValue>
             </InfoRow>
            <InfoRow>
              <InfoLabel>Status:</InfoLabel>
              <InfoValue>
                <StatusBadge isActive={selectedUser.is_active || selectedUser.isActive || false}>
                  {selectedUser.is_active || selectedUser.isActive ? 'Active' : 'Inactive'}
                </StatusBadge>
              </InfoValue>
            </InfoRow>
          </InfoGrid>
        </Card>

        <Card>
          <CardTitle>🔐 Account Information</CardTitle>
          <InfoGrid>
            <InfoRow>
              <InfoLabel>Roles:</InfoLabel>
              <InfoValue>
                <RolesContainer>
                  {selectedUser.roles && selectedUser.roles.length > 0 ? (
                    selectedUser.roles.map((role) => (
                      <RoleBadge key={role.id}>{role.name}</RoleBadge>
                    ))
                  ) : (
                    <span>No roles assigned</span>
                  )}
                </RolesContainer>
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Created:</InfoLabel>
              <InfoValue>
                {new Date(selectedUser.createdAt).toLocaleDateString()} at{' '}
                {new Date(selectedUser.createdAt).toLocaleTimeString()}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Last Updated:</InfoLabel>
              <InfoValue>
                {new Date(selectedUser.updatedAt).toLocaleDateString()} at{' '}
                {new Date(selectedUser.updatedAt).toLocaleTimeString()}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Last Login:</InfoLabel>
              <InfoValue>
                {selectedUser.lastLoginAt ? (
                  <>
                    {new Date(selectedUser.lastLoginAt).toLocaleDateString()} at{' '}
                    {new Date(selectedUser.lastLoginAt).toLocaleTimeString()}
                  </>
                ) : (
                  'Never logged in'
                )}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Deleted:</InfoLabel>
              <InfoValue>
                <StatusBadge isActive={!selectedUser.isDeleted}>
                  {selectedUser.isDeleted ? 'Yes' : 'No'}
                </StatusBadge>
              </InfoValue>
            </InfoRow>
          </InfoGrid>
        </Card>
      </ContentGrid>

      <ConfirmationDialog
        isOpen={deleteDialog}
        title="Delete User"
        message="Are you sure you want to delete this user? This action cannot be undone."
        confirmText="Delete"
        cancelText="Cancel"
        onConfirm={confirmDeleteUser}
        onCancel={cancelDeleteUser}
        type="danger"
      />
    </PageContainer>
  );
};

export default UserDetailPage; 