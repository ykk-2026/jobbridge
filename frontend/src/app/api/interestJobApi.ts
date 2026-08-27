import type { Job } from '@/app/types';

export interface InterestJob {
  id: number;
  companyName: string;
  title: string;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string };
    return body.detail || body.message || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

const salaryRange = (salary: string) => {
  const values = salary.match(/\d[\d,]*/g)?.map(value => Number(value.replaceAll(',', ''))) || [];
  return { salaryMin: values[0] || null, salaryMax: values[1] || null };
};

const accessibilityText = (job: Job) => Object.entries(job.accessibility)
  .filter(([, enabled]) => enabled)
  .map(([name]) => name)
  .join(', ');

export async function getInterestJobs(): Promise<InterestJob[]> {
  const response = await fetch('/api/interest-jobs');
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<InterestJob[]>;
}

export async function saveInterestJob(job: Job) {
  const response = await fetch('/api/interest-jobs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      companyName: job.company,
      title: job.title,
      jobCategory: job.category,
      employmentType: job.category === 'PartTime' ? 'PART_TIME' : 'FULL_TIME',
      location: job.location,
      ...salaryRange(job.salary),
      experienceLevel: null,
      educationLevel: null,
      description: job.description,
      requirements: job.requirements.join(', '),
      preferredQualifications: job.benefits.join(', '),
      accessibilityInfo: accessibilityText(job),
      deadline: job.deadline,
      status: 'OPEN',
    }),
  });
  if (!response.ok) throw new Error(await responseError(response));
}

export async function deleteInterestJob(companyName: string, title: string) {
  const params = new URLSearchParams({ companyName, title });
  const response = await fetch(`/api/interest-jobs?${params.toString()}`, { method: 'DELETE' });
  if (!response.ok) throw new Error(await responseError(response));
}
