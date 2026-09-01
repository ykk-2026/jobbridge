export const demoMemberId = 1;

export interface ProfileDto {
  memberId: number;
  name: string | null;
  birthDate: string | null;
  gender: string | null;
  email: string | null;
  phone: string | null;
  profileImageUrl: string | null;
  residenceRegion: string | null;
  desiredJob: string | null;
  desiredRegion: string | null;
  employmentType: string | null;
  careerType: string | null;
  careerYears: number | null;
  minSalary: number | null;
  remotePreferred: boolean;
  flexiblePreferred: boolean;
  wheelchairRequired: boolean;
  accessibleRestroomRequired: boolean;
  disabledParkingRequired: boolean;
  assistiveDeviceRequired: boolean;
  hybridPreferred: boolean;
  onsitePreferred: boolean;
  contactTimeStart: string | null;
  contactTimeEnd: string | null;
  contactMethod: string | null;
  introduction: string | null;
  profilePublic: boolean;
}

const errorMessage = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string };
    return body.detail || body.message || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

export async function getProfile(memberId: number): Promise<ProfileDto> {
  const response = await fetch(`/api/profiles/${memberId}`);
  if (!response.ok) throw new Error(await errorMessage(response));
  return response.json() as Promise<ProfileDto>;
}

export async function saveProfile(memberId: number, profile: Omit<ProfileDto, 'memberId'>) {
  const response = await fetch(`/api/profiles/${memberId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(profile),
  });
  if (!response.ok) throw new Error(await errorMessage(response));
}
