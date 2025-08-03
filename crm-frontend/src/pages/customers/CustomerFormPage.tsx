import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchCustomerById, createCustomer, updateCustomer } from '../../store/slices/customerSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';

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

const FormSection = styled.div`
  margin-bottom: 24px;
`;

const SectionTitle = styled.h3`
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
`;

const FormGroup = styled.div`
  margin-bottom: 20px;
`;

const Label = styled.label`
  display: block;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
`;

const Required = styled.span`
  color: #e74c3c;
  margin-left: 4px;
`;

const Input = styled.input<{ error?: boolean }>`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid ${props => props.error ? '#e74c3c' : '#ddd'};
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }

  &::placeholder {
    color: #adb5bd;
  }
`;



const TextArea = styled.textarea<{ error?: boolean }>`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid ${props => props.error ? '#e74c3c' : '#ddd'};
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }

  &::placeholder {
    color: #adb5bd;
  }
`;

const ErrorMessage = styled.div`
  color: #e74c3c;
  font-size: 0.9rem;
  margin-top: 4px;
`;

const CheckboxContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
`;

const Checkbox = styled.input`
  width: 18px;
  height: 18px;
  accent-color: #667eea;
  cursor: pointer;
`;

const CheckboxLabel = styled.label`
  font-size: 1rem;
  color: #2c3e50;
  cursor: pointer;
  user-select: none;
`;

const FormActions = styled.div`
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e9ecef;
`;

const Button = styled.button<{ variant?: 'primary' | 'secondary' }>`
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  ${props => props.variant === 'primary' ? `
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }
  ` : `
    background: #f8f9fa;
    color: #6c757d;
    border: 1px solid #dee2e6;

    &:hover {
      background: #e9ecef;
    }
  `}

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
`;

const CancelButton = styled(Link)`
  padding: 12px 24px;
  border-radius: 8px;
  border: 1px solid #dee2e6;
  background: #f8f9fa;
  color: #6c757d;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: #e9ecef;
    text-decoration: none;
    color: #6c757d;
  }
`;

interface FormData {
  name: string;
  email: string;
  phone: string;
  company: string;
  isActive: boolean;
  notes: string;
}

interface FormErrors {
  name?: string;
  email?: string;
  phone?: string;
}

const CustomerFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedCustomer, loading, error } = useSelector((state: RootState) => state.customer);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState<FormData>({
    name: '',
    email: '',
    phone: '',
    company: '',
    isActive: true,
    notes: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});

  const isEditing = Boolean(id);

  useEffect(() => {
    if (isEditing && id) {
      dispatch(fetchCustomerById(parseInt(id)));
    }
  }, [dispatch, id, isEditing]);

  useEffect(() => {
    if (selectedCustomer && isEditing) {
      setFormData({
        name: selectedCustomer.name,
        email: selectedCustomer.email || '',
        phone: selectedCustomer.phone || '',
        company: selectedCustomer.company || '',
        isActive: selectedCustomer.isActive,
        notes: '',
      });
    }
  }, [selectedCustomer, isEditing]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = 'The name is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    if (formData.phone && !/^[+]?[1-9][\d]{0,15}$/.test(formData.phone.replace(/\s/g, ''))) {
      newErrors.phone = 'Please enter a valid phone number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    
    // Handle checkbox for isActive
    if (name === 'isActive') {
      const checked = (e.target as HTMLInputElement).checked;
      setFormData(prev => ({
        ...prev,
        [name]: checked,
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value,
      }));
    }

    // Clear error when user starts typing
    if (errors[name as keyof FormErrors]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      if (isEditing && selectedCustomer) {
        const customerData = {
          ...formData,
          name: `${formData.name}`,
        };
        await dispatch(updateCustomer({ id: selectedCustomer.id, customerData }));
        dispatch(addNotification({
          type: 'success',
          message: 'Customer updated successfully',
          title: 'Success',
          duration: 3000,
        }));
      } else {
        const customerData = {
          ...formData,
          name: `${formData.name}`,
        };
        await dispatch(createCustomer(customerData));
        dispatch(addNotification({
          type: 'success',
          message: 'Customer created successfully',
          title: 'Success',
          duration: 3000,
        }));
      }
      navigate('/customers');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: isEditing ? 'Failed to update customer' : 'Failed to create customer',
        title: 'Error',
        duration: 5000,
      }));
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error && isEditing) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <h2>Error loading customer</h2>
          <p>{error}</p>
          <BackButton to="/customers">← Back to Customers</BackButton>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <BackButton to="/customers">← Back to Customers</BackButton>
        <PageTitle>{isEditing ? 'Edit Customer' : 'Add New Customer'}</PageTitle>
      </PageHeader>

      <FormContainer>
        <FormCard>
          <form onSubmit={handleSubmit}>
            <FormGrid>
              <FormSection>
                <SectionTitle>👤 Personal Information</SectionTitle>

                <FormGroup>
                  <Label> Name<Required>*</Required>
                  </Label>
                  <Input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="Enter the full name"
                    error={!!errors.name}
                  />
                  {errors.name && <ErrorMessage>{errors.name}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>
                    Email<Required>*</Required>
                  </Label>
                  <Input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    placeholder="Enter email address"
                    error={!!errors.email}
                  />
                  {errors.email && <ErrorMessage>{errors.email}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>Phone</Label>
                  <Input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    placeholder="Enter phone number"
                    error={!!errors.phone}
                  />
                  {errors.phone && <ErrorMessage>{errors.phone}</ErrorMessage>}
                </FormGroup>
              </FormSection>

              <FormSection>
                <SectionTitle>🏢 Business Information</SectionTitle>
                
                <FormGroup>
                  <Label>Company</Label>
                  <Input
                    type="text"
                    name="company"
                    value={formData.company}
                    onChange={handleInputChange}
                    placeholder="Enter company name"
                  />
                </FormGroup>

                <FormGroup>
                  <Label>Status</Label>
                  <CheckboxContainer>
                    <Checkbox
                      type="checkbox"
                      name="isActive"
                      checked={formData.isActive}
                      onChange={handleInputChange}
                    />
                    <CheckboxLabel>
                      Customer is active
                    </CheckboxLabel>
                  </CheckboxContainer>
                </FormGroup>

                <FormGroup>
                  <Label>Notes</Label>
                  <TextArea
                    name="notes"
                    value={formData.notes}
                    onChange={handleInputChange}
                    placeholder="Add any additional notes about this customer..."
                  />
                </FormGroup>
              </FormSection>
            </FormGrid>

            <FormActions>
              <CancelButton to="/customers">
                Cancel
              </CancelButton>
              <Button
                type="submit"
                variant="primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Saving...' : (isEditing ? 'Update Customer' : 'Create Customer')}
              </Button>
            </FormActions>
          </form>
        </FormCard>
      </FormContainer>
    </PageContainer>
  );
};

export default CustomerFormPage; 