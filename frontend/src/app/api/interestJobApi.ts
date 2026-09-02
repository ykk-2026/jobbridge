import type { Job } from '@/app/types';

export interface InterestJob {
  id: number;
  companyName: string;
  title: string;
  accessibilityInfo: string | null;
  wheelchairAccessible: boolean;
  accessibleRestroom: boolean;
  disabledParking: boolean;
  remoteAvailable: boolean;
  flexibleWorkAvailable: boolean;
  assistiveDeviceSupport: boolean;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string; error?: string };
    return body.detail || body.message || body.error || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

const salaryRange = (salary: string) => {
  const values = salary.match(/\d[\d,]*/g)?.map(value => Number(value.replaceAll(',', ''))) || [];
  return { salaryMin: values[0] || null, salaryMax: values[1] || null };
};

const accessibilityLabels: Record<keyof Job['accessibility'], string> = {
  elevator: '엘리베이터',
  parking: '장애인 주차시설',
  wheelchair: '휠체어 접근',
  restroom: '장애인 화장실',
  guideDog: '안내견 동반',
  hearingLoop: '청각 보조장치',
};

const accessibilityText = (job: Job) => (Object.entries(job.accessibility) as [keyof Job['accessibility'], boolean][])
  .filter(([, enabled]) => enabled)
  .map(([name]) => accessibilityLabels[name])
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
      wheelchairAccessible: job.accessibility.wheelchair,
      accessibleRestroom: job.accessibility.restroom,
      disabledParking: job.accessibility.parking,
      remoteAvailable: job.isRemote,
      flexibleWorkAvailable: job.workType.includes('유연')
        || job.benefits.some(benefit => benefit.includes('유연근무')),
      assistiveDeviceSupport: job.accessibility.hearingLoop,
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
