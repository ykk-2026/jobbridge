import type { RegisterFormData } from '@/app/types';

export interface LoginMember {
  id: number;
  loginId: string;
  name: string;
  role: string;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string; error?: string };
    return body.detail || body.message || body.error || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

export async function checkLoginIdAvailability(loginId: string) {
  const response = await fetch(`/api/members/check-login-id?loginId=${encodeURIComponent(loginId)}`);
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<{ available: boolean; message: string }>;
}

export async function loginMember(loginId: string, password: string) {
  const response = await fetch('/api/members/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ loginId: loginId.trim(), password }),
  });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<LoginMember>;
}

export async function getCurrentMember() {
  const response = await fetch('/api/members/me');
  if (response.status === 204) return null;
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<LoginMember>;
}

export async function logoutMember() {
  await fetch('/api/members/logout', { method: 'POST' });
}

export async function registerMember(form: RegisterFormData, passwordConfirm: string) {
  const response = await fetch('/api/members', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      loginId: form.loginId.trim().toLowerCase(),
      password: form.password,
      passwordConfirm,
      name: form.name.trim(),
      birthDate: form.birthDate.trim(),
      gender: form.gender || 'OTHER',
      email: form.email.trim(),
      phone: form.phone,
      role: 'JOB_SEEKER',
      desiredJob: form.preferredRole.trim() || null,
    }),
  });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<{ memberId: number; loginId: string; name: string; message: string }>;
}
