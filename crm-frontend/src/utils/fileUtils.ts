/**
 * File utility functions for handling file downloads and conversions
 */

/**
 * Download a file from base64 encoded bytes
 * @param bytes - The file bytes as base64 string or number array
 * @param fileName - The name of the file to download
 * @param fileType - The MIME type of the file
 */
export const downloadFileFromBytes = (bytes: string | number[], fileName: string, fileType: string): void => {
  try {
    let uint8Array: Uint8Array;
    
    if (typeof bytes === 'string') {
      // Handle base64 encoded string
      const binaryString = atob(bytes);
      uint8Array = new Uint8Array(binaryString.length);
      for (let i = 0; i < binaryString.length; i++) {
        uint8Array[i] = binaryString.charCodeAt(i);
      }
    } else {
      // Handle number array (backward compatibility)
      uint8Array = new Uint8Array(bytes);
    }
    
    // Create blob from bytes
    const blob = new Blob([uint8Array], { type: fileType });
    
    // Create download link
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    
    // Trigger download
    document.body.appendChild(link);
    link.click();
    
    // Cleanup
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Error downloading file:', error);
    throw new Error('Failed to download file');
  }
};

/**
 * Get file icon based on file type
 * @param fileType - The file extension or MIME type
 * @returns The appropriate icon class or emoji
 */
export const getFileIcon = (fileType: string): string => {
  const type = fileType.toLowerCase();
  
  if (type.includes('pdf')) return '📄';
  if (type.includes('doc') || type.includes('docx')) return '📝';
  if (type.includes('xls') || type.includes('xlsx')) return '📊';
  if (type.includes('ppt') || type.includes('pptx')) return '📈';
  if (type.includes('image') || type.includes('jpg') || type.includes('jpeg') || type.includes('png') || type.includes('gif')) return '🖼️';
  if (type.includes('zip') || type.includes('rar') || type.includes('7z')) return '📦';
  if (type.includes('txt')) return '📄';
  
  return '📎';
};

/**
 * Format file size from bytes
 * @param bytes - File size in bytes
 * @returns Formatted file size string
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}; 