// src/app/core/models/auth.model.ts
export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export type Role = 'ADMIN' | 'MANAGER' | 'EMPLOYEE';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface UserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  department?: string;
  designation?: string;
  employeeCode?: string;
  role: Role;
  status: 'ACTIVE' | 'INACTIVE';
  dateOfJoining?: string;
  manager?: ManagerInfo;
  createdAt?: string;
}

export interface ManagerInfo {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  designation?: string;
}

export interface CreateEmployeeRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  department?: string;
  designation?: string;
  employeeCode?: string;
  role: Role;
  managerId?: number;
  dateOfJoining?: string;
}

export interface UpdateProfileRequest {
  phone?: string;
  address?: string;
  firstName?: string;
  lastName?: string;
}

// Leave Models
export type LeaveType = 'CASUAL' | 'SICK' | 'PAID';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export interface LeaveBalanceResponse {
  id: number;
  leaveType: LeaveType;
  year: number;
  totalDays: number;
  usedDays: number;
  remainingDays: number;
}

export interface LeaveApplicationRequest {
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  reason?: string;
}

export interface LeaveApplicationResponse {
  id: number;
  employeeName: string;
  employeeCode?: string;
  department?: string;
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  numberOfDays: number;
  reason?: string;
  managerComment?: string;
  status: LeaveStatus;
  approvedByName?: string;
  appliedAt: string;
  updatedAt?: string;
}

export interface LeaveActionRequest {
  action: 'APPROVE' | 'REJECT';
  comment?: string;
}

export interface LeaveQuota {
  id: number;
  leaveType: LeaveType;
  defaultDays: number;
  year: number;
}

export interface LeaveQuotaRequest {
  leaveType: LeaveType;
  defaultDays: number;
  year: number;
}

// Performance Models
export type ReviewStatus = 'DRAFT' | 'SUBMITTED' | 'REVIEWED';
export type GoalStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';

export interface PerformanceReviewRequest {
  reviewYear: number;
  reviewPeriod: string;
  selfAssessment?: string;
}

export interface PerformanceReviewResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  managerName?: string;
  reviewYear: number;
  reviewPeriod: string;
  selfAssessment?: string;
  managerFeedback?: string;
  rating?: number;
  status: ReviewStatus;
  createdAt: string;
  submittedAt?: string;
  reviewedAt?: string;
}

export interface ManagerFeedbackRequest {
  feedback: string;
  rating: number;
}

export interface GoalRequest {
  title: string;
  description?: string;
  year: number;
  targetDate?: string;
  progressPercentage: number;
  status: GoalStatus;
}

export interface GoalResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  title: string;
  description?: string;
  year: number;
  targetDate?: string;
  progressPercentage: number;
  status: GoalStatus;
  createdAt: string;
  updatedAt?: string;
}

// Dashboard
export interface DashboardStats {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  totalManagers: number;
  totalAdmins: number;
}
