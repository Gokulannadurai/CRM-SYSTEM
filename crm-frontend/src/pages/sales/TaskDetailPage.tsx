import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchTaskById, deleteTask } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
import AttachmentListComponent from '../../components/common/AttachmentList';
import { getTaskStatus, getTaskStatusDisplay } from '../../utils/taskUtils';
import { useDropdownData } from '../../hooks/useDropdownData';

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

const StatusBadge = styled.span<{ status: string }>`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: ${props => {
    switch (props.status) {
      case 'PENDING': return '#fff3cd';
      case 'IN_PROGRESS': return '#d1ecf1';
      case 'COMPLETED': return '#d4edda';
      case 'CANCELLED': return '#f8d7da';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.status) {
      case 'PENDING': return '#856404';
      case 'IN_PROGRESS': return '#0c5460';
      case 'COMPLETED': return '#155724';
      case 'CANCELLED': return '#721c24';
      default: return '#6c757d';
    }
  }};
`;

const PriorityBadge = styled.span<{ priority: string }>`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: ${props => {
    switch (props.priority) {
      case 'HIGH': return '#f8d7da';
      case 'MEDIUM': return '#fff3cd';
      case 'LOW': return '#d1ecf1';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.priority) {
      case 'HIGH': return '#721c24';
      case 'MEDIUM': return '#856404';
      case 'LOW': return '#0c5460';
      default: return '#6c757d';
    }
  }};
`;

const TaskDescription = styled.div`
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin: 16px 0;
  line-height: 1.6;
  color: #2c3e50;
`;

const RelatedLead = styled.div`
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-top: 16px;
`;

const LeadName = styled.h4`
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
`;

const LeadInfo = styled.div`
  font-size: 0.9rem;
  color: #6c757d;
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

const TaskDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedTask, loading, error } = useSelector((state: RootState) => state.sales);
  const [deleteDialog, setDeleteDialog] = useState(false);
  
  // Get users data for mapping assignedTo ID to user name
  const { users, loading: usersLoading, fetchUsers } = useDropdownData();
  
  // Function to get user name by ID from users dropdown data
  const getUserNameById = (userId: number | string | null | undefined): string => {
    if (!userId) return 'Unassigned';
    
    // If users are still loading, show loading state
    if (usersLoading.users) {
      return 'Loading...';
    }
    
    const userIdNum = typeof userId === 'string' ? parseInt(userId) : userId;
    
    const user = users.find(u => u.id === userIdNum || u.user_id === userIdNum);
    
    if (user) {
      // Use firstName and lastName if available, otherwise fall back to name
      if (user.firstName || user.lastName) {
        const fullName = `${user.firstName || ''} ${user.lastName || ''}`.trim();
        return fullName || user.username || 'Unknown User';
      }
      return user.name || 'Unknown User';
    }
    
    return 'Unassigned';
  };

  useEffect(() => {
    if (id) {
      dispatch(fetchTaskById(parseInt(id)));
    }
  }, [dispatch, id]);
  
  // Fetch users data for mapping
  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const handleDeleteTask = async () => {
    if (!selectedTask) return;
    setDeleteDialog(true);
  };

  const confirmDeleteTask = async () => {
    if (!selectedTask) return;

    try {
      await dispatch(deleteTask(selectedTask.id));
      dispatch(addNotification({
        type: 'success',
        message: 'Task deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
      navigate('/tasks');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete task',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog(false);
    }
  };

  const cancelDeleteTask = () => {
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
          <ErrorTitle>Error loading task</ErrorTitle>
          <ErrorMessage>{error}</ErrorMessage>
          <BackButton to="/tasks">← Back to Tasks</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  if (!selectedTask) {
    return (
      <PageContainer>
        <ErrorContainer>
          <ErrorIcon>✅</ErrorIcon>
          <ErrorTitle>Task not found</ErrorTitle>
          <ErrorMessage>The task you're looking for doesn't exist or has been removed.</ErrorMessage>
          <BackButton to="/tasks">← Back to Tasks</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <div>
          <BackButton to="/tasks">← Back to Tasks</BackButton>
          <PageTitle>{selectedTask.title || 'Untitled Task'}</PageTitle>
        </div>
        <ActionButtons>
          <EditButton to={`/tasks/${selectedTask.id}/edit`}>
            Edit Task
          </EditButton>
          <DeleteButton onClick={handleDeleteTask}>
            Delete Task
          </DeleteButton>
        </ActionButtons>
      </PageHeader>

      <ContentGrid>
        <Card>
          <CardTitle>📋 Task Information</CardTitle>
          <InfoGrid>
            <InfoRow>
              <InfoLabel>Title:</InfoLabel>
              <InfoValue>{selectedTask.title || 'Untitled Task'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Status:</InfoLabel>
              <InfoValue>
                <StatusBadge status={getTaskStatus(selectedTask)}>
                  {getTaskStatusDisplay(getTaskStatus(selectedTask))}
                </StatusBadge>
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Priority:</InfoLabel>
              <InfoValue>
                <PriorityBadge priority={selectedTask.priority || 'MEDIUM'}>
                  {selectedTask.priority || 'MEDIUM'}
                </PriorityBadge>
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Assigned To:</InfoLabel>
              <InfoValue>{getUserNameById(selectedTask.assignedTo)}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Due Date:</InfoLabel>
              <InfoValue>
                {selectedTask.dueDate ? new Date(selectedTask.dueDate).toLocaleDateString() : 'No due date'}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Created:</InfoLabel>
              <InfoValue>
                {selectedTask.createdAt ? new Date(selectedTask.createdAt).toLocaleDateString() : 'N/A'}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Last Updated:</InfoLabel>
              <InfoValue>
                {selectedTask.updatedAt ? new Date(selectedTask.updatedAt).toLocaleDateString() : 'N/A'}
              </InfoValue>
            </InfoRow>
          </InfoGrid>

          {selectedTask.description && (
            <TaskDescription>
              <strong>Description:</strong><br />
              {selectedTask.description}
            </TaskDescription>
          )}
          
          {/* Attachments Section */}
          <AttachmentListComponent 
            attachments={selectedTask.attachments} 
            title="Task Attachments" 
          />
        </Card>

        <Card>
          <CardTitle>🎯 Related Lead</CardTitle>
          {selectedTask.relatedLead ? (
            <RelatedLead>
              <LeadName>{selectedTask.relatedLead.title || 'Untitled Lead'}</LeadName>
              <LeadInfo>
                <div>Customer: {selectedTask.relatedLead.customerName || `Customer ID: ${selectedTask.relatedLead.customerId}` || 'N/A'}</div>
                <div>Status: {selectedTask.relatedLead.status || 'N/A'}</div>
                <div>Expected Close Date: {selectedTask.relatedLead.expectedCloseDate ? new Date(selectedTask.relatedLead.expectedCloseDate).toLocaleDateString() : 'Not set'}</div>
                <div>Value: ${selectedTask.relatedLead.value?.toLocaleString() || 'N/A'}</div>
                <div>Created: {selectedTask.relatedLead.createdAt ? new Date(selectedTask.relatedLead.createdAt).toLocaleDateString() : 'N/A'}</div>
              </LeadInfo>
            </RelatedLead>
          ) : (
            <div style={{ textAlign: 'center', padding: '40px', color: '#6c757d' }}>
              <div style={{ fontSize: '3rem', marginBottom: '16px', opacity: 0.5 }}>🎯</div>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '8px', color: '#2c3e50' }}>No related lead</h3>
              <p style={{ fontSize: '1rem', margin: 0 }}>This task is not associated with any lead.</p>
            </div>
          )}
        </Card>
              </ContentGrid>

        <ConfirmationDialog
          isOpen={deleteDialog}
          title="Delete Task"
          message="Are you sure you want to delete this task? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          onConfirm={confirmDeleteTask}
          onCancel={cancelDeleteTask}
          type="danger"
        />
      </PageContainer>
    );
  };

export default TaskDetailPage; 