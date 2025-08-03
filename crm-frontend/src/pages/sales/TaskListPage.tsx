import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchTasks, deleteTask, updateTaskStatus } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
import { getTaskStatus, getTaskStatusDisplay, getNextTaskStatus, getTaskStatusButtonText } from '../../utils/taskUtils';
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

const TaskGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
`;

const TaskCard = styled.div`
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

const TaskHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
`;

const TaskTitle = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  flex: 1;
`;

const TaskStatus = styled.span<{ status: string }>`
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
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

const TaskInfo = styled.div`
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

const TaskDescription = styled.p`
  color: #6c757d;
  font-size: 0.9rem;
  line-height: 1.4;
  margin: 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
`;

const TaskActions = styled.div`
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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



const StatusButton = styled.button<{ status: string }>`
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background: ${props => {
    switch (props.status) {
      case 'PENDING': return '#fff3cd';
      case 'IN_PROGRESS': return '#d1ecf1';
      case 'COMPLETED': return '#d4edda';
      default: return '#e2e3e5';
    }
  }};
  color: ${props => {
    switch (props.status) {
      case 'PENDING': return '#856404';
      case 'IN_PROGRESS': return '#0c5460';
      case 'COMPLETED': return '#155724';
      default: return '#6c757d';
    }
  }};

  &:hover {
    opacity: 0.8;
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

const TaskListPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const { tasks, loading, error, totalElements } = useSelector((state: RootState) => state.sales);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [currentPage, setCurrentPage] = useState(0); // Changed to 0-based for API
  const itemsPerPage = 50; // Increased page size
  const [deleteDialog, setDeleteDialog] = useState<{ isOpen: boolean; taskId: number | null }>({
    isOpen: false,
    taskId: null
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
    // Only fetch if tasks array is empty or when navigating back
    if (tasks.length === 0 || location.pathname === '/tasks') {
      // Fetch tasks with proper pagination
      dispatch(fetchTasks({ page: currentPage, size: itemsPerPage }));
    }
  }, [dispatch, tasks.length, location.pathname, currentPage, itemsPerPage]);

  // Refresh list when window gains focus
  useEffect(() => {
    const handleFocus = () => {
      dispatch(fetchTasks({ page: currentPage, size: itemsPerPage }));
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [dispatch, currentPage, itemsPerPage]);
  
  // Fetch users data for mapping
  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const filteredTasks = tasks.filter(task => {
    // Get assigned user name for search
    const assignedUserName = getUserNameById(task.assignedTo);
    
    const matchesSearch = (task.title?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
                         (task.description?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
                         (assignedUserName.toLowerCase()).includes(searchTerm.toLowerCase());
    
    const taskStatus = getTaskStatus(task);
    const matchesStatus = statusFilter === 'ALL' || taskStatus === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  // Use tasks directly from Redux state (already paginated from server)
  const paginatedTasks = filteredTasks;
  
  // Get total pages from Redux state
  const totalPages = Math.ceil((totalElements || 0) / itemsPerPage);

  const handleDeleteTask = async (taskId: number) => {
    setDeleteDialog({ isOpen: true, taskId });
  };

  const confirmDeleteTask = async () => {
    if (!deleteDialog.taskId) return;
    
    try {
      await dispatch(deleteTask(deleteDialog.taskId));
      dispatch(addNotification({
        type: 'success',
        message: 'Task deleted successfully',
        title: 'Success',
        duration: 3000,
      }));
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to delete task',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setDeleteDialog({ isOpen: false, taskId: null });
    }
  };

  const cancelDeleteTask = () => {
    setDeleteDialog({ isOpen: false, taskId: null });
  };

  const handleStatusChange = async (taskId: number, newStatus: string) => {
    try {
      await dispatch(updateTaskStatus({ taskId, status: newStatus }));
      dispatch(addNotification({
        type: 'success',
        message: 'Task status updated successfully',
        title: 'Success',
        duration: 3000,
      }));
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: 'Failed to update task status',
        title: 'Error',
        duration: 5000,
      }));
    }
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    // Fetch tasks for the new page
    dispatch(fetchTasks({ page: newPage, size: itemsPerPage }));
  };

  const getNextStatus = (task: any): string => {
    const currentStatus = getTaskStatus(task);
    return getNextTaskStatus(currentStatus);
  };

  const getStatusButtonText = (task: any): string => {
    const currentStatus = getTaskStatus(task);
    return getTaskStatusButtonText(currentStatus);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <h2>Error loading tasks</h2>
          <p>{error}</p>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
              <PageHeader>
          <PageTitle>Tasks</PageTitle>
          <AddButton to="/tasks/new">+ Add Task</AddButton>
        </PageHeader>

      <SearchBar>
        <SearchInput
          type="text"
          placeholder="Search tasks by title, description, or assignee..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <FilterSelect
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="ALL">All Status</option>
          <option value="PENDING">Start</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </FilterSelect>
      </SearchBar>

      {paginatedTasks.length === 0 ? (
        <EmptyState>
          <EmptyStateIcon>✅</EmptyStateIcon>
          <EmptyStateText>No tasks found</EmptyStateText>
          <EmptyStateSubtext>
            {searchTerm || statusFilter !== 'ALL' 
              ? 'Try adjusting your search or filter criteria'
              : 'Get started by adding your first task'
            }
          </EmptyStateSubtext>
          {!searchTerm && statusFilter === 'ALL' && (
            <AddButton to="/tasks/new">+ Add Task</AddButton>
          )}
        </EmptyState>
      ) : (
        <>
          <TaskGrid>
            {paginatedTasks.map((task) => (
              <TaskCard key={task.id}>
                <TaskHeader>
                  <TaskTitle>{task.title || 'Untitled Task'}</TaskTitle>
                  <TaskStatus status={getTaskStatus(task)}>{getTaskStatusDisplay(getTaskStatus(task))}</TaskStatus>
                </TaskHeader>
                
                {task.description && (
                  <TaskDescription>{task.description}</TaskDescription>
                )}
                
                <TaskInfo>
                  <InfoRow>
                    <InfoLabel>Assigned:</InfoLabel>
                    <InfoValue>{getUserNameById(task.assignedTo)}</InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Due Date:</InfoLabel>
                    <InfoValue>
                      {task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No due date'}
                    </InfoValue>
                  </InfoRow>
                  <InfoRow>
                    <InfoLabel>Priority:</InfoLabel>
                    <InfoValue>{task.priority || 'Medium'}</InfoValue>
                  </InfoRow>
                </TaskInfo>

                <TaskActions>
                  <ViewButton to={`/tasks/${task.id}`}>
                    View
                  </ViewButton>
                  <DeleteButton onClick={() => handleDeleteTask(task.id)}>
                    Delete
                  </DeleteButton>
                </TaskActions>
              </TaskCard>
            ))}
          </TaskGrid>

          <PaginationContainer>
            <PaginationButton
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              Previous
            </PaginationButton>
            <PageInfo>
              Page {currentPage + 1} of {totalPages} ({totalElements || 0} tasks)
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

export default TaskListPage; 