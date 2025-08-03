import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchCustomers, deleteCustomer } from '../../store/slices/customerSlice';
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

const CustomerGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
`;

const CustomerCard = styled.div`
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

const CustomerHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
`;

const CustomerName = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const CustomerStatus = styled.span<{ isActive: boolean }>`
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  background: ${props => props.isActive ? '#d4edda' : '#f8d7da'};
  color: ${props => props.isActive ? '#155724' : '#721c24'};
`;

const CustomerInfo = styled.div`
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

const CustomerActions = styled.div`
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

const CustomerListPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const { customers, loading, error, totalElements } = useSelector((state: RootState) => state.customer);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ACTIVE');
  const [currentPage, setCurrentPage] = useState(0); // Changed to 0-based for API
  const itemsPerPage = 50; // Increased page size
  const [deleteDialog, setDeleteDialog] = useState<{ isOpen: boolean; customerId: number | null }>({
    isOpen: false,
    customerId: null
  });

  useEffect(() => {
    // Only fetch if customers array is empty or when navigating back
    if (customers.length === 0 || location.pathname === '/customers') {
      dispatch(fetchCustomers({ page: currentPage, size: itemsPerPage }));
    }
  }, [dispatch, customers.length, location.pathname, currentPage, itemsPerPage]);

  // Refresh list when window gains focus
  useEffect(() => {
    const handleFocus = () => {
      dispatch(fetchCustomers({ page: currentPage, size: itemsPerPage }));
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [dispatch, currentPage, itemsPerPage]);

  const filteredCustomers = customers.filter(customer => {
    const matchesSearch = (customer.name?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
                         (customer.email?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
                         (customer.company?.toLowerCase() || '').includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === 'ALL' || 
                         (statusFilter === 'ACTIVE' && customer.isActive) ||
                         (statusFilter === 'INACTIVE' && !customer.isActive);
    
    return matchesSearch && matchesStatus;
  });

  // Use customers directly from Redux state (already paginated from server)
  const paginatedCustomers = filteredCustomers;
  
  // Get total pages from Redux state
  const totalPages = Math.ceil((totalElements || 0) / itemsPerPage);

  const handleDeleteCustomer = async (customerId: number) => {
    setDeleteDialog({ isOpen: true, customerId });
  };

  const confirmDeleteCustomer = async () => {
    if (!deleteDialog.customerId) return;
    
    try {
      await dispatch(deleteCustomer(deleteDialog.customerId));
      dispatch(addNotification({
        type: 'success',
        message: 'Customer deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete customer',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog({ isOpen: false, customerId: null });
    }
  };

  const cancelDeleteCustomer = () => {
    setDeleteDialog({ isOpen: false, customerId: null });
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    // Fetch customers for the new page
    dispatch(fetchCustomers({ page: newPage, size: itemsPerPage }));
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <h2>Error loading customers</h2>
          <p>{error}</p>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Customers</PageTitle>
        <AddButton to="/customers/new">+ Add Customer</AddButton>
      </PageHeader>

      <SearchBar>
        <SearchInput
          type="text"
          placeholder="Search customers by name, email, or company..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <FilterSelect
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
          <option value="ALL">All Customers</option>
        </FilterSelect>
      </SearchBar>

      {paginatedCustomers.length === 0 ? (
        <EmptyState>
          <EmptyStateIcon>👥</EmptyStateIcon>
          <EmptyStateText>No customers found</EmptyStateText>
          <EmptyStateSubtext>
            {searchTerm || statusFilter !== 'ALL' 
              ? 'Try adjusting your search or filter criteria'
              : 'Get started by adding your first customer'
            }
          </EmptyStateSubtext>
          {!searchTerm && statusFilter === 'ALL' && (
            <AddButton to="/customers/new">+ Add Customer</AddButton>
          )}
        </EmptyState>
      ) : (
        <>
          <CustomerGrid>
            {paginatedCustomers.map((customer) => (
              <CustomerCard key={customer.id}>
                <CustomerHeader>
                  <CustomerName>{customer.name || ''}</CustomerName>
                  <CustomerStatus isActive={customer.isActive}>
                    {customer.isActive ? 'Active' : 'Inactive'}
                  </CustomerStatus>
                </CustomerHeader>
                
                <CustomerInfo>
                  <InfoRow>
                    <InfoLabel>Email:</InfoLabel>
                    <InfoValue>{customer.email}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Phone:</InfoLabel>
                    <InfoValue>{customer.phone || 'N/A'}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Company:</InfoLabel>
                    <InfoValue>{customer.company || 'N/A'}</InfoValue>
                  </InfoRow>
                </CustomerInfo>

                <CustomerActions>
                  <ViewButton to={`/customers/${customer.id}`}>
                    View
                  </ViewButton>
                  <EditButton to={`/customers/${customer.id}/edit`}>
                    Edit
                  </EditButton>
                  <DeleteButton onClick={() => handleDeleteCustomer(customer.id)}>
                    Delete
                  </DeleteButton>
                </CustomerActions>
              </CustomerCard>
            ))}
          </CustomerGrid>

          <PaginationContainer>
            <PaginationButton
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              Previous
            </PaginationButton>
            <PageInfo>
              Page {currentPage + 1} of {totalPages} ({totalElements || 0} customers)
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

export default CustomerListPage; 