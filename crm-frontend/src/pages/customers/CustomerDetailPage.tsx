import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchCustomerById, deleteCustomer, fetchInteractionHistory } from '../../store/slices/customerSlice';
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

const InteractionList = styled.div`
  max-height: 400px;
  overflow-y: auto;
`;

const InteractionItem = styled.div`
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #f8f9fa;

  &:last-child {
    margin-bottom: 0;
  }
`;

const InteractionHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
`;

const InteractionType = styled.span<{ type: string }>`
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.8rem;
  font-weight: 500;
  background: ${props => {
    switch (props.type) {
      case 'CALL': return '#e3f2fd';
      case 'EMAIL': return '#f3e5f5';
      case 'MEETING': return '#e8f5e8';
      case 'NOTE': return '#fff3e0';
      default: return '#f5f5f5';
    }
  }};
  color: ${props => {
    switch (props.type) {
      case 'CALL': return '#1976d2';
      case 'EMAIL': return '#7b1fa2';
      case 'MEETING': return '#388e3c';
      case 'NOTE': return '#f57c00';
      default: return '#6c757d';
    }
  }};
`;

const InteractionDate = styled.span`
  font-size: 0.9rem;
  color: #6c757d;
`;

const InteractionContent = styled.p`
  margin: 0;
  color: #2c3e50;
  line-height: 1.5;
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

const CustomerDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedCustomer, loading, error, interactionHistory } = useSelector((state: RootState) => state.customer);
  const [deleteDialog, setDeleteDialog] = useState(false);

  useEffect(() => {
    if (id) {
      dispatch(fetchCustomerById(parseInt(id)));
      dispatch(fetchInteractionHistory(parseInt(id)));
    }
  }, [dispatch, id]);

  const handleDeleteCustomer = async () => {
    if (!selectedCustomer) return;
    setDeleteDialog(true);
  };

  const confirmDeleteCustomer = async () => {
    if (!selectedCustomer) return;

    try {
      await dispatch(deleteCustomer(selectedCustomer.id));
      dispatch(addNotification({
        type: 'success',
        message: 'Customer deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
      navigate('/customers');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete customer',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog(false);
    }
  };

  const cancelDeleteCustomer = () => {
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
          <ErrorTitle>Error loading customer</ErrorTitle>
          <ErrorMessage>{error}</ErrorMessage>
          <BackButton to="/customers">← Back to Customers</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  if (!selectedCustomer) {
    return (
      <PageContainer>
        <ErrorContainer>
          <ErrorIcon>👤</ErrorIcon>
          <ErrorTitle>Customer not found</ErrorTitle>
          <ErrorMessage>The customer you're looking for doesn't exist or has been removed.</ErrorMessage>
          <BackButton to="/customers">← Back to Customers</BackButton>
        </ErrorContainer>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <div>
          <BackButton to="/customers">← Back to Customers</BackButton>
          <PageTitle>{selectedCustomer.name || ''}</PageTitle>
        </div>
        <ActionButtons>
          <EditButton to={`/customers/${selectedCustomer.id}/edit`}>
            Edit Customer
          </EditButton>
          <DeleteButton onClick={handleDeleteCustomer}>
            Delete Customer
          </DeleteButton>
        </ActionButtons>
      </PageHeader>

      <ContentGrid>
        <Card>
          <CardTitle>📋 Customer Information</CardTitle>
          <InfoGrid>
            <InfoRow>
              <InfoLabel>Name:</InfoLabel>
                                  <InfoValue>{selectedCustomer.name || ''}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Email:</InfoLabel>
              <InfoValue>{selectedCustomer.email}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Phone:</InfoLabel>
              <InfoValue>{selectedCustomer.phone || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Company:</InfoLabel>
              <InfoValue>{selectedCustomer.company || 'N/A'}</InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Status:</InfoLabel>
              <InfoValue>
                <StatusBadge isActive={selectedCustomer.isActive}>
                  {selectedCustomer.isActive ? 'Active' : 'Inactive'}
                </StatusBadge>
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Created:</InfoLabel>
              <InfoValue>
                {new Date(selectedCustomer.createdAt).toLocaleDateString()}
              </InfoValue>
            </InfoRow>
            <InfoRow>
              <InfoLabel>Last Updated:</InfoLabel>
              <InfoValue>
                {new Date(selectedCustomer.updatedAt).toLocaleDateString()}
              </InfoValue>
            </InfoRow>
          </InfoGrid>
        </Card>

        <Card>
          <CardTitle>📞 Interaction History</CardTitle>
          {interactionHistory && interactionHistory.length > 0 ? (
            <InteractionList>
              {interactionHistory.map((interaction) => (
                <InteractionItem key={interaction.id}>
                  <InteractionHeader>
                    <InteractionType type={interaction.interactionType || interaction.type}>
                      {interaction.interactionType || interaction.type}
                    </InteractionType>
                    <InteractionDate>
                      {new Date(interaction.interactionDate || interaction.createdAt).toLocaleDateString()}
                    </InteractionDate>
                  </InteractionHeader>
                  <InteractionContent>{interaction.notes}</InteractionContent>
                </InteractionItem>
              ))}
            </InteractionList>
          ) : (
            <EmptyState>
              <EmptyStateIcon>📝</EmptyStateIcon>
              <EmptyStateText>No interactions yet</EmptyStateText>
              <EmptyStateSubtext>
                Start building your relationship by adding interaction notes.
              </EmptyStateSubtext>
            </EmptyState>
          )}
        </Card>
              </ContentGrid>

        <ConfirmationDialog
          isOpen={deleteDialog}
          title="Delete Customer"
          message="Are you sure you want to delete this customer? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          onConfirm={confirmDeleteCustomer}
          onCancel={cancelDeleteCustomer}
          type="danger"
        />
      </PageContainer>
    );
  };

export default CustomerDetailPage; 