import type { JobSummary } from '@/app/api/jobApi';

export interface AdminMember {
  id: number;
  name: string;
  email: string | null;
  role: string;
  status: string;
}

export interface AdminOverview {
  totalUsers: number;
  totalCompanies: number;
  totalJobs: number;
  totalApplications: number;
  newUsersThisMonth: number;
  newJobsThisMonth: number;
  members: AdminMember[];
  jobs: JobSummary[];
}

export async function getAdminOverview(): Promise<AdminOverview> {
  const response = await fetch('/api/admin/overview');
  if (!response.ok) throw new Error(`관리자 데이터를 불러오지 못했습니다. (${response.status})`);
  return response.json() as Promise<AdminOverview>;
}
