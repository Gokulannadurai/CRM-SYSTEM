import React, { useEffect, useState, useCallback } from 'react';
import { useSelector } from 'react-redux';
import { Link, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchUsers, searchUsers, deleteUser } from '../../store/slices/userSlice';
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
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
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

const UserGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
`;

const UserCard = styled.div`
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

const UserHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
`;

const UserName = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
`;

const UserActions = styled.div`
  display: flex;
  gap: 8px;
`;

const ActionButton = styled(Link)`
  padding: 6px 12px;
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
  padding: 6px 12px;
  background: #ffebee;
  color: #d32f2f;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background: #ffcdd2;
  }
`;

const UserInfo = styled.div`
  display: grid;
  gap: 8px;
  font-size: 0.95rem;
  color: #6c757d;
`;

const UserInfoRow = styled.div`
  display: flex;
  justify-content: space-between;
`;

const UserInfoLabel = styled.span`
  font-weight: 500;
  color: #495057;
`;

const UserInfoValue = styled.span`
  color: #6c757d;
`;

const StatusBadge = styled.span<{ isActive: boolean }>`
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  background: ${props => props.isActive ? '#d4edda' : '#f8d7da'};
  color: ${props => props.isActive ? '#155724' : '#721c24'};
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

const EmptyState = styled.div`
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
`;



const EmptyStateText = styled.h3`
  font-size: 1.5rem;
  margin-bottom: 8px;
  color: #2c3e50;
`;

const EmptyStateSubtext = styled.p`
  font-size: 1.1rem;
  margin: 0;
`;

const UserListPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const { users, loading, pagination } = useSelector((state: RootState) => state.user);
  const { roles, fetchRoles } = useDropdownData();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState<string>('');
  const [currentPage, setCurrentPage] = useState(0);
  const [deleteDialog, setDeleteDialog] = useState<{ isOpen: boolean; userId: number | null }>({
    isOpen: false,
    userId: null,
  });

  useEffect(() => {
    fetchRoles();
  }, [fetchRoles]);

  // Refresh list when component mounts or when navigating back
  useEffect(() => {
    // Only fetch if users array is empty or when dependencies change
    if (users.length === 0 || location.pathname === '/users') {
      dispatch(fetchUsers({ 
        page: currentPage, 
        size: 10, 
        role: selectedRole === "" ? undefined : selectedRole,
        searchTerm: searchTerm.trim() || undefined
      }));
    }
  }, [dispatch, currentPage, selectedRole, searchTerm, location.pathname, users.length]);

  // Refresh list when window gains focus (user returns to tab)
  useEffect(() => {
    const handleFocus = () => {
      dispatch(fetchUsers({ 
        page: currentPage, 
        size: 10, 
        role: selectedRole === "" ? undefined : selectedRole,
        searchTerm: searchTerm.trim() || undefined
      }));
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [dispatch, currentPage, selectedRole, searchTerm]);

  const handleSearch = () => {
    // Always use fetchUsers to support both search and role filtering
    dispatch(fetchUsers({ 
      page: 0, 
      size: 10, 
      role: selectedRole === "" ? undefined : selectedRole,
      searchTerm: searchTerm.trim() || undefined
    }));
    setCurrentPage(0);
  };

  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newRole = e.target.value;
    setSelectedRole(newRole);
    setCurrentPage(0);
    
    console.log('Role change - newRole:', newRole, 'type:', typeof newRole);
    console.log('Role change - searchTerm:', searchTerm);
    
    // Always use fetchUsers to support role filtering, even when there's a search term
    // The backend will handle the search and role filtering together
    const roleParam = newRole === "" ? undefined : newRole;
    console.log('Role change - using fetchUsers with role:', roleParam);
    console.log('Role change - "All Roles" selected, will fetch all users without role specification');
    dispatch(fetchUsers({ 
      page: 0, 
      size: 10, 
      role: roleParam,
      searchTerm: searchTerm.trim() || undefined
    }));
  };

  // Auto-search on typing with debouncing
  const debouncedSearch = useCallback(
    (value: string) => {
      const timeoutId = setTimeout(() => {
        // Always use fetchUsers to support both search and role filtering
        dispatch(fetchUsers({ 
          page: 0, 
          size: 10, 
          role: selectedRole === "" ? undefined : selectedRole,
          searchTerm: value.trim() || undefined
        }));
        setCurrentPage(0);
      }, 500);

      return () => clearTimeout(timeoutId);
    },
    [dispatch, selectedRole]
  );

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchTerm(value);
    debouncedSearch(value);
  };

  const handleDeleteUser = (userId: number) => {
    setDeleteDialog({ isOpen: true, userId });
  };

  const confirmDeleteUser = async () => {
    if (deleteDialog.userId) {
      try {
        await dispatch(deleteUser(deleteDialog.userId)).unwrap();
        dispatch(addNotification({
          type: 'success',
          message: 'User deleted successfully',
          title: 'Success',
          duration: 3000,
        }));
        
        // Refresh the user list after successful deletion
        dispatch(fetchUsers({ 
          page: 0, 
          size: 10, 
          role: selectedRole === "" ? undefined : selectedRole,
          searchTerm: searchTerm.trim() || undefined
        }));
        setCurrentPage(0);
      } catch (error) {
        dispatch(addNotification({
          type: 'error',
          message: 'Failed to delete user',
          title: 'Error',
          duration: 5000,
        }));
      }
    }
    setDeleteDialog({ isOpen: false, userId: null });
  };

  const cancelDeleteUser = () => {
    setDeleteDialog({ isOpen: false, userId: null });
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  if (loading && users.length === 0) {
    return <LoadingSpinner />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Users</PageTitle>
        <AddButton to="/users/new">+ Add User</AddButton>
      </PageHeader>

      <SearchBar>
        <SearchInput
          type="text"
          placeholder="Search users by name, email, or username..."
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <FilterSelect
          value={selectedRole}
          onChange={handleRoleChange}
        >
          <option value="">All Roles</option>
          {roles.map((role) => (
            <option key={role.id} value={role.name}>
              {role.name}
            </option>
          ))}
        </FilterSelect>
      </SearchBar>

      {users.length === 0 ? (
        <EmptyState>
          <EmptyStateText>No users found</EmptyStateText>
          <EmptyStateSubtext>
            {searchTerm ? 'Try adjusting your search terms.' : 'Get started by creating your first user.'}
          </EmptyStateSubtext>
        </EmptyState>
      ) : (
        <>
          <UserGrid>
            {users.map((user) => (
              <UserCard key={user.user_id || user.id}>
                <UserHeader>
                  <UserName>{user.first_name || user.firstName} {user.last_name || user.lastName}</UserName>
                  <UserActions>
                    <ViewButton to={`/users/${user.user_id || user.id}`}>View</ViewButton>
                    <EditButton to={`/users/${user.user_id || user.id}/edit`}>Edit</EditButton>
                    <DeleteButton onClick={() => handleDeleteUser(user.user_id || user.id || 0)}>
                      Delete
                    </DeleteButton>
                  </UserActions>
                </UserHeader>
                <UserInfo>
                  <UserInfoRow>
                    <UserInfoLabel>Username:</UserInfoLabel>
                    <UserInfoValue>{user.username}</UserInfoValue>
                  </UserInfoRow>
                  <UserInfoRow>
                    <UserInfoLabel>Email:</UserInfoLabel>
                    <UserInfoValue>{user.email}</UserInfoValue>
                  </UserInfoRow>
                  <UserInfoRow>
                    <UserInfoLabel>Mobile:</UserInfoLabel>
                    <UserInfoValue>{user.mobile_number || user.mobileNumber || 'N/A'}</UserInfoValue>
                  </UserInfoRow>
                  <UserInfoRow>
                    <UserInfoLabel>Status:</UserInfoLabel>
                    <UserInfoValue>
                      <StatusBadge isActive={user.is_active || user.isActive || false}>
                        {user.is_active || user.isActive ? 'Active' : 'Inactive'}
                      </StatusBadge>
                    </UserInfoValue>
                  </UserInfoRow>
                  <UserInfoRow>
                    <UserInfoLabel>Roles:</UserInfoLabel>
                    <UserInfoValue>
                      {user.roles?.map(role => role.name).join(', ') || 'No roles'}
                    </UserInfoValue>
                  </UserInfoRow>
                  <UserInfoRow>
                    <UserInfoLabel>Created:</UserInfoLabel>
                    <UserInfoValue>
                      {new Date(user.createdAt).toLocaleDateString()}
                    </UserInfoValue>
                  </UserInfoRow>
                </UserInfo>
              </UserCard>
            ))}
          </UserGrid>

          <PaginationContainer>
            <PaginationButton
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              Previous
            </PaginationButton>
            <PageInfo>
              Page {currentPage + 1} of {pagination.totalPages} ({pagination.totalElements} users)
            </PageInfo>
            <PaginationButton
              disabled={currentPage >= pagination.totalPages - 1}
              onClick={() => handlePageChange(currentPage + 1)}
            >
              Next
            </PaginationButton>
          </PaginationContainer>
        </>
      )}

      <ConfirmationDialog
        isOpen={deleteDialog.isOpen}
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

export default UserListPage; 