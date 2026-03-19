import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { EmployeeService, AdminService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserResponse, CreateEmployeeRequest, Role } from '../../../core/models';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, DatePipe],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.scss'
})
export class EmployeeListComponent implements OnInit {
  auth = inject(AuthService);
  private empSvc   = inject(EmployeeService);
  private adminSvc = inject(AdminService);
  private fb       = inject(FormBuilder);

  employees  = signal<UserResponse[]>([]);
  managers   = signal<UserResponse[]>([]);
  loading    = signal(true);
  showCreate = signal(false);
  creating   = signal(false);
  createError = signal('');
  assignTarget      = signal<UserResponse | null>(null);
  selectedManagerId: number | null = null;

  searchTerm   = '';
  roleFilter   = '';
  statusFilter = '';

  filteredEmployees = computed(() =>
    this.employees().filter(e => {
      const term = this.searchTerm.toLowerCase();
      const matchSearch = !term ||
        `${e.firstName} ${e.lastName}`.toLowerCase().includes(term) ||
        e.email.toLowerCase().includes(term) ||
        (e.department || '').toLowerCase().includes(term);
      return matchSearch &&
        (!this.roleFilter   || e.role   === this.roleFilter) &&
        (!this.statusFilter || e.status === this.statusFilter);
    })
  );

  createForm = this.fb.group({
    email:         [''],
    password:      [''],
    firstName:     [''],
    lastName:      [''],
    phone:         [''],
    department:    [''],
    designation:   [''],
    employeeCode:  [''],
    role:          ['EMPLOYEE' as Role],
    managerId:     [null as number | null],
    dateOfJoining: ['']
  });

  ngOnInit() {
    this.loadEmployees();
    this.empSvc.getManagers().subscribe(res => this.managers.set(res.data || []));
  }

  loadEmployees() {
    const obs = this.auth.isAdmin ? this.empSvc.getAllEmployees() : this.empSvc.getMyTeam();
    obs.subscribe({
      next:  res => { this.employees.set(res.data || []); this.loading.set(false); },
      error: ()  => this.loading.set(false)
    });
  }

  createEmployee() {
    this.creating.set(true);
    this.createError.set('');
    const val = this.createForm.value;
    const req: CreateEmployeeRequest = {
      email:         val.email!,
      password:      val.password!,
      firstName:     val.firstName!,
      lastName:      val.lastName!,
      phone:         val.phone         || undefined,
      department:    val.department    || undefined,
      designation:   val.designation   || undefined,
      employeeCode:  val.employeeCode  || undefined,
      role:          val.role as Role,
      managerId:     val.managerId     || undefined,
      dateOfJoining: val.dateOfJoining || undefined
    };
    this.adminSvc.createEmployee(req).subscribe({
      next: res => {
        this.employees.update(list => [...list, res.data]);
        this.showCreate.set(false);
        this.createForm.reset({ role: 'EMPLOYEE' });
        this.creating.set(false);
      },
      error: err => {
        this.createError.set(err.error?.message || 'Creation failed');
        this.creating.set(false);
      }
    });
  }

  openAssign(emp: UserResponse) {
    this.assignTarget.set(emp);
    this.selectedManagerId = emp.manager?.id || null;
  }

  assignManager() {
    const target = this.assignTarget();
    if (!target || !this.selectedManagerId) return;
    this.adminSvc.assignManager(target.id, this.selectedManagerId).subscribe({
      next: res => {
        this.employees.update(list => list.map(e => e.id === res.data.id ? res.data : e));
        this.assignTarget.set(null);
      }
    });
  }

  toggleStatus(id: number) {
    this.adminSvc.toggleStatus(id).subscribe(res => {
      this.employees.update(list => list.map(e => e.id === res.data.id ? res.data : e));
    });
  }
}
