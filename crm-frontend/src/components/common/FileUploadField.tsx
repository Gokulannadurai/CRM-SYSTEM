import React, { useState, useRef } from 'react';
import styled from 'styled-components';

interface FileUploadFieldProps {
  label: string;
  value: File[];
  onChange: (files: File[]) => void;
  maxFiles?: number;
  maxFileSize?: number; // in MB
  acceptedTypes?: string[];
  error?: string;
  required?: boolean;
}

const FileUploadContainer = styled.div`
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

const UploadArea = styled.div<{ isDragOver: boolean; hasError: boolean }>`
  border: 2px dashed ${props => props.hasError ? '#e74c3c' : props.isDragOver ? '#667eea' : '#ddd'};
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  background: ${props => props.isDragOver ? '#f8f9ff' : '#fafafa'};
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    border-color: #667eea;
    background: #f8f9ff;
  }
`;

const UploadIcon = styled.div`
  font-size: 2rem;
  color: #667eea;
  margin-bottom: 8px;
`;

const UploadText = styled.p`
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
`;

const FileInput = styled.input`
  display: none;
`;

const FileList = styled.div`
  margin-top: 16px;
`;

const FileItem = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 8px;
`;

const FileInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const FileName = styled.span`
  font-size: 0.9rem;
  color: #2c3e50;
`;

const FileSize = styled.span`
  font-size: 0.8rem;
  color: #6c757d;
`;

const RemoveButton = styled.button`
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #c0392b;
  }
`;

const ErrorMessage = styled.div`
  color: #e74c3c;
  font-size: 0.8rem;
  margin-top: 4px;
`;

const FileUploadField: React.FC<FileUploadFieldProps> = ({
  label,
  value,
  onChange,
  maxFiles = 3,
  maxFileSize = 10, // 10MB default
  acceptedTypes = ['.pdf', '.doc', '.docx', '.txt', '.jpg', '.jpeg', '.png'],
  error,
  required = false
}) => {
  const [isDragOver, setIsDragOver] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (files: FileList | null) => {
    if (!files) return;

    const newFiles = Array.from(files);
    const validFiles: File[] = [];
    const errors: string[] = [];

    newFiles.forEach(file => {
      // Check file size
      if (file.size > maxFileSize * 1024 * 1024) {
        errors.push(`${file.name} is too large. Maximum size is ${maxFileSize}MB.`);
        return;
      }

      // Check file type
      const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();
      if (!acceptedTypes.includes(fileExtension)) {
        errors.push(`${file.name} is not an accepted file type.`);
        return;
      }

      validFiles.push(file);
    });

    // Check total file count
    if (value.length + validFiles.length > maxFiles) {
      errors.push(`Maximum ${maxFiles} files allowed.`);
    }

    if (errors.length > 0) {
      alert(errors.join('\n'));
      return;
    }

    onChange([...value, ...validFiles]);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    handleFileSelect(e.dataTransfer.files);
  };

  const removeFile = (index: number) => {
    const newFiles = value.filter((_, i) => i !== index);
    onChange(newFiles);
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <FileUploadContainer>
      <Label>
        {label}
        {required && <Required>*</Required>}
      </Label>
      
      <UploadArea
        isDragOver={isDragOver}
        hasError={!!error}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
      >
        <UploadIcon>📁</UploadIcon>
        <UploadText>
          Drag and drop files here, or click to select files
        </UploadText>
        <UploadText style={{ fontSize: '0.8rem', marginTop: '4px' }}>
          Accepted types: {acceptedTypes.join(', ')} | Max size: {maxFileSize}MB | Max files: {maxFiles}
        </UploadText>
      </UploadArea>

      <FileInput
        ref={fileInputRef}
        type="file"
        multiple
        accept={acceptedTypes.join(',')}
        onChange={(e) => handleFileSelect(e.target.files)}
      />

      {value.length > 0 && (
        <FileList>
          {value.map((file, index) => (
            <FileItem key={index}>
              <FileInfo>
                <FileName>{file.name}</FileName>
                <FileSize>({formatFileSize(file.size)})</FileSize>
              </FileInfo>
              <RemoveButton onClick={() => removeFile(index)}>
                Remove
              </RemoveButton>
            </FileItem>
          ))}
        </FileList>
      )}

      {error && <ErrorMessage>{error}</ErrorMessage>}
    </FileUploadContainer>
  );
};

export default FileUploadField; 