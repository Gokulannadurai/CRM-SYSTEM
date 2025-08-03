import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState, useAppDispatch } from '../../store/store';
import { fetchLeadById, createLead, updateLead } from '../../store/slices/salesSlice';
import { addNotification } from '../../store/slices/uiSlice';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import FileUploadField from '../../components/common/FileUploadField';
import DropdownField from '../../components/common/DropdownField';
import { useDropdownData } from '../../hooks/useDropdownData';
import { userApi } from '../../services/userApi';
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
  customerId: string;
  customerName: string;
  assignedTo: string;
  userName: string;
  currentStageId: string;
  status: string;
  expectedCloseDate: string;
  value: string;
  source: string;
  additionalNotes: string;
}

interface FormErrors {
  title?: string;
  customerId?: string;
  assignedTo?: string;
  currentStageId?: string;
  expectedCloseDate?: string;
  value?: string;
  additionalNotes?: string;
}

const LeadFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { selectedLead, loading, error } = useSelector((state: RootState) => state.sales);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [attachments, setAttachments] = useState<File[]>([]);
  const [existingAttachments, setExistingAttachments] = useState<Attachment[]>([]);
  const [removedAttachmentIds, setRemovedAttachmentIds] = useState<number[]>([]);
  
  // Get dropdown data
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
    customerId: '',
    customerName: '',
    assignedTo: '',
    userName: '',

    currentStageId: '',
    status: 'NEW',
    expectedCloseDate: '',
    value: '',
    source: 'WEBSITE',
    additionalNotes: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});

  const isEditing = Boolean(id);

  useEffect(() => {
    if (isEditing && id) {
      dispatch(fetchLeadById(parseInt(id)));
    }
  }, [dispatch, id, isEditing]);

  // Fetch dropdown data on component mount
  useEffect(() => {
    console.log('LeadFormPage: Fetching dropdown data...');
    
    // Test the dropdown endpoint directly
    const testDropdown = async () => {
      try {
        const result = await userApi.testDropdownEndpoint();
        console.log('Direct dropdown test result:', result);
      } catch (error) {
        console.error('Direct dropdown test failed:', error);
      }
    };
    
    testDropdown();
    fetchUsers();
    fetchCustomers();
  }, [fetchUsers, fetchCustomers]);

  // Debug: Log dropdown data changes
  useEffect(() => {
    console.log('LeadFormPage: Users dropdown data:', users);
    console.log('LeadFormPage: Users loading:', dropdownLoading.users);
    console.log('LeadFormPage: Users error:', dropdownError.users);
  }, [users, dropdownLoading.users, dropdownError.users]);

  useEffect(() => {
    if (selectedLead && isEditing) {
      console.log('Selected lead for editing:', selectedLead);
      console.log('Selected lead assignedTo:', selectedLead.assignedTo);
      console.log('Selected lead assignedTo type:', typeof selectedLead.assignedTo);
      
      const newFormData = {
        id: selectedLead.id,
        title: selectedLead.title || '',
        customerId: selectedLead.customerId ? selectedLead.customerId.toString() : '',
        customerName: selectedLead.customerName || '',
        assignedTo: selectedLead.assignedTo ? selectedLead.assignedTo.toString() : '',
        userName: selectedLead.userName || '',

        currentStageId: selectedLead.currentStageId ? selectedLead.currentStageId.toString() : '',
        status: selectedLead.status || 'NEW',
        expectedCloseDate: selectedLead.expectedCloseDate || '',
        value: selectedLead.value ? selectedLead.value.toString() : '',
        source: selectedLead.source || 'WEBSITE',
        additionalNotes: selectedLead.additionalNotes || '',
      };
      console.log('Setting form data for editing:', newFormData);
      console.log('Form data assignedTo:', newFormData.assignedTo);
      setFormData(newFormData);
      
      // Set existing attachments
      if (selectedLead.attachments && selectedLead.attachments.length > 0) {
        setExistingAttachments(selectedLead.attachments);
        console.log('Setting existing attachments:', selectedLead.attachments);
      }
    }
  }, [selectedLead, isEditing]);

  // Debug: Log form data changes
  useEffect(() => {
    console.log('Form data updated:', formData);
  }, [formData]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.customerId) {
      newErrors.customerId = 'Customer is required';
    }

    if (!formData.assignedTo) {
      newErrors.assignedTo = 'Assigned user is required';
    }



    if (!formData.currentStageId) {
      newErrors.currentStageId = 'Stage is required';
    }

    if (formData.expectedCloseDate && new Date(formData.expectedCloseDate) < new Date()) {
      newErrors.expectedCloseDate = 'Expected close date cannot be in the past';
    }

    if (formData.value && isNaN(Number(formData.value))) {
      newErrors.value = 'Please enter a valid amount';
    }

    if (!formData.additionalNotes || !formData.additionalNotes.trim()) {
      newErrors.additionalNotes = 'Additional notes are required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    
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
      
      // Create leadRequest object - send only fields that exist in Lead entity
      const leadRequest = {
        ...(isEditing && selectedLead && { id: selectedLead.id }), // Include ID for updates
        title: formData.title,
        customerId: parseInt(formData.customerId),
        customerName: formData.customerName,
        assignedTo: formData.assignedTo ? parseInt(formData.assignedTo) : null,
        userName: formData.userName,
        status: formData.status,
        currentStageId: formData.currentStageId ? parseInt(formData.currentStageId) : null,
        expectedCloseDate: formData.expectedCloseDate ? new Date(formData.expectedCloseDate) : null,
        value: formData.value ? parseFloat(formData.value) : null,
        additionalNotes: formData.additionalNotes && formData.additionalNotes.trim() ? formData.additionalNotes : 'No additional notes'
      };

      // Add leadRequest as JSON string
      formDataToSend.append('leadRequest', JSON.stringify(leadRequest));
      
      // Log the data being sent for debugging
      console.log('=== LEAD FORM SUBMISSION DEBUG ===');
      console.log('Full leadRequest object:', leadRequest);
      console.log('JSON string being sent:', JSON.stringify(leadRequest));
      console.log('Customer ID:', formData.customerId);
      console.log('Assigned To ID:', formData.assignedTo);
      console.log('Form Data State:', formData);
      console.log('================================');

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

      if (isEditing && selectedLead) {
        await dispatch(updateLead({ id: selectedLead.id, formData: formDataToSend }));
        dispatch(addNotification({
          type: 'success',
          message: 'Lead updated successfully',
          title: 'Success',
          duration: 3000,
        }));
      } else {
        await dispatch(createLead(formDataToSend));
        dispatch(addNotification({
          type: 'success',
          message: 'Lead created successfully',
          title: 'Success',
          duration: 3000,
        }));
      }
      navigate('/leads');
    } catch (error) {
      dispatch(addNotification({
        type: 'error',
        message: isEditing ? 'Failed to update lead' : 'Failed to create lead',
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
          <h2>Error loading lead</h2>
          <p>{error}</p>
          <BackButton to="/leads">← Back to Leads</BackButton>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader>
        <BackButton to="/leads">← Back to Leads</BackButton>
        <PageTitle>{isEditing ? 'Edit Lead' : 'Add New Lead'}</PageTitle>
      </PageHeader>

      <FormContainer>
        <FormCard>
          <form onSubmit={handleSubmit}>
            <FormGrid>
              <FormSection>
                <SectionTitle>📋 Lead Information</SectionTitle>
                
                <FormGroup>
                  <Label>
                    Title<Required>*</Required>
                  </Label>
                  <Input
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    placeholder="Enter lead title"
                    error={!!errors.title}
                  />
                  {errors.title && <ErrorMessage>{errors.title}</ErrorMessage>}
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
                      customerId: value ? value.toString() : '',
                      customerName: selectedCustomer?.name || ''
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

                <DropdownField
                  label="Assigned To"
                  value={formData.assignedTo ? parseInt(formData.assignedTo) : ''}
                  onChange={(value) => {
                    const selectedUser = users.find(u => u.id === value);
                    console.log('User selected:', selectedUser);
                    console.log('Assigned To dropdown value:', formData.assignedTo);
                    setFormData(prev => ({ 
                      ...prev, 
                      assignedTo: value ? value.toString() : '',
                      userName: selectedUser?.name || ''
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
                    Stage<Required>*</Required>
                  </Label>
                  <Select
                    name="currentStageId"
                    value={formData.currentStageId}
                    onChange={handleInputChange}
                    error={!!errors.currentStageId}
                  >
                    <option value="">Select stage</option>
                    <option value="1">New</option>
                    <option value="2">Qualified</option>
                    <option value="3">Proposal</option>
                    <option value="4">Negotiation</option>
                    <option value="5">Closed Won</option>
                    <option value="6">Closed Lost</option>
                  </Select>
                  {errors.currentStageId && <ErrorMessage>{errors.currentStageId}</ErrorMessage>}
                </FormGroup>
              </FormSection>

              <FormSection>
                <SectionTitle>📊 Lead Details</SectionTitle>
                
                <FormGroup>
                  <Label>Status</Label>
                  <Select
                    name="status"
                    value={formData.status}
                    onChange={handleInputChange}
                  >
                    <option value="NEW">New</option>
                    <option value="CONTACTED">Contacted</option>
                    <option value="QUALIFIED">Qualified</option>
                    <option value="PROPOSAL">Proposal</option>
                    <option value="NEGOTIATION">Negotiation</option>
                    <option value="CLOSED_WON">Closed Won</option>
                    <option value="CLOSED_LOST">Closed Lost</option>
                  </Select>
                </FormGroup>



                <FormGroup>
                  <Label>Expected Close Date</Label>
                  <Input
                    type="date"
                    name="expectedCloseDate"
                    value={formData.expectedCloseDate}
                    onChange={handleInputChange}
                    error={!!errors.expectedCloseDate}
                  />
                  {errors.expectedCloseDate && <ErrorMessage>{errors.expectedCloseDate}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>Value</Label>
                  <Input
                    type="number"
                    name="value"
                    value={formData.value}
                    onChange={handleInputChange}
                    placeholder="Enter lead value"
                    error={!!errors.value}
                  />
                  {errors.value && <ErrorMessage>{errors.value}</ErrorMessage>}
                </FormGroup>

                <FormGroup>
                  <Label>Source</Label>
                  <Select
                    name="source"
                    value={formData.source}
                    onChange={handleInputChange}
                  >
                    <option value="WEBSITE">Website</option>
                    <option value="REFERRAL">Referral</option>
                    <option value="SOCIAL_MEDIA">Social Media</option>
                    <option value="COLD_CALL">Cold Call</option>
                    <option value="EVENT">Event</option>
                    <option value="OTHER">Other</option>
                  </Select>
                </FormGroup>
              </FormSection>
            </FormGrid>

            <FormSection>
              <SectionTitle>📝 Additional Information</SectionTitle>
              
              <FormGroup>
                <Label>Additional Notes</Label>
                <TextArea
                  name="additionalNotes"
                  value={formData.additionalNotes}
                  onChange={handleInputChange}
                  placeholder="Add any additional notes about this lead..."
                  error={!!errors.additionalNotes}
                />
                {errors.additionalNotes && <ErrorMessage>{errors.additionalNotes}</ErrorMessage>}
              </FormGroup>
            </FormSection>

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
                maxFiles={3}
                maxFileSize={10}
                acceptedTypes={['.pdf', '.doc', '.docx', '.txt', '.jpg', '.jpeg', '.png']}
              />
            </FormSection>

            <FormActions>
              <CancelButton to="/leads">
                Cancel
              </CancelButton>
              <Button
                type="submit"
                variant="primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? 'Saving...' : (isEditing ? 'Update Lead' : 'Create Lead')}
              </Button>
            </FormActions>
          </form>
        </FormCard>
      </FormContainer>
    </PageContainer>
  );
};

export default LeadFormPage; 