import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/api.service';
import { DashboardStats, LeaveQuota, LeaveType } from '../../core/models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  private svc = inject(AdminService);
  private fb  = inject(FormBuilder);

  stats        = signal<DashboardStats | null>(null);
  quotas       = signal<LeaveQuota[]>([]);
  savingQuota  = signal(false);
  quotaSaved   = signal(false);
  quotaError   = signal('');
  resetting    = signal(false);
  resetSuccess = signal('');
  resetError   = signal('');

  quotaForm = this.fb.group({
    leaveType:   ['CASUAL' as LeaveType],
    defaultDays: [12],
    year:        [new Date().getFullYear()]
  });

  resetForm = this.fb.group({
    year: [new Date().getFullYear()]
  });

  ngOnInit() {
    this.svc.getDashboard().subscribe(res => this.stats.set(res.data));
    this.svc.getLeaveQuotas().subscribe(res => this.quotas.set(res.data || []));
  }

  saveQuota() {
    const val = this.quotaForm.value;
    this.savingQuota.set(true);
    this.quotaError.set('');
    this.svc.configureLeaveQuota({ leaveType: val.leaveType as LeaveType, defaultDays: val.defaultDays!, year: val.year! }).subscribe({
      next: () => {
        this.quotaSaved.set(true);
        this.savingQuota.set(false);
        setTimeout(() => this.quotaSaved.set(false), 3000);
        this.svc.getLeaveQuotas().subscribe(res => this.quotas.set(res.data || []));
      },
      error: err => { this.quotaError.set(err.error?.message || 'Failed'); this.savingQuota.set(false); }
    });
  }

  resetBalances() {
    const year = this.resetForm.value.year!;
    if (!confirm(`Reset leave balances for ${year}?`)) return;
    this.resetting.set(true);
    this.resetError.set('');
    this.svc.resetLeaveBalances(year).subscribe({
      next: () => {
        this.resetSuccess.set(`Leave balances reset for ${year} successfully!`);
        this.resetting.set(false);
        setTimeout(() => this.resetSuccess.set(''), 5000);
      },
      error: err => { this.resetError.set(err.error?.message || 'Reset failed'); this.resetting.set(false); }
    });
  }
}
