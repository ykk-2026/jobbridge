export interface JobSummary {
  id: string;
  company: string;
  title: string;
  location: string;
  score: number;
  applicants: number;
  status: string;
  posted: string;
}

export async function getJobs(limit = 20): Promise<JobSummary[]> {
  const response = await fetch(`/api/jobs?limit=${limit}`);
  if (!response.ok) throw new Error(`채용공고를 불러오지 못했습니다. (${response.status})`);
  return response.json() as Promise<JobSummary[]>;
}
