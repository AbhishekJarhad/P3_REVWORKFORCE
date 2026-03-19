import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { EmployeeService } from '../../../core/services/api.service';
import { UserResponse } from '../../../core/models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private svc = inject(EmployeeService);
  private fb  = inject(FormBuilder);

  user   = signal<UserResponse | null>(null);
  loading = signal(true);
  saving  = signal(false);
  saved   = signal(false);
  error   = signal('');

  form = this.fb.group({
    firstName: [''],
    lastName:  [''],
    phone:     [''],
    address:   ['']
  });

  initials = () => {
    const u = this.user();
    return u ? (u.firstName[0] + u.lastName[0]).toUpperCase() : 'U';
  };

  ngOnInit() {
    this.svc.getMyProfile().subscribe({
      next: res => {
        this.user.set(res.data);
        this.form.patchValue({
          firstName: res.data.firstName,
          lastName:  res.data.lastName,
          phone:     res.data.phone    || '',
          address:   res.data.address  || ''
        });
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  save() {
    this.saving.set(true);
    this.saved.set(false);
    this.error.set('');
    const val = this.form.value;

    this.svc.updateProfile({
      firstName: val.firstName!,
      lastName:  val.lastName!,
      phone:     val.phone!,
      address:   val.address!
    }).subscribe({
      next: res => {
        this.user.set(res.data);
        this.saved.set(true);
        this.saving.set(false);
        setTimeout(() => this.saved.set(false), 3000);
      },
      error: err => {
        this.error.set(err.error?.message || 'Update failed');
        this.saving.set(false);
      }
    });
  }
}
