import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { PerformanceService } from '../../core/services/api.service';
import { GoalResponse, GoalStatus } from '../../core/models';

@Component({
  selector: 'app-goals',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe],
  templateUrl: './goals.component.html',
  styleUrl: './goals.component.scss'
})
export class GoalsComponent implements OnInit {
  private svc = inject(PerformanceService);
  private fb  = inject(FormBuilder);

  goals       = signal<GoalResponse[]>([]);
  loading     = signal(true);
  showModal   = signal(false);
  editTarget  = signal<GoalResponse | null>(null);
  saving      = signal(false);
  error       = signal('');
  currentYear = new Date().getFullYear();

  completedCount  = () => this.goals().filter(g => g.status === 'COMPLETED').length;
  inProgressCount = () => this.goals().filter(g => g.status === 'IN_PROGRESS').length;

  goalForm = this.fb.group({
    title:              [''],
    description:        [''],
    year:               [this.currentYear],
    targetDate:         [''],
    status:             ['NOT_STARTED' as GoalStatus],
    progressPercentage: [0]
  });

  ngOnInit() {
    this.svc.getMyGoals().subscribe({
      next:  res => { this.goals.set(res.data || []); this.loading.set(false); },
      error: ()  => this.loading.set(false)
    });
  }

  openCreate() {
    this.editTarget.set(null);
    this.goalForm.reset({ year: this.currentYear, status: 'NOT_STARTED', progressPercentage: 0 });
    this.showModal.set(true);
  }

  openEdit(goal: GoalResponse) {
    this.editTarget.set(goal);
    this.goalForm.patchValue({
      title:              goal.title,
      description:        goal.description || '',
      year:               goal.year,
      targetDate:         goal.targetDate  || '',
      status:             goal.status,
      progressPercentage: goal.progressPercentage
    });
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editTarget.set(null);
    this.error.set('');
  }

  save() {
    const val = this.goalForm.value;
    if (!val.title) { this.error.set('Title is required'); return; }
    this.saving.set(true);
    this.error.set('');

    const req = {
      title:              val.title!,
      description:        val.description || undefined,
      year:               val.year!,
      targetDate:         val.targetDate  || undefined,
      status:             val.status as GoalStatus,
      progressPercentage: val.progressPercentage ?? 0
    };

    const obs = this.editTarget()
      ? this.svc.updateGoal(this.editTarget()!.id, req)
      : this.svc.createGoal(req);

    obs.subscribe({
      next: res => {
        if (this.editTarget()) {
          this.goals.update(list => list.map(g => g.id === res.data.id ? res.data : g));
        } else {
          this.goals.update(list => [res.data, ...list]);
        }
        this.closeModal();
        this.saving.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Failed to save goal');
        this.saving.set(false);
      }
    });
  }

  deleteGoal(id: number) {
    if (!confirm('Delete this goal?')) return;
    this.svc.deleteGoal(id).subscribe(() => {
      this.goals.update(list => list.filter(g => g.id !== id));
    });
  }
}
