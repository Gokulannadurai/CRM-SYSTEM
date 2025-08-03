import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchTaskById, createTask, updateTask, clearTasks } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import FileUploadField from '../../components/common/FileUploadField';
import DropdownField from '../../components/common/DropdownField';
import { useDropdownData } from '../../hooks/useDropdownData';
import { Attachment } from '../../types';


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
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
`;

const Select = styled.select<{ error?: boolean }>`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid ${props => props.error ? '#e74c3c' : '#ddd'};
  border-radius: 6px;
  font-size: 1rem;
  background: white;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
`;

const TextArea = styled.textarea<{ error?: boolean }>`
  width: 100%;
  padding: 12px 16px;
  border: 1px solid ${props => props.error ? '#e74c3c' : '#ddd'};
  border-radius: 6px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
`;

const ErrorMessage = styled.div`
  color: #e74c3c;
  font-size: 0.8rem;
  margin-top: 4px;
`;

const FormActions = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
`;

const Button = styled.button<{ variant?: 'primary' | 'secondary' }>`
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background: ${props => props.variant === 'primary' ? '#667eea' : '#6c757d'};
  color: white;

  &:hover {
    background: ${props => props.variant === 'primary' ? '#5a6fd8' : '#5a6268'};
  }

  &:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
`;

const CancelButton = styled(Link)`
  padding: 12px 24px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  color: #6c757d;
  background: white;
  transition: all 0.2s;

  &:hover {
    background: #e9ecef;
    text-decoration: none;
    color: #6c757d;
  }
`;

const CheckboxContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
`;

const Checkbox = styled.input`
  width: 18px;
  height: 18px;
  cursor: pointer;
`;

const CheckboxLabel = styled.label`
  font-weight: 500;
  color: #2c3e50;
  cursor: pointer;
`;

// Existing Attachments Styled Components
const ExistingAttachmentsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
`;

const ExistingAttachmentItem = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background: #e9ecef;
    border-color: #dee2e6;
  }
`;

const AttachmentInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
`;

const FileIcon = styled.span`
  font-size: 1.2rem;
`;

const FileDetails = styled.div`
  display: flex;
  flex-direction: column;
`;

const FileName = styled.span`
  font-weight: 500;
  color: #2c3e50;
  font-size: 0.9rem;
`;

const FileType = styled.span`
  font-size: 0.8rem;
  color: #6c757d;
  text-transform: uppercase;
`;

const RemoveButton = styled.button`
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  transition: background 0.2s;

  &:hover {
    background: #c0392b;
  }
`;

interface FormData {
  id?: number;
  title: string;
  description: string;
  leadId: string;
  customerId: string;
  assignedTo: string;
  taskTypeId: string;
  dueDate: string;
  isStarted: boolean;
}

interface FormErrors {
  title?: string;
  description?: string;
  leadId?: string;
  customerId?: string;
  assignedTo?: string;
  taskTypeId?: string;
  dueDate?: string;
}

const TaskFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedTask, leads, loading, error } = useSelector((state: RootState) => state.sales);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [attachments, setAttachments] = useState<File[]>([]);
  const [existingAttachments, setExistingAttachments] = useState<Attachment[]>([]);
  const [removedAttachmentIds, setRemovedAttachmentIds] = useState<number[]>([]);

  // Use dropdown data hook
  const {
    users,
    customers,
    loading: dropdownLoading,
    error: dropdownError,
    fetchUsers,
    fetchCustomers,
  } = useDropdownData();

  const [formData, setFormData] = useState<FormData>({
    title: '',
    description: '',
    leadId: '',
    customerId: '',
    assignedTo: '',
    taskTypeId: '',
    dueDate: '',
    isStarted: false,
  });

  const [errors, setErrors] = useState<FormErrors>({});

  const isEditing = Boolean(id);

  useEffect(() => {
    if (isEditing && id) {
      dispatch(fetchTaskById(parseInt(id)));
    }
  }, [dispatch, id, isEditing]);

  // Fetch dropdown data on component mount
  useEffect(() => {
    fetchUsers();
    fetchCustomers();
  }, [fetchUsers, fetchCustomers]);

  useEffect(() => {
    if (selectedTask && isEditing) {
      console.log('Selected task for editing:', selectedTask);
      console.log('Selected task assignedTo:', selectedTask.assignedTo);
      console.log('Selected task assignedTo type:', typeof selectedTask.assignedTo);
      console.log('Users dropdown data available:', users);
      console.log('Users dropdown loading:', dropdownLoading.users);
      console.log('Users dropdown error:', dropdownError.users);
      
      const newFormData = {
        id: selectedTask.id,
        title: selectedTask.title || '',
        description: selectedTask.description || '',
        leadId: selectedTask.leadId ? selectedTask.leadId.toString() : '',
        customerId: selectedTask.customerId ? selectedTask.customerId.toString() : '',
        assignedTo: selectedTask.assignedTo ? selectedTask.assignedTo.toString() : '',
        taskTypeId: selectedTask.taskTypeId ? selectedTask.taskTypeId.toString() : '',
        dueDate: selectedTask.dueDate ? new Date(selectedTask.dueDate).toISOString().split('T')[0] : '',
        isStarted: selectedTask.isStarted || false,
      };
      console.log('Setting task form data for editing:', newFormData);
      console.log('Task form data assignedTo:', newFormData.assignedTo);
      
      // Check if the assigned user exists in the users dropdown
      if (newFormData.assignedTo && users.length > 0) {
        const assignedUser = users.find(u => u.id.toString() === newFormData.assignedTo);
        console.log('Found assigned user in dropdown:', assignedUser);
      } else {
        console.log('No assigned user found or users dropdown empty');
      }
      
      setFormData(newFormData);
      
      // Set existing attachments
      if (selectedTask.attachments && selectedTask.attachments.length > 0) {
        setExistingAttachments(selectedTask.attachments);
        console.log('Setting existing task attachments:', selectedTask.attachments);
      }
    }
  }, [selectedTask, isEditing, users, dropdownLoading.users, dropdownError.users]);

  // Debug: Log form data changes
  useEffect(() => {
    console.log('Task form data updated:', formData);
    console.log('Task form assignedTo field:', formData.assignedTo);
    console.log('Task form assignedTo field type:', typeof formData.assignedTo);
  }, [formData]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Description is required';
    }

    if (!formData.leadId) {
      newErrors.leadId = 'Lead is required';
    }

    if (!formData.customerId) {
      newErrors.customerId = 'Customer is required';
    }

    if (!formData.assignedTo) {
      newErrors.assignedTo = 'Assigned user is required';
    }

    if (!formData.taskTypeId) {
      newErrors.taskTypeId = 'Task type is required';
    }

    if (formData.dueDate && new Date(formData.dueDate) < new Date()) {
      newErrors.dueDate = 'Due date cannot be in the past';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    
    if (type === 'checkbox') {
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

  const handleRemoveExistingAttachment = (attachmentId: number) => {
    setRemovedAttachmentIds(prev => [...prev, attachmentId]);
    setExistingAttachments(prev => prev.filter(att => att.id !== attachmentId));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const formDataToSend = new FormData();
      
      // Create taskRequest object (matching the curl command format)
      const taskRequest = {
        ...(isEditing && selectedTask && { id: selectedTask.id }), // Include ID for updates
        title: formData.title,
        description: formData.description || 'No description provided',
        leadId: formData.leadId ? parseInt(formData.leadId) : null,
        customerId: parseInt(formData.customerId),
        assignedTo: formData.assignedTo ? parseInt(formData.assignedTo) : null,
        taskTypeId: parseInt(formData.taskTypeId),
        dueDate: formData.dueDate,
        isStarted: formData.isStarted
      };

      // Add taskRequest as JSON string
      formDataToSend.append('taskRequest', JSON.stringify(taskRequest));

      // Add existing attachments that weren't removed
      const attachmentsToKeep = existingAttachments.filter(att => !removedAttachmentIds.includes(att.id!));
      attachmentsToKeep.forEach((attachment, index) => {
        // Convert existing attachment to File object for multipart upload
        if (attachment.bytes) {
          const byteArray = typeof attachment.bytes === 'string' 
            ? Uint8Array.from(atob(attachment.bytes), c => c.charCodeAt(0))
            : new Uint8Array(attachment.bytes);
          
          const file = new File([byteArray], attachment.fileName, {
            type: attachment.fileType
          });
          formDataToSend.append('attachments', file);
        }
      });

      // Add new attachments
      attachments.forEach((file, index) => {
        formDataToSend.append('attachments', file);
      });

      if (isEditing && selectedTask) {
        await dispatch(updateTask({ id: selectedTask.id, formData: formDataToSend }));
        dispatch(addNotification({
          type: 'success',
          message: 'Task updated successfully',
          title: 'Success',
          duration: 3000,
        }));
      } else {
        await dispatch(createTask(formDataToSend));
        dispatch(addNotification({
          type: 'success',
          message: 'Task created successfully',
          title: 'Success',
          duration: 3000,
        }));
        // Clear the tasks list to force a refresh when navigating back
        dispatch(clearTasks());
      }
      navigate('/tasks');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: isEditing ? 'Failed to update task' : 'Failed to create task',
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
          <h2>Error loading task</h2>
          <p>{error}</p>
          <BackButton to="/tasks">← Back to Tasks</BackButton>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <BackButton to="/tasks">← Back to Tasks</BackButton>
        <PageTitle>{isEditing ? 'Edit Task' : 'Add New Task'}</PageTitle>
      </PageHeader>

      <FormContainer>
        <FormCard>
          <form onSubmit={handleSubmit}>
            <FormGrid>
              <FormSection>
                <SectionTitle>📋 Task Information</SectionTitle>
                
                <FormGroup>
                  <Label>
                    Title<Required>*</Required>
                  </Label>
                  <Input
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    placeholder="Enter task title"
                    error={!!errors.title}
                  />
                  {errors.title && <ErrorMessage>{errors.title}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>
                    Description<Required>*</Required>
                  </Label>
                  <TextArea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Enter task description"
                    error={!!errors.description}
                  />
                  {errors.description && <ErrorMessage>{errors.description}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>
                    Lead<Required>*</Required>
                  </Label>
                  <Select
                    name="leadId"
                    value={formData.leadId}
                    onChange={handleInputChange}
                    error={!!errors.leadId}
                  >
                    <option value="">Select lead</option>
                    {leads.map(lead => (
                      <option key={lead.id} value={lead.id}>
                        {lead.title || `Lead ${lead.id}`}
                      </option>
                    ))}
                  </Select>
                  {errors.leadId && <ErrorMessage>{errors.leadId}</ErrorMessage>}
                </FormGroup>

                <DropdownField
                  label="Customer"
                  value={formData.customerId ? parseInt(formData.customerId) : ''}
                  onChange={(value) => {
                    const selectedCustomer = customers.find(c => c.id === value);
                    console.log('Customer selected:', selectedCustomer);
                    console.log('Customer dropdown value:', formData.customerId);
                    setFormData(prev => ({ 
                      ...prev, 
                      customerId: value ? value.toString() : ''
                    }));
                    if (errors.customerId) {
                      setErrors(prev => ({ ...prev, customerId: undefined }));
                    }
                  }}
                  options={customers}
                  placeholder="Select customer"
                  required
                  loading={dropdownLoading.customers}
                  error={dropdownError.customers || errors.customerId}
                />
              </FormSection>

              <FormSection>
                <SectionTitle>📊 Task Details</SectionTitle>
                
                <DropdownField
                  label="Assigned To"
                  value={formData.assignedTo ? parseInt(formData.assignedTo) : ''}
                  onChange={(value) => {
                    const selectedUser = users.find(u => u.id === value);
                    console.log('User selected:', selectedUser);
                    console.log('Assigned To dropdown value:', formData.assignedTo);
                    console.log('Dropdown onChange received value:', value);
                    setFormData(prev => ({ 
                      ...prev, 
                      assignedTo: value ? value.toString() : ''
                    }));
                    if (errors.assignedTo) {
                      setErrors(prev => ({ ...prev, assignedTo: undefined }));
                    }
                  }}
                  options={users}
                  placeholder="Select user"
                  required
                  loading={dropdownLoading.users}
                  error={dropdownError.users || errors.assignedTo}
                />

                <FormGroup>
                  <Label>
                    Task Type<Required>*</Required>
                  </Label>
                  <Select
                    name="taskTypeId"
                    value={formData.taskTypeId}
                    onChange={handleInputChange}
                    error={!!errors.taskTypeId}
                  >
                    <option value="">Select task type</option>
                    <option value="1">Call</option>
                    <option value="2">Meeting</option>
                    <option value="3">Email</option>
                    <option value="4">Follow-up</option>
                    <option value="5">Proposal</option>
                    <option value="6">Demo</option>
                  </Select>
                  {errors.taskTypeId && <ErrorMessage>{errors.taskTypeId}</ErrorMessage>}
                </FormGroup>

                                 <FormGroup>
                   <Label>Due Date</Label>
                   <Input
                     type="date"
                     name="dueDate"
                     value={formData.dueDate}
                     onChange={handleInputChange}
                     error={!!errors.dueDate}
                   />
                   {errors.dueDate && <ErrorMessage>{errors.dueDate}</ErrorMessage>}
                 </FormGroup>

                <CheckboxContainer>
                  <Checkbox
                    type="checkbox"
                    name="isStarted"
                    checked={formData.isStarted}
                    onChange={handleInputChange}
                  />
                  <CheckboxLabel>Task has been started</CheckboxLabel>
                </CheckboxContainer>
              </FormSection>
            </FormGrid>

            <FormSection>
              <SectionTitle>📎 Attachments</SectionTitle>
              
              {/* Existing Attachments */}
              {isEditing && existingAttachments.length > 0 && (
                <FormGroup>
                  <Label>Existing Attachments</Label>
                  <ExistingAttachmentsContainer>
                    {existingAttachments.map((attachment) => (
                      <ExistingAttachmentItem key={attachment.id}>
                        <AttachmentInfo>
                          <FileIcon>📄</FileIcon>
                          <FileDetails>
                            <FileName>{attachment.fileName}</FileName>
                            <FileType>{attachment.fileType}</FileType>
                          </FileDetails>
                        </AttachmentInfo>
                        <RemoveButton
                          type="button"
                          onClick={() => handleRemoveExistingAttachment(attachment.id!)}
                          title="Remove attachment"
                        >
                          ×
                        </RemoveButton>
                      </ExistingAttachmentItem>
                    ))}
                  </ExistingAttachmentsContainer>
                </FormGroup>
              )}
              
              {/* New File Upload */}
              <FileUploadField
                label="Add New Documents"
                value={attachments}
                onChange={setAttachments}
                maxFiles={2}
                maxFileSize={10}
                acceptedTypes={['.pdf', '.doc', '.docx', '.txt', '.jpg', '.jpeg', '.png']}
              />
            </FormSection>

            <FormActions>
              <CancelButton to="/tasks">
                Cancel
              </CancelButton>
              <Button
                type="submit"
                variant="primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Saving...' : (isEditing ? 'Update Task' : 'Create Task')}
              </Button>
            </FormActions>
          </form>
        </FormCard>
      </FormContainer>
    </PageContainer>
  );
};

export default TaskFormPage; 