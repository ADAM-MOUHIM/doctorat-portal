import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="shell">
      <aside class="sidebar">
        <div class="brand">Service Auth</div>
        <div class="profile-box">
          <div><strong>Session</strong></div>
          <div>Role: {{ roleLabel() }}</div>
          <div>Etat: {{ isLogged() ? 'Connecté' : 'Invité' }}</div>
          <div class="chip" *ngIf="isSuperuser()">SUPERUSER</div>
        </div>
        <nav>
          <a *ngIf="!isLogged()" routerLink="/login">Login</a>
          <a *ngIf="!isLogged()" routerLink="/register">Register</a>
          <a *ngIf="isLogged()" routerLink="/dashboard">Dashboard</a>
          <a *ngIf="isSuperuser()" routerLink="/admin">Admin</a>
        </nav>
        <button *ngIf="isLogged()" (click)="logout()">Logout</button>
      </aside>

      <main class="content">
        <div class="topbar">
          <div class="title">Portail comptes & profils</div>
          <div class="stat-grid" style="max-width:420px;">
            <div class="stat"><div class="section-title">Compte</div><strong>{{ isLogged() ? 'Actif' : 'Non connecté' }}</strong></div>
            <div class="stat"><div class="section-title">Rôle</div><strong>{{ roleLabel() }}</strong></div>
          </div>
        </div>

        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class AppComponent {
  constructor(private router: Router) {}

  isSuperuser(): boolean {
    return localStorage.getItem('role') === 'SUPERUSER';
  }
  isLogged(): boolean {
    return !!localStorage.getItem('access');
  }
  roleLabel(): string {
    return localStorage.getItem('role') || 'Invité';
  }
  logout() {
    localStorage.removeItem('access');
    localStorage.removeItem('refresh');
    localStorage.removeItem('role');
    this.router.navigateByUrl('/login');
  }
}
