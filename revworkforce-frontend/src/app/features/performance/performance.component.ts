import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PerformanceService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { PerformanceReviewResponse } from '../../core/models';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './performance.component.html',
  styleUrl: './performance.component.scss'
})
export class PerformanceComponent implements OnInit {
  auth = inject(AuthService);
  private svc = inject(PerformanceService);
  private fb  = inject(FormBuilder);

  myReviews      = signal<PerformanceReviewResponse[]>([]);
  teamReviews    = signal<PerformanceReviewResponse[]>([]);
  pendingReviews = signal<PerformanceReviewResponse[]>([]);
  tab            = signal<'my' | 'pending' | 'team'>('my');

  showCreate  = signal(false);
  editTarget  = signal<PerformanceReviewResponse | null>(null);
  feedbackTarget = signal<PerformanceReviewResponse | null>(null);
  saving      = signal(false);
  createError = signal('');
  feedbackText   = '';
  feedbackRating = 0;
  currentYear    = new Date().getFullYear();

  displayedReviews = () => {
    switch (this.tab()) {
      case 'my':      return this.myReviews();
      case 'pending': return this.pendingReviews();
      case 'team':    return this.teamReviews();
    }
  };

  reviewForm = this.fb.group({
    reviewYear:     [this.currentYear],
    reviewPeriod:   ['Q1'],
    selfAssessment: ['']
  });

  ngOnInit() {
    this.svc.getMyReviews().subscribe(res => this.myReviews.set(res.data || []));
    if (this.auth.isManagerOrAdmin) {
      this.svc.getPendingTeamReviews().subscribe(res => this.pendingReviews.set(res.data || []));
    }
  }

  loadPending() {
    this.tab.set('pending');
    this.svc.getPendingTeamReviews().subscribe(res => this.pendingReviews.set(res.data || []));
  }

  loadTeam() {
    this.tab.set('team');
    this.svc.getTeamReviews().subscribe(res => this.teamReviews.set(res.data || []));
  }

  openEdit(r: PerformanceReviewResponse) {
    this.editTarget.set(r);
    this.reviewForm.patchValue({ reviewYear: r.reviewYear, reviewPeriod: r.reviewPeriod, selfAssessment: r.selfAssessment || '' });
    this.showCreate.set(true);
  }

  closeCreate() {
    this.showCreate.set(false);
    this.editTarget.set(null);
    this.reviewForm.reset({ reviewYear: this.currentYear, reviewPeriod: 'Q1' });
    this.createError.set('');
  }

  saveReview() {
    const val = this.reviewForm.value;
    this.saving.set(true);
    const req = { reviewYear: val.reviewYear!, reviewPeriod: val.reviewPeriod!, selfAssessment: val.selfAssessment || undefined };
    const obs = this.editTarget() ? this.svc.updateReview(this.editTarget()!.id, req) : this.svc.createReview(req);

    obs.subscribe({
      next: res => {
        if (this.editTarget()) {
          this.myReviews.update(list => list.map(r => r.id === res.data.id ? res.data : r));
        } else {
          this.myReviews.update(list => [res.data, ...list]);
        }
        this.closeCreate();
        this.saving.set(false);
      },
      error: err => {
        this.createError.set(err.error?.message || 'Failed to save review');
        this.saving.set(false);
      }
    });
  }

  submitReview(id: number) {
    this.svc.submitReview(id).subscribe(res => {
      this.myReviews.update(list => list.map(r => r.id === res.data.id ? res.data : r));
    });
  }

  openFeedback(r: PerformanceReviewResponse) {
    this.feedbackTarget.set(r);
    this.feedbackText   = r.managerFeedback || '';
    this.feedbackRating = r.rating || 0;
  }

  submitFeedback() {
    const target = this.feedbackTarget()!;
    this.svc.addFeedback(target.id, { feedback: this.feedbackText, rating: this.feedbackRating }).subscribe({
      next: res => {
        this.teamReviews.update(list => list.map(r => r.id === res.data.id ? res.data : r));
        this.pendingReviews.update(list => list.filter(r => r.id !== res.data.id));
        this.feedbackTarget.set(null);
      }
    });
  }
}
