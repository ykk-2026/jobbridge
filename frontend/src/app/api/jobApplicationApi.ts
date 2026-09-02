import type { Job } from '@/app/types';

export interface JobApplicationForm {
  applicantName: string;
  phone: string;
  email: string;
  employmentType: string;
}

export interface JobApplicationRecord extends JobApplicationForm {
  id: number;
  jobId: string;
  companyName: string;
  jobTitle: string;
  status: string;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string; error?: string };
    return body.detail || body.message || body.error || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

export async function getJobApplications(): Promise<JobApplicationRecord[]> {
  const response = await fetch('/api/job-applications');
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<JobApplicationRecord[]>;
}

export async function saveJobApplication(job: Job, form: JobApplicationForm) {
  const response = await fetch('/api/job-applications', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ jobId: job.id, companyName: job.company, jobTitle: job.title, ...form }),
  });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<JobApplicationRecord>;
}
