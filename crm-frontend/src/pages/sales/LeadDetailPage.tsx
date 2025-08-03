import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchLeadById, deleteLead } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
import AttachmentListComponent from '../../components/common/AttachmentList';
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

const StageBadge = styled.span<{ stage: string }>`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: ${props => {
    switch (props.stage) {
      case 'NEW': return '#e3f2fd';
      case 'CONTACTED': return '#fff3e0';
      case 'QUALIFIED': return '#e8f5e8';
      case 'PROPOSAL': return '#f3e5f5';
      case 'NEGOTIATION': return '#fff8e1';
      case 'CLOSED_WON': return '#d4edda';
      case 'CLOSED_LOST': return '#f8d7da';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.stage) {
      case 'NEW': return '#1976d2';
      case 'CONTACTED': return '#f57c00';
      case 'QUALIFIED': return '#388e3c';
      case 'PROPOSAL': return '#7b1fa2';
      case 'NEGOTIATION': return '#fbc02d';
      case 'CLOSED_WON': return '#155724';
      case 'CLOSED_LOST': return '#721c24';
      default: return '#6c757d';
    }
  }};
`;

const TaskList = styled.div`
  max-height: 400px;
  overflow-y: auto;
`;

const TaskItem = styled.div`
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #f8f9fa;

  &:last-child {
    margin-bottom: 0;
  }
`;

const TaskHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
`;

const TaskTitle = styled.h4`
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const TaskStatus = styled.span<{ status: string }>`
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.8rem;
  font-weight: 500;
  background: ${props => {
    switch (props.status) {
      case 'PENDING': return '#fff3cd';
      case 'IN_PROGRESS': return '#d1ecf1';
      case 'COMPLETED': return '#d4edda';
      case 'CANCELLED': return '#f8d7da';
      default: return '#f5f5f5';
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

const TaskDetails = styled.div`
  font-size: 0.9rem;
  color: #6c757d;
`;

const EmptyState = styled.div`
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
`;

const EmptyStateIcon = styled.div`
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
`;

const EmptyStateText = styled.h3`
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #2c3e50;
`;

const EmptyStateSubtext = styled.p`
  font-size: 1rem;
  margin: 0;
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

const LeadDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedLead, loading, error } = useSelector((state: RootState) => state.sales);
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
      dispatch(fetchLeadById(parseInt(id)));
    }
  }, [dispatch, id]);
  
  // Fetch users data for mapping
  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const handleDeleteLead = async () => {
    if (!selectedLead) return;
    setDeleteDialog(true);
  };

  const confirmDeleteLead = async () => {
    if (!selectedLead) return;

    try {
      await dispatch(deleteLead(selectedLead.id));
      dispatch(addNotification({
        type: 'success',
        message: 'Lead deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
      navigate('/leads');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete lead',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog(false);
    }
  };

  const cancelDeleteLead = () => {
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
          <ErrorTitle>Error loading lead</ErrorTitle>
          <ErrorMessage>{error}</ErrorMessage>
          <BackButton to="/leads">← Back to Leads</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  if (!selectedLead) {
    return (
      <PageContainer>
        <ErrorContainer>
          <ErrorIcon>🎯</ErrorIcon>
          <ErrorTitle>Lead not found</ErrorTitle>
          <ErrorMessage>The lead you're looking for doesn't exist or has been removed.</ErrorMessage>
          <BackButton to="/leads">← Back to Leads</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <div>
          <BackButton to="/leads">← Back to Leads</BackButton>
          <PageTitle>{selectedLead.title || 'Unnamed Lead'}</PageTitle>
        </div>
        <ActionButtons>
          <EditButton to={`/leads/${selectedLead.id}/edit`}>
            Edit Lead
          </EditButton>
          <DeleteButton onClick={handleDeleteLead}>
            Delete Lead
          </DeleteButton>
        </ActionButtons>
      </PageHeader>

      <ContentGrid>
        <Card>
          <CardTitle>📋 Lead Information</CardTitle>
          <InfoGrid>
            <InfoRow>
              <InfoLabel>Title:</InfoLabel>
              <InfoValue>{selectedLead.title || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Customer:</InfoLabel>
              <InfoValue>{selectedLead.customerName || `Customer ID: ${selectedLead.customerId}` || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Assigned To:</InfoLabel>
              <InfoValue>{getUserNameById(selectedLead.assignedTo)}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Status:</InfoLabel>
              <InfoValue>
                <StageBadge stage={selectedLead.status || selectedLead.stage || 'Unknown'}>
                  {(selectedLead.status || selectedLead.stage || 'Unknown').replace('_', ' ')}
                </StageBadge>
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Current Stage ID:</InfoLabel>
              <InfoValue>{selectedLead.currentStageId || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Expected Close Date:</InfoLabel>
              <InfoValue>
                {selectedLead.expectedCloseDate ? new Date(selectedLead.expectedCloseDate).toLocaleDateString() : 'Not set'}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Value:</InfoLabel>
              <InfoValue>${selectedLead.value?.toLocaleString() || selectedLead.estimatedValue?.toLocaleString() || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Additional Notes:</InfoLabel>
              <InfoValue>{selectedLead.additionalNotes || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Created:</InfoLabel>
              <InfoValue>
                {new Date(selectedLead.createdAt).toLocaleDateString()}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Last Updated:</InfoLabel>
              <InfoValue>
                {new Date(selectedLead.updatedAt).toLocaleDateString()}
              </InfoValue>
            </InfoRow>
          </InfoGrid>
          
          {/* Attachments Section */}
          {selectedLead.attachments && selectedLead.attachments.length > 0 && (
            <AttachmentListComponent 
              attachments={selectedLead.attachments} 
              title="Lead Attachments" 
            />
          )}
        </Card>

        <Card>
          <CardTitle>✅ Related Tasks</CardTitle>
          {selectedLead.relatedTasks && selectedLead.relatedTasks.length > 0 ? (
            <TaskList>
              {selectedLead.relatedTasks.map((task) => (
                <TaskItem key={task.id}>
                  <TaskHeader>
                    <TaskTitle>{task.title}</TaskTitle>
                    <TaskStatus status={task.status}>{task.status.replace('_', ' ')}</TaskStatus>
                  </TaskHeader>
                  <TaskDetails>
                    <div><strong>Type:</strong> {task.taskTypeName || 'N/A'}</div>
                    <div><strong>Assigned to:</strong> {task.assignedUserName || 'Unassigned'}</div>
                    <div><strong>Due:</strong> {task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No due date'}</div>
                    <div><strong>Status:</strong> {task.status}</div>
                    {task.description && <div><strong>Description:</strong> {task.description}</div>}
                  </TaskDetails>
                </TaskItem>
              ))}
            </TaskList>
          ) : (
            <EmptyState>
              <EmptyStateIcon>✅</EmptyStateIcon>
              <EmptyStateText>No tasks yet</EmptyStateText>
              <EmptyStateSubtext>
                Create tasks to track your progress with this lead.
              </EmptyStateSubtext>
            </EmptyState>
          )}
        </Card>
              </ContentGrid>

        <ConfirmationDialog
          isOpen={deleteDialog}
          title="Delete Lead"
          message="Are you sure you want to delete this lead? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          onConfirm={confirmDeleteLead}
          onCancel={cancelDeleteLead}
          type="danger"
        />
      </PageContainer>
    );
  };

export default LeadDetailPage; 