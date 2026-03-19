import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LeaveService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { LeaveBalanceResponse, LeaveApplicationResponse, LeaveType } from '../../core/models';

@Component({
  selector: 'app-leaves',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, DatePipe],
  templateUrl: './leaves.component.html',
  styleUrl: './leaves.component.scss'
})
export class LeavesComponent implements OnInit {
  auth = inject(AuthService);
  private svc = inject(LeaveService);
  private fb  = inject(FormBuilder);

  balance      = signal<LeaveBalanceResponse[]>([]);
  myLeaves     = signal<LeaveApplicationResponse[]>([]);
  teamLeaves   = signal<LeaveApplicationResponse[]>([]);
  pendingLeaves = signal<LeaveApplicationResponse[]>([]);
  allLeaves    = signal<LeaveApplicationResponse[]>([]);

  activeTab     = signal<'mine' | 'pending' | 'team' | 'all'>('mine');
  showApply     = signal(false);
  applying      = signal(false);
  applyError    = signal('');
  processTarget = signal<number | null>(null);
  processAction = signal<'APPROVE' | 'REJECT'>('APPROVE');
  processComment = '';

  applyForm = this.fb.group({
    leaveType: ['CASUAL' as LeaveType],
    startDate: [''],
    endDate:   [''],
    reason:    ['']
  });

  currentLeaves = () => {
    switch (this.activeTab()) {
      case 'mine':    return this.myLeaves();
      case 'pending': return this.pendingLeaves();
      case 'team':    return this.teamLeaves();
      case 'all':     return this.allLeaves();
    }
  };

  balanceStyle(type: string): string {
    if (type === 'SICK') return '--accent:var(--accent-amber)';
    if (type === 'PAID') return '--accent:var(--accent-green)';
    return '--accent:var(--accent)';
  }

  ngOnInit() {
    this.svc.getBalance().subscribe(res => this.balance.set(res.data || []));
    this.svc.getMyLeaves().subscribe(res => {
      const sorted = (res.data || []).sort(
        (a, b) => new Date(b.appliedAt).getTime() - new Date(a.appliedAt).getTime()
      );
      this.myLeaves.set(sorted);
    });
    if (this.auth.isManagerOrAdmin) {
      this.svc.getPendingTeamLeaves().subscribe(res => this.pendingLeaves.set(res.data || []));
    }
  }

  loadPending() {
    this.activeTab.set('pending');
    this.svc.getPendingTeamLeaves().subscribe(res => this.pendingLeaves.set(res.data || []));
  }

  loadTeamAll() {
    this.activeTab.set('team');
    this.svc.getTeamLeaves().subscribe(res => this.teamLeaves.set(res.data || []));
  }

  loadAll() {
    this.activeTab.set('all');
    this.svc.getAllLeaves().subscribe(res => this.allLeaves.set(res.data || []));
  }

  applyLeave() {
    const val = this.applyForm.value;
    if (!val.startDate || !val.endDate) { this.applyError.set('Please fill all required fields'); return; }
    this.applying.set(true);
    this.applyError.set('');
    this.svc.applyLeave({
      leaveType: val.leaveType as LeaveType,
      startDate: val.startDate,
      endDate:   val.endDate,
      reason:    val.reason || undefined
    }).subscribe({
      next: res => {
        this.myLeaves.update(list => [res.data, ...list]);
        this.showApply.set(false);
        this.applyForm.reset({ leaveType: 'CASUAL' });
        this.applying.set(false);
        this.svc.getBalance().subscribe(r => this.balance.set(r.data || []));
      },
      error: err => {
        this.applyError.set(err.error?.message || 'Failed to apply leave');
        this.applying.set(false);
      }
    });
  }

  cancelLeave(id: number) {
    this.svc.cancelLeave(id).subscribe(res => {
      this.myLeaves.update(list => list.map(l => l.id === res.data.id ? res.data : l));
    });
  }

  processLeave(id: number, action: 'APPROVE' | 'REJECT') {
    this.processTarget.set(id);
    this.processAction.set(action);
    this.processComment = '';
  }

  confirmProcess() {
    const id = this.processTarget();
    if (id === null) return;
    this.svc.processLeave(id, { action: this.processAction(), comment: this.processComment || undefined }).subscribe({
      next: () => {
        this.pendingLeaves.update(list => list.filter(l => l.id !== id));
        this.processTarget.set(null);
        this.svc.getPendingTeamLeaves().subscribe(r => this.pendingLeaves.set(r.data || []));
      }
    });
  }
}
