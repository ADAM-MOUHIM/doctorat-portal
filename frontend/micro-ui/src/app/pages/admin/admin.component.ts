import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AccountResponse, CreateAdminRequest, Department, Laboratory } from '../../core/models';

type AdminSection = 'overview' | 'accounts' | 'pending' | 'admins' | 'structures' | 'create';

@Component({
  standalone: true,
  selector: 'app-admin',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="panel" style="padding:16px;">
      <div class="flex" style="justify-content:space-between; align-items:center;">
        <h2>Console Admin</h2>
        <div class="badge">SUPERUSER</div>
      </div>
      <div class="flex" style="gap:8px; flex-wrap:wrap;">
        <button (click)="setSection('overview')" [disabled]="activeSection==='overview'">Vue globale</button>
        <button (click)="setSection('accounts')" [disabled]="activeSection==='accounts'">Tous les comptes</button>
        <button (click)="setSection('pending')" [disabled]="activeSection==='pending'">Comptes en attente</button>
        <button (click)="setSection('admins')" [disabled]="activeSection==='admins'">Admins</button>
        <button (click)="setSection('structures')" [disabled]="activeSection==='structures'">Départements & Labos</button>
        <button (click)="setSection('create')" [disabled]="activeSection==='create'">Créer un admin</button>
      </div>
    </div>

    <section *ngIf="activeSection==='overview'" class="panel">
      <h3>Vue globale</h3>
      <div class="stat-grid">
        <div class="stat">
          <div class="section-title">Comptes total</div>
          <strong>{{ allAccounts.length }}</strong>
        </div>
        <div class="stat">
          <div class="section-title">En attente</div>
          <strong>{{ pending.length }}</strong>
        </div>
        <div class="stat">
          <div class="section-title">Admins</div>
          <strong>{{ admins.length }}</strong>
        </div>
      </div>
      <div style="margin-top:12px;">
        <button (click)="reloadAll()">Rafraîchir tout</button>
      </div>
    </section>

    <section *ngIf="activeSection==='create'" class="panel">
      <h3>Créer un compte admin</h3>
      <form [formGroup]="createForm" (ngSubmit)="createAdmin()" class="form-row">
        <div>
          <label>Username</label>
          <input formControlName="username" />
        </div>
        <div>
          <label>Email</label>
          <input formControlName="email" />
        </div>
        <div>
          <label>Mot de passe</label>
          <input type="password" formControlName="password" />
        </div>
        <div>
          <label>Téléphone</label>
          <input formControlName="phone" />
        </div>
      </form>
      <div class="flex" style="margin-top:12px;">
        <button (click)="createAdmin()" [disabled]="createForm.invalid || creatingAdmin">Créer</button>
        <span *ngIf="createMessage" class="badge">{{createMessage}}</span>
      </div>
    </section>

    <section *ngIf="activeSection==='accounts'" class="panel">
      <div class="flex" style="justify-content:space-between; align-items:center;">
        <h3>Tous les comptes</h3>
        <button (click)="loadAllAccounts()">Rafraîchir</button>
      </div>
      <div class="table">
        <div class="table-head">
          <div>Username</div><div>Email</div><div>Rôle</div><div>Status</div><div>Email vérifié</div>
        </div>
        <div *ngFor="let acc of allAccounts" class="table-row">
          <div>{{acc.username}}</div>
          <div>{{acc.email}}</div>
          <div>{{acc.primaryRole}}</div>
          <div><span class="badge">{{acc.status}}</span></div>
          <div><span class="badge" [style.color]="acc.emailVerified ? '#d3b869' : '#f97316'">{{ acc.emailVerified ? 'Oui' : 'Non' }}</span></div>
        </div>
      </div>
    </section>

    <section *ngIf="activeSection==='pending'" class="panel">
      <div class="flex" style="justify-content:space-between; align-items:center;">
        <h3>Comptes en attente</h3>
        <button (click)="loadPending()">Rafraîchir</button>
      </div>
      <div *ngIf="pending.length===0">Aucune demande.</div>
      <div *ngFor="let acc of pending" class="card" style="padding:12px;">
        <div class="flex" style="justify-content:space-between; align-items:center;">
          <div><strong>{{acc.username}}</strong> ({{acc.email}}) - role: {{acc.primaryRole}}</div>
          <div class="badge" [style.background]="acc.emailVerified ? '#1a1a1a' : '#211'">{{ acc.emailVerified ? 'Email vérifié' : 'Email non vérifié' }}</div>
        </div>
        <div class="flex" style="gap:8px; margin-top:8px;">
          <button (click)="approve(acc.id)" [disabled]="!acc.emailVerified">Approuver</button>
          <button (click)="reject(acc.id)" style="background:#ef4444; color:white;">Rejeter</button>
        </div>
      </div>
    </section>

    <section *ngIf="activeSection==='admins'" class="panel">
      <div class="flex" style="justify-content:space-between; align-items:center;">
        <h3>Admins existants</h3>
        <button (click)="loadAdmins()">Rafraîchir</button>
      </div>
      <div *ngIf="admins.length===0">Aucun admin.</div>
      <div *ngFor="let admin of admins" class="card" style="padding:12px;">
        <div class="flex" style="justify-content:space-between; align-items:center;">
          <div><strong>{{admin.username}}</strong> ({{admin.email}})</div>
          <div class="badge">{{admin.status}}</div>
        </div>
        <div class="flex" style="gap:8px; margin-top:8px;">
          <button *ngIf="admin.status!=='ACTIVE'" (click)="activate(admin.id)">Activer</button>
          <button *ngIf="admin.status==='ACTIVE'" (click)="suspend(admin.id)" style="background:#ef4444; color:white;">Suspendre</button>
        </div>
      </div>
    </section>

    <section *ngIf="activeSection==='structures'" class="panel">
      <div class="flex" style="justify-content:space-between; align-items:center;">
        <h3>Départements</h3>
        <button (click)="loadStructures()">Rafraîchir</button>
      </div>
      <form [formGroup]="departmentForm" (ngSubmit)="createDepartment()" class="form-row" style="margin-top:8px; gap:8px; flex-wrap:wrap;">
        <input placeholder="Nom" formControlName="name" />
        <input placeholder="Description" formControlName="description" />
        <button type="submit" [disabled]="departmentForm.invalid || creatingDepartment">Ajouter</button>
      </form>
      <div class="table" style="margin-top:8px;">
        <div class="table-head">
          <div>Nom</div><div>Description</div>
        </div>
        <div *ngFor="let dep of departments" class="table-row">
          <div>{{dep.name}}</div>
          <div>{{dep.description}}</div>
        </div>
      </div>

      <div class="flex" style="justify-content:space-between; align-items:center; margin-top:16px;">
        <h3>Laboratoires</h3>
      </div>
      <form [formGroup]="laboratoryForm" (ngSubmit)="createLaboratory()" class="form-row" style="margin-top:8px; gap:8px; flex-wrap:wrap;">
        <input placeholder="Nom" formControlName="name" />
        <input placeholder="Description" formControlName="description" />
        <button type="submit" [disabled]="laboratoryForm.invalid || creatingLaboratory">Ajouter</button>
      </form>
      <div class="table" style="margin-top:8px;">
        <div class="table-head">
          <div>Nom</div><div>Description</div>
        </div>
        <div *ngFor="let lab of laboratories" class="table-row">
          <div>{{lab.name}}</div>
          <div>{{lab.description}}</div>
        </div>
      </div>
      <div *ngIf="structureMessage" class="badge" style="margin-top:8px;">{{structureMessage}}</div>
    </section>
  `
})
export class AdminComponent implements OnInit {
  activeSection: AdminSection = 'overview';
  pending: AccountResponse[] = [];
  admins: AccountResponse[] = [];
  allAccounts: AccountResponse[] = [];
  departments: Department[] = [];
  laboratories: Laboratory[] = [];
  pendingMessage = '';
  adminMessage = '';
  createMessage = '';
  structureMessage = '';
  creatingAdmin = false;
  creatingDepartment = false;
  creatingLaboratory = false;

  createForm = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    phone: ['']
  });

  departmentForm = this.fb.group({
    name: ['', Validators.required],
    description: ['']
  });

  laboratoryForm = this.fb.group({
    name: ['', Validators.required],
    description: ['']
  });

  constructor(private api: ApiService, private fb: FormBuilder) {}

  ngOnInit(): void {
    if (!this.isSuperuser()) {
      return;
    }
    this.reloadAll();
  }

  private isSuperuser(): boolean {
    return localStorage.getItem('role') === 'SUPERUSER';
  }

  setSection(sec: AdminSection) { this.activeSection = sec; }

  reloadAll() {
    this.loadPending();
    this.loadAdmins();
    this.loadAllAccounts();
    this.loadStructures();
  }

  loadPending() {
    this.pendingMessage = '';
    this.api.listPendingAccounts().subscribe({
      next: res => { this.pending = res; },
      error: err => { this.pendingMessage = err.error?.message || 'Erreur chargement des demandes'; }
    });
  }

  loadAdmins() {
    this.adminMessage = '';
    this.api.listAdmins().subscribe({
      next: res => { this.admins = res; },
      error: err => { this.adminMessage = err.error?.message || 'Erreur chargement admins'; }
    });
  }

  loadAllAccounts() {
    this.api.listAllAccounts().subscribe({
      next: res => { this.allAccounts = res; },
      error: () => {}
    });
  }

  loadStructures() {
    this.api.listDepartments().subscribe({ next: res => this.departments = res });
    this.api.listLaboratories().subscribe({ next: res => this.laboratories = res });
  }

  createAdmin() {
    if (this.createForm.invalid) return;
    this.creatingAdmin = true;
    this.createMessage = '';
    const payload: CreateAdminRequest = {
      username: this.createForm.value.username || '',
      email: this.createForm.value.email || '',
      password: this.createForm.value.password || '',
      phone: this.createForm.value.phone || undefined
    };
    this.api.createAdmin(payload).subscribe({
      next: admin => {
        this.createMessage = `Admin ${admin.username} cree (${admin.status})`;
        this.createForm.reset({ username: '', email: '', password: '', phone: '' });
        this.creatingAdmin = false;
        this.loadAdmins();
        this.loadAllAccounts();
      },
      error: err => {
        this.createMessage = err.error?.message || 'Erreur creation admin';
        this.creatingAdmin = false;
      }
    });
  }

  approve(id: string) {
    this.api.approveAccount(id).subscribe({
      next: () => { this.pendingMessage = 'Compte approuve'; this.loadPending(); this.loadAllAccounts(); },
      error: err => { this.pendingMessage = err.error?.message || 'Erreur approbation'; }
    });
  }

  reject(id: string) {
    this.api.rejectAccount(id).subscribe({
      next: () => { this.pendingMessage = 'Compte rejete'; this.loadPending(); this.loadAllAccounts(); },
      error: err => { this.pendingMessage = err.error?.message || 'Erreur rejet'; }
    });
  }

  activate(id: string) {
    this.api.activateAdmin(id).subscribe({
      next: admin => { this.adminMessage = `Admin ${admin.username} active`; this.loadAdmins(); this.loadAllAccounts(); },
      error: err => { this.adminMessage = err.error?.message || 'Erreur activation'; }
    });
  }

  suspend(id: string) {
    this.api.suspendAdmin(id).subscribe({
      next: admin => { this.adminMessage = `Admin ${admin.username} suspendu`; this.loadAdmins(); this.loadAllAccounts(); },
      error: err => { this.adminMessage = err.error?.message || 'Erreur suspension'; }
    });
  }

  createDepartment() {
    if (this.departmentForm.invalid) return;
    this.creatingDepartment = true;
    this.structureMessage = '';
    this.api.createDepartment(this.departmentForm.value as any).subscribe({
      next: dep => {
        this.structureMessage = `Departement ${dep.name} cree`;
        this.departmentForm.reset({ name: '', description: '' });
        this.creatingDepartment = false;
        this.loadStructures();
      },
      error: err => {
        this.structureMessage = err.error?.message || 'Erreur creation departement';
        this.creatingDepartment = false;
      }
    });
  }

  createLaboratory() {
    if (this.laboratoryForm.invalid) return;
    this.creatingLaboratory = true;
    this.structureMessage = '';
    this.api.createLaboratory(this.laboratoryForm.value as any).subscribe({
      next: lab => {
        this.structureMessage = `Laboratoire ${lab.name} cree`;
        this.laboratoryForm.reset({ name: '', description: '' });
        this.creatingLaboratory = false;
        this.loadStructures();
      },
      error: err => {
        this.structureMessage = err.error?.message || 'Erreur creation laboratoire';
        this.creatingLaboratory = false;
      }
    });
  }
}
