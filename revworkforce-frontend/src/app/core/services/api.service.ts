import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ApiResponse, UserResponse, CreateEmployeeRequest, UpdateProfileRequest,
  LeaveBalanceResponse, LeaveApplicationRequest, LeaveApplicationResponse,
  LeaveActionRequest, LeaveQuota, LeaveQuotaRequest,
  PerformanceReviewRequest, PerformanceReviewResponse, ManagerFeedbackRequest,
  GoalRequest, GoalResponse, DashboardStats
} from '../models';

const BASE = 'http://localhost:8080/api';

// ========== Employee Service ==========
@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private http = inject(HttpClient);
  private url = `${BASE}/employees`;

  getMyProfile(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.url}/me`);
  }

  updateProfile(req: UpdateProfileRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.url}/me`, req);
  }

  getAllEmployees(): Observable<ApiResponse<UserResponse[]>> {
    return this.http.get<ApiResponse<UserResponse[]>>(this.url);
  }

  getEmployee(id: number): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.url}/${id}`);
  }

  getManagers(): Observable<ApiResponse<UserResponse[]>> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.url}/managers`);
  }

  getMyTeam(): Observable<ApiResponse<UserResponse[]>> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.url}/my-team`);
  }
}

// ========== Leave Service ==========
@Injectable({ providedIn: 'root' })
export class LeaveService {
  private http = inject(HttpClient);
  private url = `${BASE}/leaves`;

  getBalance(): Observable<ApiResponse<LeaveBalanceResponse[]>> {
    return this.http.get<ApiResponse<LeaveBalanceResponse[]>>(`${this.url}/balance`);
  }

  applyLeave(req: LeaveApplicationRequest): Observable<ApiResponse<LeaveApplicationResponse>> {
    return this.http.post<ApiResponse<LeaveApplicationResponse>>(`${this.url}/apply`, req);
  }

  getMyLeaves(): Observable<ApiResponse<LeaveApplicationResponse[]>> {
    return this.http.get<ApiResponse<LeaveApplicationResponse[]>>(`${this.url}/my-leaves`);
  }

  cancelLeave(id: number): Observable<ApiResponse<LeaveApplicationResponse>> {
    return this.http.put<ApiResponse<LeaveApplicationResponse>>(`${this.url}/${id}/cancel`, {});
  }

  getTeamLeaves(): Observable<ApiResponse<LeaveApplicationResponse[]>> {
    return this.http.get<ApiResponse<LeaveApplicationResponse[]>>(`${this.url}/team`);
  }

  getPendingTeamLeaves(): Observable<ApiResponse<LeaveApplicationResponse[]>> {
    return this.http.get<ApiResponse<LeaveApplicationResponse[]>>(`${this.url}/team/pending`);
  }

  processLeave(id: number, req: LeaveActionRequest): Observable<ApiResponse<LeaveApplicationResponse>> {
    return this.http.put<ApiResponse<LeaveApplicationResponse>>(`${this.url}/${id}/process`, req);
  }

  getAllLeaves(): Observable<ApiResponse<LeaveApplicationResponse[]>> {
    return this.http.get<ApiResponse<LeaveApplicationResponse[]>>(`${this.url}/all`);
  }
}

// ========== Performance Service ==========
@Injectable({ providedIn: 'root' })
export class PerformanceService {
  private http = inject(HttpClient);
  private url = `${BASE}/performance`;

  createReview(req: PerformanceReviewRequest): Observable<ApiResponse<PerformanceReviewResponse>> {
    return this.http.post<ApiResponse<PerformanceReviewResponse>>(`${this.url}/reviews`, req);
  }

  updateReview(id: number, req: PerformanceReviewRequest): Observable<ApiResponse<PerformanceReviewResponse>> {
    return this.http.put<ApiResponse<PerformanceReviewResponse>>(`${this.url}/reviews/${id}`, req);
  }

  submitReview(id: number): Observable<ApiResponse<PerformanceReviewResponse>> {
    return this.http.put<ApiResponse<PerformanceReviewResponse>>(`${this.url}/reviews/${id}/submit`, {});
  }

  getMyReviews(): Observable<ApiResponse<PerformanceReviewResponse[]>> {
    return this.http.get<ApiResponse<PerformanceReviewResponse[]>>(`${this.url}/reviews/my`);
  }

  getTeamReviews(): Observable<ApiResponse<PerformanceReviewResponse[]>> {
    return this.http.get<ApiResponse<PerformanceReviewResponse[]>>(`${this.url}/reviews/team`);
  }

  getPendingTeamReviews(): Observable<ApiResponse<PerformanceReviewResponse[]>> {
    return this.http.get<ApiResponse<PerformanceReviewResponse[]>>(`${this.url}/reviews/team/pending`);
  }

  addFeedback(id: number, req: ManagerFeedbackRequest): Observable<ApiResponse<PerformanceReviewResponse>> {
    return this.http.put<ApiResponse<PerformanceReviewResponse>>(`${this.url}/reviews/${id}/feedback`, req);
  }

  createGoal(req: GoalRequest): Observable<ApiResponse<GoalResponse>> {
    return this.http.post<ApiResponse<GoalResponse>>(`${this.url}/goals`, req);
  }

  updateGoal(id: number, req: GoalRequest): Observable<ApiResponse<GoalResponse>> {
    return this.http.put<ApiResponse<GoalResponse>>(`${this.url}/goals/${id}`, req);
  }

  getMyGoals(): Observable<ApiResponse<GoalResponse[]>> {
    return this.http.get<ApiResponse<GoalResponse[]>>(`${this.url}/goals/my`);
  }

  getMyGoalsByYear(year: number): Observable<ApiResponse<GoalResponse[]>> {
    return this.http.get<ApiResponse<GoalResponse[]>>(`${this.url}/goals/my/${year}`);
  }

  deleteGoal(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.url}/goals/${id}`);
  }
}

// ========== Admin Service ==========
@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private url = `${BASE}/admin`;

  getDashboard(): Observable<ApiResponse<DashboardStats>> {
    return this.http.get<ApiResponse<DashboardStats>>(`${this.url}/dashboard`);
  }

  createEmployee(req: CreateEmployeeRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.url}/employees`, req);
  }

  assignManager(id: number, managerId: number): Observable<ApiResponse<UserResponse>> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.url}/employees/${id}/assign-manager/${managerId}`, {});
  }

  toggleStatus(id: number): Observable<ApiResponse<UserResponse>> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.url}/employees/${id}/toggle-status`, {});
  }

  configureLeaveQuota(req: LeaveQuotaRequest): Observable<ApiResponse<LeaveQuota>> {
    return this.http.post<ApiResponse<LeaveQuota>>(`${this.url}/leave-quotas`, req);
  }

  getLeaveQuotas(): Observable<ApiResponse<LeaveQuota[]>> {
    return this.http.get<ApiResponse<LeaveQuota[]>>(`${this.url}/leave-quotas`);
  }

  resetLeaveBalances(year: number): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.url}/reset-leave-balances/${year}`, {});
  }
}
