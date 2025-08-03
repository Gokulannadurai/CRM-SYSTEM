import React, { useState } from 'react';
import styled from 'styled-components';
import { Attachment } from '../../types';
import { downloadFileFromBytes, getFileIcon } from '../../utils/fileUtils';

const AttachmentContainer = styled.div`
  margin-top: 16px;
`;

const AttachmentTitle = styled.h4`
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
`;

const AttachmentList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const AttachmentItem = styled.div`
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

const ButtonGroup = styled.div`
  display: flex;
  gap: 8px;
`;

const ActionButton = styled.button`
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  &:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
`;

const ViewButton = styled(ActionButton)`
  background: #28a745;
  color: white;

  &:hover:not(:disabled) {
    background: #218838;
  }
`;

const DownloadButton = styled(ActionButton)`
  background: #667eea;
  color: white;

  &:hover:not(:disabled) {
    background: #5a6fd8;
  }
`;

const NoAttachments = styled.div`
  text-align: center;
  padding: 24px;
  color: #6c757d;
  font-style: italic;
`;

// Modal Styles
const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
`;

const ModalContent = styled.div`
  background: white;
  border-radius: 12px;
  max-width: 90vw;
  max-height: 90vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  overflow: hidden;
`;

const ModalHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
`;

const ModalTitle = styled.h3`
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.2s;

  &:hover {
    background: #f8f9fa;
    color: #2c3e50;
  }
`;

const ModalBody = styled.div`
  flex: 1;
  overflow: auto;
  padding: 24px;
  max-height: calc(90vh - 120px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
`;

const ImagePreview = styled.img`
  max-width: 100%;
  max-height: 70vh;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: contain;
`;

const PDFPreview = styled.iframe`
  width: 100%;
  height: 70vh;
  min-height: 500px;
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const TextPreview = styled.div`
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  overflow-x: auto;
  max-height: 70vh;
  overflow-y: auto;
  width: 100%;
  max-width: 800px;
`;

const UnsupportedFileMessage = styled.div`
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
`;

const UnsupportedIcon = styled.div`
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
`;

const UnsupportedTitle = styled.h4`
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #2c3e50;
`;

const UnsupportedText = styled.p`
  font-size: 1rem;
  margin: 0;
`;

interface AttachmentListProps {
  attachments?: Attachment[];
  title?: string;
}

const AttachmentListComponent: React.FC<AttachmentListProps> = ({ 
  attachments = [], 
  title = "Attachments" 
}) => {
  const [viewingAttachment, setViewingAttachment] = useState<Attachment | null>(null);

  const handleDownload = (attachment: Attachment) => {
    if (attachment.bytes) {
      try {
        downloadFileFromBytes(attachment.bytes, attachment.fileName, attachment.fileType);
      } catch (error) {
        console.error('Download failed:', error);
        // Fallback to URL download if bytes download fails
        const link = document.createElement('a');
        link.href = attachment.url;
        link.download = attachment.fileName;
        link.target = '_blank';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    } else if (attachment.url) {
      // Fallback to URL download if bytes are not available
      const link = document.createElement('a');
      link.href = attachment.url;
      link.download = attachment.fileName;
      link.target = '_blank';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } else {
      console.error('No download source available for attachment:', attachment);
    }
  };

  const handleView = (attachment: Attachment) => {
    setViewingAttachment(attachment);
  };

  const closeModal = () => {
    setViewingAttachment(null);
  };

  const canViewFile = (fileType: string): boolean => {
    const normalizedType = fileType.toLowerCase().replace(/^\./, '');
    const viewableTypes = [
      'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp',
      'application/pdf',
      'text/plain', 'text/html', 'text/css', 'text/javascript',
      'application/json', 'application/xml'
    ];
    return viewableTypes.includes(normalizedType) || viewableTypes.some(type => 
      type.includes(normalizedType) || normalizedType.includes(type.split('/')[1])
    );
  };

  const canViewAttachment = (attachment: Attachment): boolean => {
    const hasBytes = attachment.bytes && (typeof attachment.bytes === 'string' || attachment.bytes.length > 0);
    return (hasBytes || !!attachment.url);
  };

  const renderFilePreview = (attachment: Attachment) => {
    const fileType = attachment.fileType.toLowerCase();
    const normalizedType = fileType.replace(/^\./, '');
    
    // Handle image files (including .png, .jpg, etc.)
    if (fileType.startsWith('image/') || ['png', 'jpg', 'jpeg', 'gif', 'webp'].includes(normalizedType)) {
      const mimeType = fileType.startsWith('image/') ? attachment.fileType : `image/${normalizedType}`;
      const imageUrl = attachment.bytes 
        ? `data:${mimeType};base64,${attachment.bytes}`
        : attachment.url;
      return <ImagePreview src={imageUrl} alt={attachment.fileName} />;
    }
    
    // Handle PDF files
    if (fileType === 'application/pdf' || normalizedType === 'pdf') {
      const pdfUrl = attachment.bytes 
        ? `data:${attachment.fileType};base64,${attachment.bytes}`
        : attachment.url;
      return <PDFPreview src={pdfUrl} title={attachment.fileName} />;
    }
    
    // Handle text files
    if (fileType.startsWith('text/') || ['txt', 'html', 'css', 'js', 'json', 'xml'].includes(normalizedType)) {
      return (
        <TextPreview>
          {attachment.bytes 
            ? (typeof attachment.bytes === 'string' 
                ? atob(attachment.bytes)
                : 'Binary content preview not available. Please download the file to view.')
            : 'Text content preview not available. Please download the file to view.'
          }
        </TextPreview>
      );
    }
    
    // For all other file types, show file info and download option
    return (
      <UnsupportedFileMessage>
        <UnsupportedIcon>{getFileIcon(attachment.fileType)}</UnsupportedIcon>
        <UnsupportedTitle>{attachment.fileName}</UnsupportedTitle>
        <UnsupportedText>
          File Type: {attachment.fileType}
        </UnsupportedText>
        <UnsupportedText>
          This file type cannot be previewed in the browser.
          Please use the download button to view the file.
        </UnsupportedText>
        <DownloadButton
          onClick={() => handleDownload(attachment)}
          style={{ marginTop: '16px', padding: '12px 24px', fontSize: '1rem' }}
        >
          Download File
        </DownloadButton>
      </UnsupportedFileMessage>
    );
  };

  if (!attachments || attachments.length === 0) {
    return (
      <AttachmentContainer>
        <AttachmentTitle>📎 {title}</AttachmentTitle>
        <NoAttachments>No attachments available</NoAttachments>
      </AttachmentContainer>
    );
  }

  return (
    <>
      <AttachmentContainer>
        <AttachmentTitle>📎 {title}</AttachmentTitle>
        <AttachmentList>
          {attachments.map((attachment, index) => (
            <AttachmentItem key={attachment.id || index}>
              <AttachmentInfo>
                <FileIcon>{getFileIcon(attachment.fileType)}</FileIcon>
                <FileDetails>
                  <FileName>{attachment.fileName}</FileName>
                  <FileType>{attachment.fileType}</FileType>
                </FileDetails>
              </AttachmentInfo>
              <ButtonGroup>
                <ViewButton
                  onClick={() => handleView(attachment)}
                  disabled={!canViewAttachment(attachment)}
                  title={canViewAttachment(attachment) ? "View file in browser" : "No preview available"}
                >
                  View
                </ViewButton>
                <DownloadButton
                  onClick={() => handleDownload(attachment)}
                  disabled={!attachment.bytes && !attachment.url}
                  title={attachment.bytes ? 'Download file using bytes' : attachment.url ? 'Download file from URL' : 'No download available'}
                >
                  Download
                </DownloadButton>
              </ButtonGroup>
            </AttachmentItem>
          ))}
        </AttachmentList>
      </AttachmentContainer>

      {/* View Modal */}
      {viewingAttachment && (
        <ModalOverlay onClick={closeModal}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <ModalHeader>
              <ModalTitle>{viewingAttachment.fileName}</ModalTitle>
              <CloseButton onClick={closeModal}>&times;</CloseButton>
            </ModalHeader>
            <ModalBody>
              {renderFilePreview(viewingAttachment)}
            </ModalBody>
          </ModalContent>
        </ModalOverlay>
      )}
    </>
  );
};

export default AttachmentListComponent; 