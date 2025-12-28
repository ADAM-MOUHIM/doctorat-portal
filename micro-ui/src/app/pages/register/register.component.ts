import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Inscription</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <div class="form-row">
          <div>
            <label>Username</label>
            <input formControlName="username" />
          </div>
          <div>
            <label>Email</label>
            <input formControlName="email" />
          </div>
        </div>
        <div class="form-row">
          <div>
            <label>Mot de passe</label>
            <input type="password" formControlName="password" />
          </div>
          <div>
            <label>Role</label>
            <select formControlName="primaryRole">
              <option value="DOCTORANT">Doctorant</option>
              <option value="DIRECTEUR">Encadrant</option>
            </select>
          </div>
        </div>
        <label>Telephone</label>
        <input formControlName="phone" />
        <div class="flex" style="margin-top:12px;">
          <button type="submit" [disabled]="form.invalid || loading">Creer le compte</button>
          <span *ngIf="message" class="badge">{{message}}</span>
        </div>
      </form>
    </div>
  `
})
export class RegisterComponent {
  loading = false;
  message = '';
  form = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    phone: [''],
    primaryRole: ['DOCTORANT', Validators.required]
  });

  constructor(private fb: FormBuilder, private api: ApiService, private router: Router) {}

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.message = '';
    this.api.register(this.form.value as any).subscribe({
      next: res => {
        if (res.primaryRole === 'SUPERUSER' || res.status === 'ACTIVE') {
          this.message = `Compte ${res.username} cree (status ${res.status})`;
          setTimeout(() => this.router.navigateByUrl('/login'), 500);
        } else {
          this.message = `Demande envoyee. Verifiez votre email avant validation par l'admin.`;
        }
        this.loading = false;
      },
      error: err => {
        this.message = err.error?.message || 'Erreur inscription';
        this.loading = false;
      }
    });
  }
}
