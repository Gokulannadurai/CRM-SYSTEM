import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchLeads, deleteLead } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
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

const PageTitle = styled.h1`
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const AddButton = styled(Link)`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
    text-decoration: none;
    color: white;
  }
`;

const SearchBar = styled.div`
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
`;

const SearchInput = styled.input`
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }
`;

const FilterSelect = styled.select`
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  background: white;

  &:focus {
    border-color: #667eea;
  }
`;

const LeadGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
`;

const LeadCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
`;

const LeadHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
`;

const LeadName = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const LeadStage = styled.span<{ stage: string }>`
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  background: ${props => {
    switch (props.stage) {
      case 'Lead': return '#e3f2fd';
      case 'Qualified': return '#e8f5e8';
      case 'Proposal': return '#f3e5f5';
      case 'Negotiation': return '#fff8e1';
      case 'Closed Won': return '#d4edda';
      case 'Closed Lost': return '#f8d7da';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.stage) {
      case 'Lead': return '#1976d2';
      case 'Qualified': return '#388e3c';
      case 'Proposal': return '#7b1fa2';
      case 'Negotiation': return '#fbc02d';
      case 'Closed Won': return '#155724';
      case 'Closed Lost': return '#721c24';
      default: return '#6c757d';
    }
  }};
`;

const LeadInfo = styled.div`
  margin-bottom: 16px;
`;

const InfoRow = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #6c757d;
`;

const InfoLabel = styled.span`
  font-weight: 500;
  min-width: 80px;
  margin-right: 8px;
`;

const InfoValue = styled.span`
  color: #2c3e50;
`;

const LeadActions = styled.div`
  display: flex;
  gap: 8px;
`;

const ActionButton = styled(Link)`
  padding: 8px 16px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
`;

const ViewButton = styled(ActionButton)`
  background: #e3f2fd;
  color: #1976d2;

  &:hover {
    background: #bbdefb;
    text-decoration: none;
    color: #1976d2;
  }
`;



const DeleteButton = styled.button`
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  background: #ffebee;
  color: #d32f2f;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #ffcdd2;
  }
`;

const EmptyState = styled.div`
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
`;

const EmptyStateIcon = styled.div`
  font-size: 4rem;
  margin-bottom: 16px;
  opacity: 0.5;
`;

const EmptyStateText = styled.h3`
  font-size: 1.5rem;
  margin-bottom: 8px;
  color: #2c3e50;
`;

const EmptyStateSubtext = styled.p`
  font-size: 1rem;
  margin-bottom: 24px;
`;

const PaginationContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 24px;
`;

const PaginationButton = styled.button<{ disabled?: boolean }>`
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: ${props => props.disabled ? '#f8f9fa' : 'white'};
  color: ${props => props.disabled ? '#6c757d' : '#495057'};
  border-radius: 6px;
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #e9ecef;
    border-color: #adb5bd;
  }
`;

const PageInfo = styled.span`
  color: #6c757d;
  font-size: 0.9rem;
`;

const LeadListPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const { leads, loading, error, totalElements } = useSelector((state: RootState) => state.sales);
  const [searchTerm, setSearchTerm] = useState('');
  const [stageFilter, setStageFilter] = useState('ALL');
  const [currentPage, setCurrentPage] = useState(0); // Changed to 0-based for API
  const itemsPerPage = 50; // Increased page size
  const [deleteDialog, setDeleteDialog] = useState<{ isOpen: boolean; leadId: number | null }>({
    isOpen: false,
    leadId: null
  });

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
    // Only fetch if leads array is empty or when navigating back
    if (leads.length === 0 || location.pathname === '/leads') {
      dispatch(fetchLeads({ page: currentPage, size: itemsPerPage }));
    }
  }, [dispatch, leads.length, location.pathname, currentPage, itemsPerPage]);
  
  // Refresh list when window gains focus
  useEffect(() => {
    const handleFocus = () => {
      dispatch(fetchLeads({ page: currentPage, size: itemsPerPage }));
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [dispatch, currentPage, itemsPerPage]);
  
  // Fetch users data for mapping
  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const filteredLeads = leads.filter(lead => {
    // Get customer name from lead.customerName or fallback to customer object
    const customerName = lead.customerName || lead.customer?.name || `${lead.firstName || ''} ${lead.lastName || ''}`.trim();
    const leadTitle = lead.title || '';
    
    const matchesSearch = customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         leadTitle.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStage = stageFilter === 'ALL' || lead.status === stageFilter;
    
    return matchesSearch && matchesStage;
  });

  // Use leads directly from Redux state (already paginated from server)
  const paginatedLeads = filteredLeads;
  
  // Get total pages from Redux state
  const totalPages = Math.ceil((totalElements || 0) / itemsPerPage);

  const handleDeleteLead = async (leadId: number) => {
    setDeleteDialog({ isOpen: true, leadId });
  };

  const confirmDeleteLead = async () => {
    if (!deleteDialog.leadId) return;
    
    try {
      await dispatch(deleteLead(deleteDialog.leadId));
      dispatch(addNotification({
        type: 'success',
        message: 'Lead deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete lead',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog({ isOpen: false, leadId: null });
    }
  };

  const cancelDeleteLead = () => {
    setDeleteDialog({ isOpen: false, leadId: null });
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    // Fetch leads for the new page
    dispatch(fetchLeads({ page: newPage, size: itemsPerPage }));
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <h2>Error loading leads</h2>
          <p>{error}</p>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Leads</PageTitle>
        <AddButton to="/leads/new">+ Add Lead</AddButton>
      </PageHeader>

      <SearchBar>
        <SearchInput
          type="text"
          placeholder="Search leads by customer name or title..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <FilterSelect
          value={stageFilter}
          onChange={(e) => setStageFilter(e.target.value)}
        >
          <option value="ALL">All Stages</option>
          <option value="Lead">Lead</option>
          <option value="Qualified">Qualified</option>
          <option value="Proposal">Proposal</option>
          <option value="Negotiation">Negotiation</option>
          <option value="Closed Won">Closed Won</option>
          <option value="Closed Lost">Closed Lost</option>
        </FilterSelect>
      </SearchBar>

      {paginatedLeads.length === 0 ? (
        <EmptyState>
          <EmptyStateIcon>🎯</EmptyStateIcon>
          <EmptyStateText>No leads found</EmptyStateText>
          <EmptyStateSubtext>
            {searchTerm || stageFilter !== 'ALL' 
              ? 'Try adjusting your search or filter criteria'
              : 'Get started by adding your first lead'
            }
          </EmptyStateSubtext>
          {!searchTerm && stageFilter === 'ALL' && (
            <AddButton to="/leads/new">+ Add Lead</AddButton>
          )}
        </EmptyState>
      ) : (
        <>
          <LeadGrid>
            {paginatedLeads.map((lead) => (
              <LeadCard key={lead.id}>
                <LeadHeader>
                  <LeadName>{lead.title || 'Untitled Lead'}</LeadName>
                  <LeadStage stage={lead.status}>{lead.status?.replace('_', ' ') || 'Unknown'}</LeadStage>
                </LeadHeader>
                
                <LeadInfo>
                  <InfoRow>
                    <InfoLabel>Customer:</InfoLabel>
                    <InfoValue>{lead.customerName || lead.customer?.name || `${lead.firstName || ''} ${lead.lastName || ''}`.trim() || 'N/A'}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Assigned To:</InfoLabel>
                    <InfoValue>{getUserNameById(lead.assignedTo)}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Expected Close:</InfoLabel>
                    <InfoValue>{lead.expectedCloseDate ? new Date(lead.expectedCloseDate).toLocaleDateString() : 'N/A'}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Value:</InfoLabel>
                    <InfoValue>${lead.value?.toLocaleString() || lead.estimatedValue?.toLocaleString() || 'N/A'}</InfoValue>
                  </InfoRow>
                  {lead.leadId && (
                    <InfoRow>
                      <InfoLabel>Related Lead:</InfoLabel>
                      <InfoValue>Lead #{lead.leadId}</InfoValue>
                    </InfoRow>
                  )}
                </LeadInfo>

                <LeadActions>
                  <ViewButton to={`/leads/${lead.id}`}>
                    View
                  </ViewButton>
                  <DeleteButton onClick={() => handleDeleteLead(lead.id)}>
                    Delete
                  </DeleteButton>
                </LeadActions>
              </LeadCard>
            ))}
          </LeadGrid>

          <PaginationContainer>
            <PaginationButton
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              Previous
            </PaginationButton>
            <PageInfo>
              Page {currentPage + 1} of {totalPages} ({totalElements || 0} leads)
            </PageInfo>
            <PaginationButton
              disabled={currentPage >= totalPages - 1}
              onClick={() => handlePageChange(currentPage + 1)}
            >
              Next
            </PaginationButton>
          </PaginationContainer>
        </>
      )}

      <ConfirmationDialog
        isOpen={deleteDialog.isOpen}
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

export default LeadListPage; 