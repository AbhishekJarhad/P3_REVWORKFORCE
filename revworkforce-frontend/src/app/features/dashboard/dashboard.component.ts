import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AdminService, LeaveService, PerformanceService } from '../../core/services/api.service';
import { DashboardStats, LeaveBalanceResponse, LeaveApplicationResponse, GoalResponse } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  auth = inject(AuthService);
  private adminSvc = inject(AdminService);
  private leaveSvc = inject(LeaveService);
  private perfSvc = inject(PerformanceService);

  stats        = signal<DashboardStats | null>(null);
  leaveBalance = signal<LeaveBalanceResponse[]>([]);
  recentLeaves = signal<LeaveApplicationResponse[]>([]);
  activeGoals  = signal<GoalResponse[]>([]);
  leaveLoading = signal(true);

  ngOnInit() {
    if (this.auth.isAdmin) {
      this.adminSvc.getDashboard().subscribe(res => {
        if (res.data) this.stats.set(res.data);
      });
    }

    this.leaveSvc.getBalance().subscribe({
      next: res => { this.leaveBalance.set(res.data || []); this.leaveLoading.set(false); },
      error: ()  => this.leaveLoading.set(false)
    });

    this.leaveSvc.getMyLeaves().subscribe(res => {
      const sorted = (res.data || []).sort(
        (a, b) => new Date(b.appliedAt).getTime() - new Date(a.appliedAt).getTime()
      );
      this.recentLeaves.set(sorted.slice(0, 4));
    });

    this.perfSvc.getMyGoals().subscribe(res => {
      const active = (res.data || []).filter(g => g.status !== 'COMPLETED');
      this.activeGoals.set(active.slice(0, 3));
    });
  }

  timeGreeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'morning';
    if (h < 17) return 'afternoon';
    return 'evening';
  }

  today(): string {
    return new Date().toLocaleDateString('en-IN', {
      weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    });
  }
}
