import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {
  AccountResponse,
  CreateDoctorantProfileRequest,
  CreateEncadrantProfileRequest,
  DoctorantProfileResponse,
  EncadrantProfileResponse,
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
  TokenResponse,
  UpdateDoctorantProfileRequest,
  UpdateEncadrantProfileRequest,
  CreateAdminRequest,
  Department,
  Laboratory
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  login(body: LoginRequest) {
    return this.http.post<TokenResponse>(`${this.base}/auth/login`, body);
  }

  register(body: RegisterRequest) {
    return this.http.post<RegisterResponse>(`${this.base}/accounts`, body);
  }

  me() {
    return this.http.get<AccountResponse>(`${this.base}/accounts/me`);
  }

  refresh(refreshToken: string) {
    return this.http.post<TokenResponse>(`${this.base}/auth/refresh`, { refreshToken });
  }

  logout(refreshToken: string) {
    return this.http.post<void>(`${this.base}/auth/logout`, { refreshToken });
  }

  createDoctorant(body: CreateDoctorantProfileRequest) {
    return this.http.post<DoctorantProfileResponse>(`${this.base}/profiles/doctorant`, body);
  }

  createEncadrant(body: CreateEncadrantProfileRequest) {
    return this.http.post<EncadrantProfileResponse>(`${this.base}/profiles/encadrant`, body);
  }

  getDoctorantProfile(accountId: string) {
    return this.http.get<DoctorantProfileResponse>(`${this.base}/profiles/doctorant/${accountId}`);
  }

  getEncadrantProfile(accountId: string) {
    return this.http.get<EncadrantProfileResponse>(`${this.base}/profiles/encadrant/${accountId}`);
  }

  updateDoctorant(accountId: string, body: UpdateDoctorantProfileRequest) {
    return this.http.put<DoctorantProfileResponse>(`${this.base}/profiles/doctorant/${accountId}`, body);
  }

  updateEncadrant(accountId: string, body: UpdateEncadrantProfileRequest) {
    return this.http.put<EncadrantProfileResponse>(`${this.base}/profiles/encadrant/${accountId}`, body);
  }

  // --- Admin (SUPERUSER) ---
  listPendingAccounts() {
    return this.http.get<AccountResponse[]>(`${this.base}/admin/accounts/pending`);
  }

  listAllAccounts() {
    return this.http.get<AccountResponse[]>(`${this.base}/admin/accounts`);
  }

  listAdmins() {
    return this.http.get<AccountResponse[]>(`${this.base}/admin/accounts/admins`);
  }

  listDepartments() {
    return this.http.get<Department[]>(`${this.base}/departments`);
  }

  listLaboratories() {
    return this.http.get<Laboratory[]>(`${this.base}/laboratories`);
  }

  createDepartment(body: { name: string; description?: string }) {
    return this.http.post<Department>(`${this.base}/admin/departments`, body);
  }

  createLaboratory(body: { name: string; description?: string }) {
    return this.http.post<Laboratory>(`${this.base}/admin/laboratories`, body);
  }

  createAdmin(body: CreateAdminRequest) {
    return this.http.post<AccountResponse>(`${this.base}/admin/accounts/admins`, body);
  }

  activateAdmin(id: string) {
    return this.http.post<AccountResponse>(`${this.base}/admin/accounts/${id}/activate`, {});
  }

  suspendAdmin(id: string) {
    return this.http.post<AccountResponse>(`${this.base}/admin/accounts/${id}/suspend`, {});
  }

  approveAccount(id: string) {
    return this.http.post<AccountResponse>(`${this.base}/admin/accounts/${id}/approve`, {});
  }

  rejectAccount(id: string) {
    return this.http.post<AccountResponse>(`${this.base}/admin/accounts/${id}/reject`, {});
  }
}
