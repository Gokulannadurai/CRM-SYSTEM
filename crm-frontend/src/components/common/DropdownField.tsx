import React from 'react';
import styled from 'styled-components';

interface DropdownOption {
  id: number;
  name?: string;
  title?: string;
  firstName?: string;
  lastName?: string;
}

interface DropdownFieldProps {
  label: string;
  value: number | string;
  onChange: (value: number | string) => void;
  options: DropdownOption[];
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  error?: string;
  loading?: boolean;
  name?: string;
}

const FieldContainer = styled.div`
  margin-bottom: 20px;
`;

const Label = styled.label<{ required?: boolean }>`
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  
  ${props => props.required && `
    &::after {
      content: ' *';
      color: #e74c3c;
    }
  `}
`;

const SelectContainer = styled.div`
  position: relative;
`;

const Select = styled.select<{ hasError?: boolean; disabled?: boolean }>`
  width: 100%;
  padding: 12px 16px;
  border: 2px solid ${props => props.hasError ? '#e74c3c' : '#ddd'};
  border-radius: 8px;
  font-size: 14px;
  background-color: ${props => props.disabled ? '#f5f5f5' : '#fff'};
  color: ${props => props.disabled ? '#999' : '#333'};
  cursor: ${props => props.disabled ? 'not-allowed' : 'pointer'};
  transition: border-color 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${props => props.hasError ? '#e74c3c' : '#007bff'};
    box-shadow: 0 0 0 3px ${props => props.hasError ? 'rgba(231, 76, 60, 0.1)' : 'rgba(0, 123, 255, 0.1)'};
  }

  &:hover:not(:disabled) {
    border-color: ${props => props.hasError ? '#e74c3c' : '#007bff'};
  }
`;

const LoadingSpinner = styled.div`
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;

  @keyframes spin {
    0% { transform: translateY(-50%) rotate(0deg); }
    100% { transform: translateY(-50%) rotate(360deg); }
  }
`;

const ErrorMessage = styled.div`
  color: #e74c3c;
  font-size: 12px;
  margin-top: 4px;
`;

const EmptyMessage = styled.div`
  color: #999;
  font-size: 12px;
  margin-top: 4px;
  font-style: italic;
`;

const DropdownField: React.FC<DropdownFieldProps> = ({
  label,
  value,
  onChange,
  options,
  placeholder = 'Select an option...',
  required = false,
  disabled = false,
  error,
  loading = false,
  name,
}) => {
  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = event.target.value;
    onChange(selectedValue === '' ? '' : Number(selectedValue));
  };

  const getDisplayValue = (option: DropdownOption) => {
    if (option.name) return option.name;
    if (option.title) return option.title;
    if (option.firstName && option.lastName) return `${option.firstName} ${option.lastName}`;
    if (option.firstName) return option.firstName;
    if (option.lastName) return option.lastName;
    return `Option ${option.id}`;
  };

  return (
    <FieldContainer>
      <Label required={required}>
        {label}
      </Label>
      
      <SelectContainer>
        <Select
          value={value === undefined || value === null ? '' : value}
          onChange={handleChange}
          required={required}
          disabled={disabled || loading}
          hasError={!!error}
          name={name}
        >
          <option value="">{placeholder}</option>
          {options.map((option) => (
            <option key={option.id} value={option.id}>
              {getDisplayValue(option)}
            </option>
          ))}
        </Select>
        
        {loading && <LoadingSpinner />}
      </SelectContainer>
      
      {error && <ErrorMessage>{error}</ErrorMessage>}
      
      {!loading && options.length === 0 && !error && (
        <EmptyMessage>No options available</EmptyMessage>
      )}
    </FieldContainer>
  );
};

export default DropdownField; 