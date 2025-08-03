import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchUserById, createUser, updateUser, fetchUsers, clearUsers } from '../../store/slices/userSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
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

const FormContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
`;

const FormCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const FormGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const FormGroup = styled.div`
  margin-bottom: 20px;
`;

const FormLabel = styled.label`
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
`;

const FormInput = styled.input`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  }

  &.error {
    border-color: #dc3545;
  }
`;

const FormSelect = styled.select`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  background: white;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  }

  &.error {
    border-color: #dc3545;
  }
`;

// Removed unused styled components

const ErrorMessage = styled.div`
  color: #dc3545;
  font-size: 0.9rem;
  margin-top: 4px;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
`;

const Button = styled.button`
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
`;

const SaveButton = styled(Button)`
  background: #28a745;
  color: white;

  &:hover {
    background: #218838;
  }

  &:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
`;

const CancelButton = styled(Button)`
  background: #6c757d;
  color: white;

  &:hover {
    background: #5a6268;
  }
`;

const FullWidthGroup = styled(FormGroup)`
  grid-column: 1 / -1;
`;

interface FormData {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  mobileNumber: string;
  isActive: boolean;
  roles: string[];
}

interface FormErrors {
  username?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
  firstName?: string;
  lastName?: string;
  mobileNumber?: string;
  roles?: string;
}

const UserFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedUser, loading } = useSelector((state: RootState) => state.user);
  const isEditing = Boolean(id);
  
  // Get dropdown data
  const { roles, fetchRoles, loading: dropdownLoading, error: dropdownError } = useDropdownData();

  const [formData, setFormData] = useState<FormData>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    mobileNumber: '',
    isActive: true,
    roles: [],
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isEditing && id) {
      dispatch(fetchUserById(parseInt(id)));
    }
    // Fetch roles for dropdown
    fetchRoles();
  }, [dispatch, id, isEditing, fetchRoles]);

  useEffect(() => {
    if (isEditing && selectedUser) {
      setFormData({
        username: selectedUser.username || '',
        email: selectedUser.email || '',
        password: '',
        confirmPassword: '',
        firstName: selectedUser.first_name || selectedUser.firstName || '',
        lastName: selectedUser.last_name || selectedUser.lastName || '',
        mobileNumber: selectedUser.mobile_number || selectedUser.mobileNumber || '',
        isActive: selectedUser.is_active || selectedUser.isActive || true,
        roles: selectedUser.roles?.map(role => role.name) || [],
      });
    }
  }, [selectedUser, isEditing]);

  const handleInputChange = (field: keyof FormData, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));

    // Clear error when user starts typing
    if (errors[field as keyof FormErrors]) {
      setErrors(prev => ({
        ...prev,
        [field]: undefined,
      }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.username.trim()) {
      newErrors.username = 'Username is required';
    } else if (formData.username.length < 3) {
      newErrors.username = 'Username must be at least 3 characters';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    if (!isEditing && !formData.password.trim()) {
      newErrors.password = 'Password is required';
    } else if (!isEditing && formData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters';
    }

    if (!isEditing && formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }

    if (formData.mobileNumber && !/^[+]?[1-9][\d]{0,15}$/.test(formData.mobileNumber)) {
      newErrors.mobileNumber = 'Please enter a valid mobile number';
    }

    if (!formData.roles || formData.roles.length === 0 || !formData.roles[0]) {
      newErrors.roles = 'Please select a role';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      // Map single role name to Role object with proper structure
      const selectedRoleName = formData.roles[0];
      const selectedRole = roles.find(r => r.name === selectedRoleName);
      const roleObjects = selectedRole ? [{
        id: selectedRole.id,
        name: selectedRole.name,
        level: 1 // Default level since DropdownOption doesn't have level
      }] : [];

      const userData = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        mobileNumber: formData.mobileNumber,
        isActive: formData.isActive,
        roles: roleObjects,
      };

      if (isEditing && selectedUser) {
        await dispatch(updateUser({ userId: selectedUser.user_id || selectedUser.id || 0, userData })).unwrap();
        dispatch(addNotification({
          type: 'success',
          message: 'User updated successfully',
          title: 'Success',
          duration: 3000,
        }));
        // Refresh the users list after successful update
        // Clear the users list to force a refresh when navigating back
        dispatch(clearUsers());
      } else {
        await dispatch(createUser(userData)).unwrap();
        dispatch(addNotification({
          type: 'success',
          message: 'User created successfully',
          title: 'Success',
          duration: 3000,
        }));
      }

      navigate('/users');
    } catch (error: any) {
      dispatch(addNotification({
        type: 'error',
        message: error.message || 'Failed to save user',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading && isEditing) {
    return <LoadingSpinner />;
  }

  return (
    <PageContainer>
      <PageHeader>
        <div>
          <BackButton to="/users">← Back to Users</BackButton>
          <PageTitle>{isEditing ? 'Edit User' : 'Create New User'}</PageTitle>
        </div>
      </PageHeader>

      <FormContainer>
        <FormCard>
          <form onSubmit={handleSubmit}>
            <FormGrid>
              <FormGroup>
                <FormLabel>Username *</FormLabel>
                <FormInput
                  type="text"
                  value={formData.username}
                  onChange={(e) => handleInputChange('username', e.target.value)}
                  className={errors.username ? 'error' : ''}
                  disabled={isEditing}
                />
                {errors.username && <ErrorMessage>{errors.username}</ErrorMessage>}
              </FormGroup>

              <FormGroup>
                <FormLabel>Email *</FormLabel>
                <FormInput
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  className={errors.email ? 'error' : ''}
                />
                {errors.email && <ErrorMessage>{errors.email}</ErrorMessage>}
              </FormGroup>

              {!isEditing && (
                <>
                  <FormGroup>
                    <FormLabel>Password *</FormLabel>
                    <FormInput
                      type="password"
                      value={formData.password}
                      onChange={(e) => handleInputChange('password', e.target.value)}
                      className={errors.password ? 'error' : ''}
                    />
                    {errors.password && <ErrorMessage>{errors.password}</ErrorMessage>}
                  </FormGroup>

                  <FormGroup>
                    <FormLabel>Confirm Password *</FormLabel>
                    <FormInput
                      type="password"
                      value={formData.confirmPassword}
                      onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                      className={errors.confirmPassword ? 'error' : ''}
                    />
                    {errors.confirmPassword && <ErrorMessage>{errors.confirmPassword}</ErrorMessage>}
                  </FormGroup>
                </>
              )}

              <FormGroup>
                <FormLabel>First Name *</FormLabel>
                <FormInput
                  type="text"
                  value={formData.firstName}
                  onChange={(e) => handleInputChange('firstName', e.target.value)}
                  className={errors.firstName ? 'error' : ''}
                />
                {errors.firstName && <ErrorMessage>{errors.firstName}</ErrorMessage>}
              </FormGroup>

              <FormGroup>
                <FormLabel>Last Name *</FormLabel>
                <FormInput
                  type="text"
                  value={formData.lastName}
                  onChange={(e) => handleInputChange('lastName', e.target.value)}
                  className={errors.lastName ? 'error' : ''}
                />
                {errors.lastName && <ErrorMessage>{errors.lastName}</ErrorMessage>}
              </FormGroup>

              <FormGroup>
                <FormLabel>Mobile Number</FormLabel>
                <FormInput
                  type="tel"
                  value={formData.mobileNumber}
                  onChange={(e) => handleInputChange('mobileNumber', e.target.value)}
                  className={errors.mobileNumber ? 'error' : ''}
                />
                {errors.mobileNumber && <ErrorMessage>{errors.mobileNumber}</ErrorMessage>}
              </FormGroup>

                            <FormGroup>
                <FormLabel>Status</FormLabel>
                <FormSelect
                  value={formData.isActive ? 'active' : 'inactive'}
                  onChange={(e) => handleInputChange('isActive', e.target.value === 'active')}
                >
                  <option value="active">Active</option>
                  <option value="inactive">Inactive</option>
                </FormSelect>
              </FormGroup>

              <FormGroup>
                <FormLabel>Role *</FormLabel>
                <FormSelect
                  value={formData.roles[0] || ''}
                  onChange={(e) => handleInputChange('roles', [e.target.value])}
                  className={errors.roles ? 'error' : ''}
                >
                  <option value="">Select a role</option>
                  {roles.map((role) => (
                    <option key={role.id} value={role.name}>
                      {role.name}
                    </option>
                  ))}
                </FormSelect>
                {errors.roles && <ErrorMessage>{errors.roles}</ErrorMessage>}
                {dropdownLoading.roles && (
                  <div style={{ color: '#007bff', fontStyle: 'italic', marginTop: '4px' }}>
                    Loading roles...
                  </div>
                )}
                {roles.length === 0 && !dropdownLoading.roles && (
                  <div style={{ color: '#6c757d', fontStyle: 'italic', marginTop: '4px' }}>
                    No roles available. Please contact an administrator.
                  </div>
                )}
              </FormGroup>
            </FormGrid>

            <ButtonContainer>
              <CancelButton type="button" onClick={() => navigate('/users')}>
                Cancel
              </CancelButton>
              <SaveButton type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Saving...' : isEditing ? 'Update User' : 'Create User'}
              </SaveButton>
            </ButtonContainer>
          </form>
        </FormCard>
      </FormContainer>
    </PageContainer>
  );
};

export default UserFormPage; 