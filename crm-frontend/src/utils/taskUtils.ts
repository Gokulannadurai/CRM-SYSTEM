import { Task } from '../types';

/**
 * Maps task boolean fields to status string
 * @param task - The task object with boolean fields
 * @returns The mapped status string
 */
export const getTaskStatus = (task: Task): string => {
  // If task is cancelled, return CANCELLED
  if (task.isCancelled) {
    return 'CANCELLED';
  }
  
  // If task is completed, return COMPLETED
  if (task.isCompleted) {
    return 'COMPLETED';
  }
  
  // If task is started but not completed, return IN_PROGRESS
  if (task.isStarted) {
    return 'IN_PROGRESS';
  }
  
  // Default: task is not started, return PENDING
  return 'PENDING';
};

/**
 * Gets the display text for a task status
 * @param status - The status string
 * @returns The display text
 */
export const getTaskStatusDisplay = (status: string): string => {
  switch (status) {
    case 'PENDING':
      return 'Start';
    case 'IN_PROGRESS':
      return 'In Progress';
    case 'COMPLETED':
      return 'Completed';
    case 'CANCELLED':
      return 'Cancelled';
    default:
      return status.replace('_', ' ');
  }
};

/**
 * Gets the next status for a task
 * @param currentStatus - The current status
 * @returns The next status
 */
export const getNextTaskStatus = (currentStatus: string): string => {
  switch (currentStatus) {
    case 'PENDING':
      return 'IN_PROGRESS';
    case 'IN_PROGRESS':
      return 'COMPLETED';
    case 'COMPLETED':
      return 'COMPLETED'; // No next status after completed
    case 'CANCELLED':
      return 'CANCELLED'; // No next status after cancelled
    default:
      return 'PENDING';
  }
};

/**
 * Gets the button text for the next status action
 * @param currentStatus - The current status
 * @returns The button text
 */
export const getTaskStatusButtonText = (currentStatus: string): string => {
  switch (currentStatus) {
    case 'PENDING':
      return 'Start Task';
    case 'IN_PROGRESS':
      return 'Mark Complete';
    case 'COMPLETED':
      return 'Completed';
    case 'CANCELLED':
      return 'Cancelled';
    default:
      return 'Update Status';
  }
}; 