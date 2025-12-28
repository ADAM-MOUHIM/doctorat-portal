import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('access');
  if (token && role === 'SUPERUSER') {
    return true;
  }
  router.navigateByUrl('/dashboard');
  return false;
};
