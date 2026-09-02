export interface ApiJobSeekerProfile {
  memberId: number;
  name: string;
  birthDate: string;
  gender: string;
  email: string;
  phone: string;
  profileId?: number | null;
  profileImageUrl?: string | null;
  residenceRegion?: string | null;
  desiredJob?: string | null;
  desiredRegion?: string | null;
  employmentType?: string | null;
  careerType?: string | null;
  careerYears?: number | null;
  minSalary?: number | null;
  remotePreferred?: boolean | null;
  flexiblePreferred?: boolean | null;
  wheelchairRequired?: boolean | null;
  accessibleRestroomRequired?: boolean | null;
  disabledParkingRequired?: boolean | null;
  assistiveDeviceRequired?: boolean | null;
  hybridPreferred?: boolean | null;
  onsitePreferred?: boolean | null;
  contactTimeStart?: string | null;
  contactTimeEnd?: string | null;
  contactMethod?: string | null;
  introduction?: string | null;
  profilePublic?: boolean | null;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string; error?: string };
    return body.detail || body.message || body.error || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

export async function getProfile(memberId: string | number) {
  const response = await fetch(`/api/profiles/${memberId}`);
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiJobSeekerProfile>;
}

export async function saveProfile(memberId: string | number, profile: ApiJobSeekerProfile) {
  const response = await fetch(`/api/profiles/${memberId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(profile),
  });
  if (!response.ok) throw new Error(await responseError(response));
}
