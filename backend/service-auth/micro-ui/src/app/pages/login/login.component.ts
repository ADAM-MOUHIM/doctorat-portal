import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Login</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>Username ou Email</label>
        <input formControlName="usernameOrEmail" />
        <label>Mot de passe</label>
        <input type="password" formControlName="password" />
        <div class="flex" style="margin-top:12px;">
          <button type="submit" [disabled]="form.invalid || loading">Se connecter</button>
          <span *ngIf="error" class="badge">{{error}}</span>
        </div>
      </form>
    </div>
  `
})
export class LoginComponent {
  loading = false;
  error = '';
  form = this.fb.group({
    usernameOrEmail: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(private fb: FormBuilder, private api: ApiService, private router: Router) {}

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.api.login(this.form.value as any).subscribe({
      next: res => {
        localStorage.setItem('access', res.accessToken);
        localStorage.setItem('refresh', res.refreshToken);
        // Récupérer le rôle pour l'UI (nav/admin)
        this.api.me().subscribe({
          next: me => {
            localStorage.setItem('role', me.primaryRole);
            if (me.primaryRole === 'SUPERUSER') {
              this.router.navigateByUrl('/admin');
            } else {
              this.router.navigateByUrl('/dashboard');
            }
          },
          error: () => {
            this.router.navigateByUrl('/dashboard');
          }
        });
      },
      error: err => {
        this.error = err.error?.message || 'Erreur login';
        this.loading = false;
      }
    });
  }
}
