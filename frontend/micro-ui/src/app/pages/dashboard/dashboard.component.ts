import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import {
  AccountResponse,
  CommonProfileInfoDTO,
  Department,
  DoctorantProfileResponse,
  EncadrantProfileResponse,
  Laboratory,
  UpdateDoctorantProfileRequest,
  UpdateEncadrantProfileRequest
} from '../../core/models';

type Tab = 'session' | 'account' | 'profile';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="panel">
      <div class="flex" style="gap:8px; flex-wrap:wrap;">
        <button (click)="setTab('session')" [disabled]="activeTab==='session'">Session</button>
        <button (click)="setTab('account')" [disabled]="activeTab==='account'">Mon compte</button>
        <button (click)="setTab('profile')" [disabled]="activeTab==='profile'">Profil</button>
        <a *ngIf="isSuperuser()" routerLink="/admin" class="badge" style="background:#d3b869; color:#111;">Admin</a>
      </div>
    </div>

    <div class="panel" *ngIf="activeTab==='session'">
      <h2>Session</h2>
      <div class="flex" style="flex-wrap:wrap; gap:8px;">
        <button (click)="refreshAccess()">Refresh token</button>
        <button (click)="logout()">Logout</button>
        <span *ngIf="sessionMessage" class="badge">{{sessionMessage}}</span>
      </div>
    </div>

    <div class="panel" *ngIf="activeTab==='account' && me">
      <h2>Mon compte</h2>
      <p><strong>Username:</strong> {{me.username}}</p>
      <p><strong>Email:</strong> {{me.email}}</p>
      <p><strong>Role:</strong> {{me.primaryRole}}</p>
      <p><strong>Status:</strong> {{me.status}}</p>
      <p><strong>Profil complet ?</strong> {{me.profileCompleted}}</p>
    </div>

    <div class="panel" *ngIf="activeTab==='profile' && me?.primaryRole === 'DOCTORANT'">
      <h3>Profil doctorant</h3>

      <div *ngIf="doctorantProfile && !editingDoctorant" class="profile-block">
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Prenom:</strong> {{doctorantProfile.info?.firstName || 'N/A'}}</p>
          <p><strong>Nom:</strong> {{doctorantProfile.info?.lastName || 'N/A'}}</p>
        </div>
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Date de naissance:</strong> {{doctorantProfile.info?.birthDate || 'N/A'}}</p>
          <p><strong>Adresse:</strong> {{doctorantProfile.info?.address || 'N/A'}}</p>
        </div>
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Diplome:</strong> {{doctorantProfile.diploma || 'N/A'}}</p>
          <p><strong>Annee graduation:</strong> {{doctorantProfile.graduationYear || 'N/A'}}</p>
        </div>
        <p><strong>Universite:</strong> {{doctorantProfile.university || 'N/A'}}</p>
        <div class="flex" style="gap:8px;">
          <button type="button" (click)="startDoctorantEdit()">Modifier le profil</button>
          <span *ngIf="profileMessage" class="badge">{{profileMessage}}</span>
        </div>
      </div>

      <form *ngIf="!doctorantProfile || editingDoctorant" [formGroup]="docForm" (ngSubmit)="saveDoctorant()">
        <div class="form-row">
          <div><label>Prenom</label><input formControlName="firstName" /></div>
          <div><label>Nom</label><input formControlName="lastName" /></div>
        </div>
        <div class="form-row">
          <div><label>Date de naissance</label><input type="date" formControlName="birthDate" /></div>
          <div><label>Adresse</label><input formControlName="address" /></div>
        </div>
        <div class="form-row">
          <div><label>Annee graduation</label><input type="number" formControlName="graduationYear" /></div>
          <div><label>Diplome</label><input formControlName="diploma" /></div>
        </div>
        <label>Universite</label><input formControlName="university" />
        <div class="flex" style="margin-top:12px; gap:8px;">
          <button type="submit" [disabled]="docForm.invalid">{{ doctorantProfile ? 'Mettre a jour' : 'Enregistrer profil' }}</button>
          <button type="button" *ngIf="doctorantProfile" (click)="cancelDoctorantEdit()">Annuler</button>
          <span *ngIf="profileMessage && (!doctorantProfile || editingDoctorant)" class="badge">{{profileMessage}}</span>
        </div>
      </form>
    </div>

    <div class="panel" *ngIf="activeTab==='profile' && me?.primaryRole === 'DIRECTEUR'">
      <h3>Profil encadrant</h3>

      <div *ngIf="encadrantProfile && !editingEncadrant" class="profile-block">
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Prenom:</strong> {{encadrantProfile.info?.firstName || 'N/A'}}</p>
          <p><strong>Nom:</strong> {{encadrantProfile.info?.lastName || 'N/A'}}</p>
        </div>
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Date de naissance:</strong> {{encadrantProfile.info?.birthDate || 'N/A'}}</p>
          <p><strong>Adresse:</strong> {{encadrantProfile.info?.address || 'N/A'}}</p>
        </div>
        <div class="form-row" style="flex-wrap:wrap;">
          <p><strong>Grade:</strong> {{encadrantProfile.grade || 'N/A'}}</p>
          <p><strong>Departement:</strong> {{encadrantProfile.departmentId || 'N/A'}}</p>
          <p><strong>Laboratoire:</strong> {{encadrantProfile.laboratoryId || 'N/A'}}</p>
        </div>
        <div class="flex" style="gap:8px;">
          <button type="button" (click)="startEncadrantEdit()">Modifier le profil</button>
          <span *ngIf="profileMessage" class="badge">{{profileMessage}}</span>
        </div>
      </div>

      <form *ngIf="!encadrantProfile || editingEncadrant" [formGroup]="encForm" (ngSubmit)="saveEncadrant()">
        <div class="form-row">
          <div><label>Prenom</label><input formControlName="firstName" /></div>
          <div><label>Nom</label><input formControlName="lastName" /></div>
        </div>
        <div class="form-row">
          <div><label>Date de naissance</label><input type="date" formControlName="birthDate" /></div>
          <div><label>Adresse</label><input formControlName="address" /></div>
        </div>
        <label>Grade</label><input formControlName="grade" />
        <label>Departement</label>
        <select formControlName="departmentId">
          <option value="">-- Choisir --</option>
          <option *ngFor="let dep of departments" [value]="dep.id">{{dep.name}}</option>
        </select>
        <label>Laboratoire</label>
        <select formControlName="laboratoryId">
          <option value="">-- Choisir --</option>
          <option *ngFor="let lab of laboratories" [value]="lab.id">{{lab.name}}</option>
        </select>
        <div class="flex" style="margin-top:12px; gap:8px;">
          <button type="submit" [disabled]="encForm.invalid">{{ encadrantProfile ? 'Mettre a jour' : 'Enregistrer profil' }}</button>
          <button type="button" *ngIf="encadrantProfile" (click)="cancelEncadrantEdit()">Annuler</button>
          <span *ngIf="profileMessage && (!encadrantProfile || editingEncadrant)" class="badge">{{profileMessage}}</span>
        </div>
      </form>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  activeTab: Tab = 'session';
  me: AccountResponse | null = null;
  doctorantProfile: DoctorantProfileResponse | null = null;
  encadrantProfile: EncadrantProfileResponse | null = null;
  departments: Department[] = [];
  laboratories: Laboratory[] = [];
  editingDoctorant = false;
  editingEncadrant = false;
  sessionMessage = '';
  profileMessage = '';

  docForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    birthDate: [''],
    address: [''],
    diploma: [''],
    graduationYear: [null as number | null],
    university: ['']
  });

  encForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    birthDate: [''],
    address: [''],
    grade: [''],
    departmentId: [''],
    laboratoryId: ['']
  });

  constructor(private fb: FormBuilder, private api: ApiService) {}

  ngOnInit() {
    this.loadMe();
  }

  setTab(tab: Tab) { this.activeTab = tab; }

  isSuperuser(): boolean {
    return localStorage.getItem('role') === 'SUPERUSER';
  }

  loadMe() {
    this.api.me().subscribe({
      next: res => {
        this.me = res;
        localStorage.setItem('role', res.primaryRole);
        this.loadProfileForRole();
      },
      error: () => {
        this.me = null;
        this.doctorantProfile = null;
        this.encadrantProfile = null;
        this.editingDoctorant = false;
        this.editingEncadrant = false;
      }
    });
  }

  private loadProfileForRole() {
    this.profileMessage = '';
    if (!this.me) return;

    if (this.me.primaryRole === 'DOCTORANT') {
      this.api.getDoctorantProfile(this.me.id).subscribe({
        next: profile => {
          this.doctorantProfile = profile;
          this.editingDoctorant = false;
          this.patchDoctorantForm(profile);
        },
        error: () => {
          this.doctorantProfile = null;
          this.editingDoctorant = false;
          this.resetDoctorantForm();
        }
      });
    } else if (this.me.primaryRole === 'DIRECTEUR') {
      this.loadStructures();
      this.api.getEncadrantProfile(this.me.id).subscribe({
        next: profile => {
          this.encadrantProfile = profile;
          this.editingEncadrant = false;
          this.patchEncadrantForm(profile);
        },
        error: () => {
          this.encadrantProfile = null;
          this.editingEncadrant = false;
          this.resetEncadrantForm();
        }
      });
    }
  }

  refreshAccess() {
    const refresh = localStorage.getItem('refresh');
    if (!refresh) return;
    this.api.refresh(refresh).subscribe({
      next: res => {
        localStorage.setItem('access', res.accessToken);
        localStorage.setItem('refresh', res.refreshToken);
        this.sessionMessage = 'Token rafraichi';
      },
      error: err => {
        this.sessionMessage = err.error?.message || 'Refresh echoue';
      }
    });
  }

  logout() {
    const refresh = localStorage.getItem('refresh');
    if (!refresh) return;
    this.api.logout(refresh).subscribe({
      next: () => {
        localStorage.removeItem('access');
        localStorage.removeItem('refresh');
        this.sessionMessage = 'Deconnecte';
        this.me = null;
        this.doctorantProfile = null;
        this.encadrantProfile = null;
        this.editingDoctorant = false;
        this.editingEncadrant = false;
      },
      error: err => {
        this.sessionMessage = err.error?.message || 'Logout echoue';
      }
    });
  }

  startDoctorantEdit() {
    this.profileMessage = '';
    this.editingDoctorant = true;
    if (this.doctorantProfile) {
      this.patchDoctorantForm(this.doctorantProfile);
    }
  }

  cancelDoctorantEdit() {
    this.editingDoctorant = false;
    this.profileMessage = '';
    if (this.doctorantProfile) {
      this.patchDoctorantForm(this.doctorantProfile);
    } else {
      this.resetDoctorantForm();
    }
  }

  saveDoctorant() {
    if (!this.me) return;
    const info = this.buildInfoFromForm(this.docForm.value);
    const payload: UpdateDoctorantProfileRequest = {
      info,
      diploma: this.docForm.value.diploma || undefined,
      graduationYear: this.docForm.value.graduationYear || undefined,
      university: this.docForm.value.university || undefined
    };

    const request$ = this.doctorantProfile
      ? this.api.updateDoctorant(this.me.id, payload)
      : this.api.createDoctorant({ ...payload, accountId: this.me.id });

    request$.subscribe({
      next: res => {
        this.profileMessage = this.doctorantProfile ? 'Profil doctorant mis a jour' : 'Profil doctorant cree';
        this.doctorantProfile = res;
        this.editingDoctorant = false;
        if (this.me) this.me.profileCompleted = true;
        this.patchDoctorantForm(res);
      },
      error: err => {
        this.profileMessage = err.error?.message || 'Erreur profil doctorant';
      }
    });
  }

  startEncadrantEdit() {
    this.profileMessage = '';
    this.editingEncadrant = true;
    if (this.encadrantProfile) {
      this.patchEncadrantForm(this.encadrantProfile);
    }
  }

  cancelEncadrantEdit() {
    this.editingEncadrant = false;
    this.profileMessage = '';
    if (this.encadrantProfile) {
      this.patchEncadrantForm(this.encadrantProfile);
    } else {
      this.resetEncadrantForm();
    }
  }

  saveEncadrant() {
    if (!this.me) return;
    const info = this.buildInfoFromForm(this.encForm.value);
    const payload: UpdateEncadrantProfileRequest = {
      info,
      grade: this.encForm.value.grade || undefined,
      departmentId: this.normalizeUuid(this.encForm.value.departmentId),
      laboratoryId: this.normalizeUuid(this.encForm.value.laboratoryId)
    };

    const request$ = this.encadrantProfile
      ? this.api.updateEncadrant(this.me.id, payload)
      : this.api.createEncadrant({ ...payload, accountId: this.me.id });

    request$.subscribe({
      next: res => {
        this.profileMessage = this.encadrantProfile ? 'Profil encadrant mis a jour' : 'Profil encadrant cree';
        this.encadrantProfile = res;
        this.editingEncadrant = false;
        if (this.me) this.me.profileCompleted = true;
        this.patchEncadrantForm(res);
      },
      error: err => {
        this.profileMessage = err.error?.message || 'Erreur profil encadrant';
      }
    });
  }

  private patchDoctorantForm(profile: DoctorantProfileResponse) {
    this.docForm.patchValue({
      firstName: profile.info?.firstName || '',
      lastName: profile.info?.lastName || '',
      birthDate: profile.info?.birthDate || '',
      address: profile.info?.address || '',
      diploma: profile.diploma || '',
      graduationYear: profile.graduationYear ?? null,
      university: profile.university || ''
    });
  }

  private patchEncadrantForm(profile: EncadrantProfileResponse) {
    this.encForm.patchValue({
      firstName: profile.info?.firstName || '',
      lastName: profile.info?.lastName || '',
      birthDate: profile.info?.birthDate || '',
      address: profile.info?.address || '',
      grade: profile.grade || '',
      departmentId: profile.departmentId || '',
      laboratoryId: profile.laboratoryId || ''
    });
  }

  private resetDoctorantForm() {
    this.docForm.reset({
      firstName: '',
      lastName: '',
      birthDate: '',
      address: '',
      diploma: '',
      graduationYear: null,
      university: ''
    });
  }

  private resetEncadrantForm() {
    this.encForm.reset({
      firstName: '',
      lastName: '',
      birthDate: '',
      address: '',
      grade: '',
      departmentId: '',
      laboratoryId: ''
    });
  }

  private buildInfoFromForm(value: any): CommonProfileInfoDTO {
    const normalizedBirthDate = this.normalizeDate(value.birthDate);
    return {
      firstName: value.firstName || undefined,
      lastName: value.lastName || undefined,
      birthDate: normalizedBirthDate,
      address: value.address || undefined
    };
  }

  private normalizeDate(raw: string | null | undefined): string | undefined {
    if (!raw) return undefined;

    // Accept dd/MM/yyyy or dd-MM-yyyy and convert to yyyy-MM-dd
    const slashMatch = /^(\d{2})[\\/](\d{2})[\\/](\d{4})$/.exec(raw);
    if (slashMatch) {
      const [, dd, mm, yyyy] = slashMatch;
      return `${yyyy}-${mm}-${dd}`;
    }

    const date = new Date(raw);
    if (isNaN(date.getTime())) return undefined;
    return date.toISOString().slice(0, 10);
  }

  private normalizeUuid(value: string | null | undefined): string | undefined {
    if (!value) return undefined;
    const trimmed = value.trim();
    const uuidRegex = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/;
    return uuidRegex.test(trimmed) ? trimmed : undefined;
  }

  private loadStructures() {
    this.api.listDepartments().subscribe({ next: d => this.departments = d });
    this.api.listLaboratories().subscribe({ next: l => this.laboratories = l });
  }
}
