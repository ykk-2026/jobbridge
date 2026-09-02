import { type ChangeEvent, useEffect, useRef, useState } from 'react';
import {
  ArrowRight,
  Bookmark,
  BookmarkCheck,
  Briefcase,
  Camera,
  Check,
  Clock3,
  Eye,
  EyeOff,
  Lock,
  Mail,
  MapPin,
  Pencil,
  Phone,
  Save,
  Trash2,
  UserRound,
} from 'lucide-react';
import { mockJobs } from '@/app/data/mockData';
import type { ApplicationFormData, CurrentUser, Job, Page } from '@/app/types';
import { getProfile, saveProfile, type ApiJobSeekerProfile } from '@/app/api/profileApi';

interface ProfilePageProps {
  currentUser: CurrentUser | null;
  navigate: (page: Page, jobId?: string) => void;
  bookmarks: Set<string>;
  appliedJobIds?: Set<string>;
  applicationForms?: Record<string, ApplicationFormData>;
  initialMenu?: string;
  onBookmark: (id: string) => void;
  onProfileSaved?: (profile: ApiJobSeekerProfile) => void;
  onUpdateApplication?: (id: string, formData: ApplicationFormData) => void;
  onDeleteApplication?: (id: string) => void;
}

type ProfileAvatar = {
  type: 'initial' | 'preset' | 'upload';
  value: string;
};

const regionOptions = [
  '서울특별시 송파구',
  '서울특별시 강남구',
  '서울특별시 강서구',
  '서울특별시 마포구',
  '경기도 성남시',
  '경기도 수원시',
  '경기도 고양시',
  '인천광역시',
  '부산광역시',
  '대구광역시',
  '대전광역시',
  '광주광역시',
  '울산광역시',
  '세종특별자치시',
  '강원특별자치도',
  '충청북도',
  '충청남도',
  '전북특별자치도',
  '전라남도',
  '경상북도',
  '경상남도',
  '제주특별자치도',
];

const menuItems = [
  { label: '내 프로필', icon: UserRound },
  { label: '지원 현황', icon: Briefcase },
  { label: '관심 공고', icon: Bookmark },
  { label: '계정 설정', icon: Lock },
];

const workTypes = [
  { label: '재택근무', field: 'remotePreferred' as const },
  { label: '유연근무', field: 'flexiblePreferred' as const },
  { label: '하이브리드', field: 'hybridPreferred' as const },
  { label: '출퇴근 근무', field: 'onsitePreferred' as const },
];

const workEnvironmentOptions = [
  { label: '휠체어 접근 필요', field: 'wheelchairRequired' as const },
  { label: '장애인 화장실 필요', field: 'accessibleRestroomRequired' as const },
  { label: '장애인 주차시설 필요', field: 'disabledParkingRequired' as const },
  { label: '재택근무 선호', field: 'remotePreferred' as const },
  { label: '유연근무 선호', field: 'flexiblePreferred' as const },
  { label: '보조공학기기 필요', field: 'assistiveDeviceRequired' as const },
];

const salaryPresets = ['3000', '4000', '5000', '6000', '10000'];
const maxIntroLength = 500;
const profilePhotoStoragePrefix = 'jobBridgeProfilePhoto';
const maxProfilePhotoSize = 3 * 1024 * 1024;

const defaultProfileOptions = [
  { id: 'blue', label: '블루', backgroundColor: '#E4EFFF', color: '#0D6BEA' },
  { id: 'green', label: '그린', backgroundColor: '#E8F7EF', color: '#14843C' },
  { id: 'yellow', label: '옐로', backgroundColor: '#FFF5D6', color: '#B7791F' },
  { id: 'gray', label: '그레이', backgroundColor: '#EEF2F6', color: '#475467' },
];

const genderLabels: Record<string, string> = {
  MALE: '남성',
  FEMALE: '여성',
  OTHER: '기타/선택 안함',
  남성: '남성',
  여성: '여성',
  '선택 안함': '기타/선택 안함',
};

const employmentTypeOptions = [
  { value: 'ANY', label: '무관' },
  { value: 'FULL_TIME', label: '정규직' },
  { value: 'PART_TIME', label: '파트타임' },
  { value: 'CONTRACT', label: '계약직' },
  { value: 'INTERNSHIP', label: '인턴' },
  { value: 'FREELANCER', label: '프리랜서' },
];

const careerTypeOptions = [
  { value: 'ANY', label: '무관' },
  { value: 'ENTRY', label: '신입' },
  { value: 'EXPERIENCED', label: '경력' },
];

const contactMethodOptions = [
  { value: 'PHONE', label: '전화' },
  { value: 'EMAIL', label: '이메일' },
  { value: 'SMS', label: '문자' },
  { value: 'KAKAO', label: '카카오톡' },
];

const getProfilePhotoStorageKey = (userId: string) => `${profilePhotoStoragePrefix}:${userId}`;

const formatSalaryInput = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 6);
  if (!digits) return '';
  return Number(digits).toLocaleString('ko-KR');
};

const formatCareerYears = (value: string) => value.replace(/\D/g, '').slice(0, 2);

const toNumberOrNull = (value: string) => {
  const digits = value.replace(/\D/g, '');
  return digits ? Number(digits) : null;
};

const toProfileFormValues = (profile: ApiJobSeekerProfile) => ({
  name: profile.name || '',
  birthDate: profile.birthDate || '',
  gender: profile.gender || 'OTHER',
  email: profile.email || '',
  phone: profile.phone || '',
  residenceRegion: profile.residenceRegion || '',
  desiredJob: profile.desiredJob || '',
  desiredRegion: profile.desiredRegion || '',
  employmentType: profile.employmentType || 'ANY',
  careerType: profile.careerType || 'ANY',
  careerYears: String(profile.careerYears ?? 0),
  minSalary: profile.minSalary ? Number(profile.minSalary).toLocaleString('ko-KR') : '',
  remotePreferred: Boolean(profile.remotePreferred),
  flexiblePreferred: Boolean(profile.flexiblePreferred),
  hybridPreferred: Boolean(profile.hybridPreferred),
  onsitePreferred: Boolean(profile.onsitePreferred),
  wheelchairRequired: Boolean(profile.wheelchairRequired),
  accessibleRestroomRequired: Boolean(profile.accessibleRestroomRequired),
  disabledParkingRequired: Boolean(profile.disabledParkingRequired),
  assistiveDeviceRequired: Boolean(profile.assistiveDeviceRequired),
  contactTimeStart: profile.contactTimeStart || '09:00',
  contactTimeEnd: profile.contactTimeEnd || '18:00',
  contactMethod: profile.contactMethod || 'PHONE',
  introduction: profile.introduction || '',
  profilePublic: profile.profilePublic ?? true,
});

const loadProfilePhoto = (userId: string) => {
  if (typeof window === 'undefined') return { type: 'initial', value: '' } satisfies ProfileAvatar;

  const savedAvatar = localStorage.getItem(getProfilePhotoStorageKey(userId));
  if (!savedAvatar) return { type: 'initial', value: '' } satisfies ProfileAvatar;

  if (savedAvatar.startsWith('data:image/')) {
    return { type: 'upload', value: savedAvatar } satisfies ProfileAvatar;
  }

  try {
    const parsedAvatar = JSON.parse(savedAvatar) as ProfileAvatar;
    if (parsedAvatar.type === 'initial' || parsedAvatar.type === 'preset' || parsedAvatar.type === 'upload') {
      return parsedAvatar;
    }
  } catch {
    localStorage.removeItem(getProfilePhotoStorageKey(userId));
  }

  return { type: 'initial', value: '' } satisfies ProfileAvatar;
};

export function ProfilePage({
  currentUser,
  navigate,
  bookmarks,
  appliedJobIds = new Set(),
  applicationForms = {},
  initialMenu = '내 프로필',
  onBookmark,
  onProfileSaved,
  onUpdateApplication,
  onDeleteApplication,
}: ProfilePageProps) {
  const initialName = currentUser?.name || '김민준';
  const initialAvatar = currentUser?.avatar || initialName.slice(0, 1);
  const userId = currentUser?.loginId || currentUser?.id || 'guest';
  const photoInputRef = useRef<HTMLInputElement>(null);
  const [activeMenu, setActiveMenu] = useState(initialMenu);
  const [savedMessage, setSavedMessage] = useState('');
  const [isSavingProfile, setIsSavingProfile] = useState(false);
  const [isPreviewOpen, setIsPreviewOpen] = useState(false);
  const [isResidenceRegionOpen, setIsResidenceRegionOpen] = useState(false);
  const [isDesiredRegionOpen, setIsDesiredRegionOpen] = useState(false);
  const [isPhotoPickerOpen, setIsPhotoPickerOpen] = useState(false);
  const [editingApplicationJobId, setEditingApplicationJobId] = useState<string | null>(null);
  const [applicationEditError, setApplicationEditError] = useState('');
  const [applicationEditForm, setApplicationEditForm] = useState({
    name: '',
    phone1: '010',
    phone2: '',
    phone3: '',
    email: '',
    employmentType: '정규/계약직',
    privacyAgreed: true,
  });
  const [profileAvatar, setProfileAvatar] = useState<ProfileAvatar>(() => loadProfilePhoto(userId));
  const [accountForm, setAccountForm] = useState({
    loginId: currentUser?.loginId || currentUser?.id || 'minjun_kim',
    email: currentUser?.email || '',
    phone: currentUser?.phone || '010-1234-5678',
    currentPassword: '',
    newPassword: '',
    status: 'ACTIVE',
  });
  const [profileForm, setProfileForm] = useState({
    name: initialName,
    birthDate: currentUser?.birthDate || '1998-05-23',
    gender: currentUser?.gender || 'MALE',
    email: currentUser?.email || '',
    phone: currentUser?.phone || '010-1234-5678',
    residenceRegion: currentUser?.currentRegion || '서울특별시 송파구',
    desiredJob: currentUser?.preferredRole || '',
    desiredRegion: '서울특별시 송파구',
    employmentType: 'ANY',
    careerType: 'ANY',
    careerYears: '0',
    minSalary: '',
    remotePreferred: Boolean(currentUser?.remotePreferred),
    flexiblePreferred: Boolean(currentUser?.flexiblePreferred),
    hybridPreferred: false,
    onsitePreferred: false,
    wheelchairRequired: Boolean(currentUser?.wheelchairRequired),
    accessibleRestroomRequired: Boolean(currentUser?.accessibleRestroomRequired),
    disabledParkingRequired: Boolean(currentUser?.disabledParkingRequired),
    assistiveDeviceRequired: Boolean(currentUser?.assistiveDeviceRequired),
    contactTimeStart: '09:00',
    contactTimeEnd: '18:00',
    contactMethod: 'PHONE',
    introduction: '',
    profilePublic: true,
  });

  useEffect(() => {
    if (!currentUser?.id || currentUser.role !== 'personal') return;

    let cancelled = false;
    getProfile(currentUser.id)
      .then(profile => {
        if (cancelled) return;
        const nextProfile = toProfileFormValues(profile);
        setProfileForm(nextProfile);
        setAccountForm(prev => ({
          ...prev,
          email: nextProfile.email,
          phone: nextProfile.phone,
        }));
      })
      .catch(error => {
        if (!cancelled) {
          setSavedMessage(error instanceof Error ? error.message : '프로필 정보를 불러오지 못했습니다.');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [currentUser?.id, currentUser?.role]);

  useEffect(() => {
    setProfileAvatar(loadProfilePhoto(userId));
  }, [userId]);

  useEffect(() => {
    setActiveMenu(initialMenu);
  }, [initialMenu]);
  const avatarInitial = (profileForm.name.trim() || initialName).slice(0, 1);
  const selectedDefaultProfile = defaultProfileOptions.find(option => option.id === profileAvatar.value) || defaultProfileOptions[0];
  const savedJobs = mockJobs
    .map(job => ({ ...job, savedKey: bookmarks.has(`job-${job.id}`) ? `job-${job.id}` : '' }))
    .filter(job => job.savedKey);
  const appliedJobs = mockJobs.filter(job => appliedJobIds.has(job.id));
  const editingApplicationJob = editingApplicationJobId ? mockJobs.find(job => job.id === editingApplicationJobId) : null;

  const updateForm = (field: keyof typeof profileForm, value: string | boolean) => {
    setProfileForm(prev => ({ ...prev, [field]: value }));
    setSavedMessage('');
  };

  const updateAccountForm = (field: keyof typeof accountForm, value: string) => {
    setAccountForm(prev => ({ ...prev, [field]: value }));
    setSavedMessage('');
  };

  const updateApplicationEditForm = (field: keyof typeof applicationEditForm, value: string | boolean) => {
    setApplicationEditForm(prev => ({ ...prev, [field]: value }));
    setApplicationEditError('');
  };

  const toggleWorkType = (field: (typeof workTypes)[number]['field']) => {
    setProfileForm(prev => ({ ...prev, [field]: !prev[field] }));
    setSavedMessage('');
  };

  const saveProfileAvatar = (nextAvatar: ProfileAvatar, message: string) => {
    setProfileAvatar(nextAvatar);
    try {
      localStorage.setItem(getProfilePhotoStorageKey(userId), JSON.stringify(nextAvatar));
      setSavedMessage(message);
    } catch {
      window.alert('프로필 사진 저장 공간이 부족합니다. 더 작은 이미지를 선택해 주세요.');
    }
  };

  const renderProfileAvatar = (className: string) => {
    if (profileAvatar.type === 'upload' && profileAvatar.value) {
      return (
        <div className={`${className} overflow-hidden rounded-full bg-[#E4EFFF]`}>
          <img src={profileAvatar.value} alt={`${profileForm.name} 프로필 사진`} className="h-full w-full object-cover" />
        </div>
      );
    }

    const avatarStyle = profileAvatar.type === 'preset'
      ? { backgroundColor: selectedDefaultProfile.backgroundColor, color: selectedDefaultProfile.color }
      : { backgroundColor: '#E4EFFF', color: '#0D6BEA' };

    return (
      <div className={`${className} rounded-full font-extrabold`} style={avatarStyle}>
        {avatarInitial || initialAvatar}
      </div>
    );
  };

  const handlePersistProfile = async () => {
    if (profileForm.contactTimeStart && profileForm.contactTimeEnd && profileForm.contactTimeStart >= profileForm.contactTimeEnd) {
      window.alert('연락 가능 시작시간은 종료시간보다 빨라야 합니다.');
      return;
    }

    if (!currentUser?.id || currentUser.role !== 'personal') {
      window.alert('로그인한 개인회원만 프로필을 저장할 수 있습니다.');
      return;
    }

    const payload: ApiJobSeekerProfile = {
      memberId: Number(currentUser.id),
      name: profileForm.name.trim(),
      birthDate: profileForm.birthDate,
      gender: profileForm.gender,
      email: profileForm.email.trim(),
      phone: profileForm.phone.trim(),
      residenceRegion: profileForm.residenceRegion,
      desiredJob: profileForm.desiredJob.trim(),
      desiredRegion: profileForm.desiredRegion,
      employmentType: profileForm.employmentType,
      careerType: profileForm.careerType,
      careerYears: toNumberOrNull(profileForm.careerYears),
      minSalary: toNumberOrNull(profileForm.minSalary),
      remotePreferred: profileForm.remotePreferred,
      flexiblePreferred: profileForm.flexiblePreferred,
      wheelchairRequired: profileForm.wheelchairRequired,
      accessibleRestroomRequired: profileForm.accessibleRestroomRequired,
      disabledParkingRequired: profileForm.disabledParkingRequired,
      assistiveDeviceRequired: profileForm.assistiveDeviceRequired,
      hybridPreferred: profileForm.hybridPreferred,
      onsitePreferred: profileForm.onsitePreferred,
      contactTimeStart: profileForm.contactTimeStart,
      contactTimeEnd: profileForm.contactTimeEnd,
      contactMethod: profileForm.contactMethod,
      introduction: profileForm.introduction,
      profilePublic: profileForm.profilePublic,
    };

    setIsSavingProfile(true);
    setSavedMessage('');
    try {
      await saveProfile(currentUser.id, payload);
      onProfileSaved?.(payload);
      setSavedMessage('프로필이 변경되었습니다.');
      window.alert('프로필이 변경되었습니다.');
    } catch (error) {
      const message = error instanceof Error ? error.message : '프로필 변경에 실패했습니다.';
      setSavedMessage(message);
      window.alert(message);
    } finally {
      setIsSavingProfile(false);
    }
  };

  const handleSave = () => {
    if (profileForm.contactTimeStart && profileForm.contactTimeEnd && profileForm.contactTimeStart >= profileForm.contactTimeEnd) {
      window.alert('연락 가능 시작시간은 종료시간보다 빨라야 합니다.');
      return;
    }

    setSavedMessage('프로필 정보가 저장되었습니다.');
    window.alert('프로필 정보가 저장되었습니다.');
  };

  const saveAccountSettings = () => {
    setSavedMessage('회원 계정 정보가 저장되었습니다.');
    setAccountForm(prev => ({ ...prev, currentPassword: '', newPassword: '' }));
  };

  const handleProfilePhotoChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      window.alert('이미지 파일만 선택할 수 있습니다.');
      return;
    }

    if (file.size > maxProfilePhotoSize) {
      window.alert('프로필 사진은 3MB 이하 이미지만 등록할 수 있습니다.');
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const nextPhotoUrl = typeof reader.result === 'string' ? reader.result : '';
      if (!nextPhotoUrl) return;

      saveProfileAvatar({ type: 'upload', value: nextPhotoUrl }, '프로필 사진이 변경되었습니다.');
      setIsPhotoPickerOpen(false);
    };
    reader.readAsDataURL(file);
  };

  const openApplicationEdit = (job: Job) => {
    const savedApplication = applicationForms[job.id];
    const phoneParts = (savedApplication?.phone || profileForm.phone || '010--').split('-');

    setApplicationEditForm({
      name: savedApplication?.name || profileForm.name,
      phone1: phoneParts[0] || '010',
      phone2: phoneParts[1] || '',
      phone3: phoneParts[2] || '',
      email: savedApplication?.email || profileForm.email,
      employmentType: savedApplication?.employmentType || (job.category === 'PartTime' ? '아르바이트' : '정규/계약직'),
      privacyAgreed: savedApplication?.privacyAgreed ?? true,
    });
    setApplicationEditError('');
    setEditingApplicationJobId(job.id);
  };

  const closeApplicationEdit = () => {
    setEditingApplicationJobId(null);
    setApplicationEditError('');
  };

  const saveApplicationEdit = () => {
    if (!editingApplicationJob || !onUpdateApplication) return;

    const phoneParts = [applicationEditForm.phone1, applicationEditForm.phone2, applicationEditForm.phone3].map(value => String(value).trim());

    if (!applicationEditForm.privacyAgreed) {
      setApplicationEditError('개인정보 전달 동의가 필요합니다.');
      return;
    }

    if (!applicationEditForm.name.trim() || /\s/.test(applicationEditForm.name)) {
      setApplicationEditError('성명은 공백 없이 입력해 주세요.');
      return;
    }

    if (phoneParts.some(part => !/^\d+$/.test(part)) || phoneParts[1].length < 3 || phoneParts[2].length < 4) {
      setApplicationEditError('연락 가능한 휴대전화 번호를 정확히 입력해 주세요.');
      return;
    }

    if (!applicationEditForm.email.trim() || !applicationEditForm.email.includes('@')) {
      setApplicationEditError('지원 결과를 받을 이메일을 입력해 주세요.');
      return;
    }

    const previousApplication = applicationForms[editingApplicationJob.id];
    const now = new Date().toISOString();
    onUpdateApplication(editingApplicationJob.id, {
      name: applicationEditForm.name.trim(),
      phone: phoneParts.join('-'),
      email: applicationEditForm.email.trim(),
      employmentType: applicationEditForm.employmentType,
      privacyAgreed: applicationEditForm.privacyAgreed,
      submittedAt: previousApplication?.submittedAt || now,
      updatedAt: now,
    });
    setSavedMessage('지원서가 수정되었습니다.');
    closeApplicationEdit();
  };

  const deleteApplication = () => {
    if (!editingApplicationJob || !onDeleteApplication) return;

    const confirmed = window.confirm(`${editingApplicationJob.company} ${editingApplicationJob.title} 지원서를 삭제하시겠습니까?`);
    if (!confirmed) return;

    onDeleteApplication(editingApplicationJob.id);
    setSavedMessage('지원서가 삭제되었습니다.');
    closeApplicationEdit();
  };

  const selectedWorkTypes = workTypes.filter(item => profileForm[item.field]).map(item => item.label);
  return (
    <div className="min-h-screen bg-[#F3F7FF] text-[#111827]">
      <main className="mx-auto grid max-w-[1256px] gap-5 px-5 py-5 sm:px-8 lg:grid-cols-[260px_1fr]">
        <aside className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
          <div className="mb-4 rounded-xl bg-[#F4F8FC] px-4 py-4">
            <div className="flex items-center gap-3">
              {renderProfileAvatar('flex h-11 w-11 shrink-0 items-center justify-center text-lg')}
              <div className="min-w-0">
                <p className="truncate text-base font-extrabold text-black">{profileForm.name}</p>
                <p className="mt-1 truncate text-xs font-bold text-[#7A8495]">{profileForm.desiredJob || '개인회원'}</p>
              </div>
            </div>
          </div>

          <nav className="space-y-1">
            {menuItems.map(item => {
              const Icon = item.icon;
              return (
                <button
                  type="button"
                  key={item.label}
                  aria-pressed={activeMenu === item.label}
                  onClick={() => {
                    setActiveMenu(item.label);
                    setSavedMessage('');
                  }}
                  className={`flex w-full items-center gap-2 rounded-lg px-3 py-3 text-left text-sm font-bold transition ${
                    activeMenu === item.label ? 'bg-[#E4EFFF] text-[#0D6BEA]' : 'bg-transparent text-[#344054] hover:text-[#0D6BEA]'
                  }`}
                >
                  <Icon size={17} />
                  {item.label}
                </button>
              );
            })}
          </nav>
        </aside>

        <section className="space-y-5">
          <div className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-bold text-[#0D6BEA]">{activeMenu}</p>
                <h1 className="mt-1 text-2xl font-extrabold text-black">프로필 관리</h1>
              </div>
              {activeMenu === '내 프로필' && (
                <div className="flex gap-2">
                  <button type="button" onClick={() => setIsPreviewOpen(true)} className="rounded-lg border border-[#D7DDE5] px-4 py-2 text-sm font-bold hover:bg-[#F8FAFC]">
                    미리보기
                  </button>
                  <button type="button" onClick={handlePersistProfile} disabled={isSavingProfile} className="inline-flex items-center gap-2 rounded-lg bg-[#0D6BEA] px-4 py-2 text-sm font-bold text-white hover:bg-[#0959C7] disabled:cursor-not-allowed disabled:opacity-60">
                    <Save size={16} />
                    저장
                  </button>
                </div>
              )}
            </div>
            {savedMessage && <p className="mt-4 rounded-lg bg-[#E7F8EF] px-4 py-3 text-sm font-bold text-[#14843C]">{savedMessage}</p>}
          </div>

          {activeMenu === '지원 현황' ? (
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h2 className="text-lg font-extrabold text-black">지원 현황</h2>
                </div>
                <button type="button" onClick={() => navigate('jobs')} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  공고 더 보기 <ArrowRight size={15} />
                </button>
              </div>

              {appliedJobs.length > 0 ? (
                <div className="grid gap-3">
                  {appliedJobs.map(job => {
                    const savedApplication = applicationForms[job.id];
                    return (
                      <article key={job.id} className="rounded-lg border border-[#E1E6EE] bg-white p-4">
                        <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                          <button type="button" onClick={() => navigate('job-detail', `job-${job.id}`)} className="flex min-w-0 items-start gap-3 text-left">
                            <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg text-lg font-extrabold text-white" style={{ backgroundColor: job.companyColor }}>
                              {job.companyInitials}
                            </span>
                            <span className="min-w-0">
                              <span className="block text-sm font-bold text-[#7A8495]">{job.company}</span>
                              <span className="mt-1 block text-lg font-extrabold text-black">{job.title}</span>
                              <span className="mt-2 flex flex-wrap gap-2 text-xs font-bold text-[#596273]">
                                <span className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1">
                                  <MapPin size={13} /> {job.location}
                                </span>
                                <span className="rounded-full bg-[#E7F8EF] px-3 py-1 text-[#14843C]">APPLIED</span>
                                {savedApplication?.employmentType && (
                                  <span className="rounded-full bg-[#EEF5FF] px-3 py-1 text-[#0D6BEA]">{savedApplication.employmentType}</span>
                                )}
                              </span>
                              {savedApplication && (
                                <span className="mt-3 block text-xs font-bold leading-5 text-[#7A8495]">
                                  {savedApplication.name} · {savedApplication.phone} · {savedApplication.email}
                                </span>
                              )}
                            </span>
                          </button>

                          <div className="flex flex-wrap gap-2 lg:justify-end">
                            <button
                              type="button"
                              onClick={() => openApplicationEdit(job)}
                              className="inline-flex h-9 items-center gap-2 rounded-lg border border-[#D7DDE5] px-3 text-sm font-bold text-[#344054] hover:bg-[#F8FAFC]"
                            >
                              <Pencil size={15} />
                              지원 수정
                            </button>
                            <button type="button" onClick={() => navigate('job-detail', `job-${job.id}`)} className="inline-flex h-9 items-center gap-1 px-1 text-sm font-extrabold text-[#0D6BEA]">
                              상세 보기 <ArrowRight size={15} />
                            </button>
                          </div>
                        </div>
                      </article>
                    );
                  })}
                </div>
              ) : (
                <div className="rounded-lg border border-dashed border-[#D7DDE5] bg-[#F8FAFC] p-8 text-center">
                  <Briefcase size={32} className="mx-auto text-[#7A8495]" />
                  <p className="mt-3 text-base font-extrabold text-black">지원한 공고가 없습니다.</p>
                  <button type="button" onClick={() => navigate('jobs')} className="mt-4 rounded-lg bg-[#0D6BEA] px-5 py-2.5 text-sm font-bold text-white hover:bg-[#0959C7]">
                    공고 보러가기
                  </button>
                </div>
              )}
            </section>
          ) : activeMenu === '관심 공고' ? (
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h2 className="text-lg font-extrabold text-black">관심 공고</h2>
                </div>
                <button type="button" onClick={() => navigate('jobs')} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  공고 더 보기 <ArrowRight size={15} />
                </button>
              </div>

              {savedJobs.length > 0 ? (
                <div className="grid gap-3">
                  {savedJobs.map(job => (
                    <article key={job.id} className="rounded-lg border border-[#E1E6EE] bg-white p-4">
                      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                        <button type="button" onClick={() => navigate('job-detail', `job-${job.id}`)} className="flex min-w-0 items-start gap-3 text-left">
                          <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg text-lg font-extrabold text-white" style={{ backgroundColor: job.companyColor }}>
                            {job.companyInitials}
                          </span>
                          <span className="min-w-0">
                            <span className="block text-sm font-bold text-[#7A8495]">{job.company}</span>
                            <span className="mt-1 block text-lg font-extrabold text-black">{job.title}</span>
                            <span className="mt-2 flex flex-wrap gap-2 text-xs font-bold text-[#596273]">
                              <span className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1">
                                <MapPin size={13} /> {job.location}
                              </span>
                              <span className="rounded-full bg-[#F1F3F6] px-3 py-1">{job.workType}</span>
                            </span>
                          </span>
                        </button>

                        <button
                          type="button"
                          onClick={() => onBookmark(job.savedKey)}
                          className="inline-flex h-9 items-center gap-2 rounded-lg border border-[#D7DDE5] px-3 text-sm font-bold hover:bg-[#F8FAFC]"
                        >
                          <BookmarkCheck size={16} />
                          저장 해제
                        </button>
                      </div>
                    </article>
                  ))}
                </div>
              ) : (
                <div className="rounded-lg border border-dashed border-[#D7DDE5] bg-[#F8FAFC] p-8 text-center">
                  <Bookmark size={32} className="mx-auto text-[#7A8495]" />
                  <p className="mt-3 text-base font-extrabold text-black">저장한 공고가 없습니다.</p>
                  <button type="button" onClick={() => navigate('jobs')} className="mt-4 rounded-lg bg-[#0D6BEA] px-5 py-2.5 text-sm font-bold text-white hover:bg-[#0959C7]">
                    공고 보러가기
                  </button>
                </div>
              )}
            </section>
          ) : activeMenu === '계정 설정' ? (
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <div className="mb-5">
                <h2 className="text-lg font-extrabold text-black">계정 설정</h2>
              </div>

              <div className="grid gap-4 sm:grid-cols-2">
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">로그인 아이디</span>
                  <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.loginId} onChange={event => updateAccountForm('loginId', event.target.value)} />
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">회원 상태</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={accountForm.status} onChange={event => updateAccountForm('status', event.target.value)}>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="SUSPENDED">SUSPENDED</option>
                  </select>
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">이메일</span>
                  <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.email} onChange={event => updateAccountForm('email', event.target.value)} />
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">전화번호</span>
                  <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.phone} onChange={event => updateAccountForm('phone', event.target.value)} />
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">현재 비밀번호</span>
                  <input type="password" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.currentPassword} onChange={event => updateAccountForm('currentPassword', event.target.value)} />
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-bold">새 비밀번호</span>
                  <input type="password" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.newPassword} onChange={event => updateAccountForm('newPassword', event.target.value)} placeholder="변경할 때만 입력하세요" />
                </label>
              </div>

              <button type="button" onClick={saveAccountSettings} className="mt-6 rounded-lg bg-[#0D6BEA] px-5 py-3 text-sm font-extrabold text-white hover:bg-[#0959C7]">
                계정 설정 저장
              </button>
            </section>
          ) : (
            <>
              <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
                <div className="mb-5 flex items-center justify-between">
                  <div>
                    <h2 className="text-lg font-extrabold">회원 기본 정보</h2>
                  </div>
                  <button type="button" onClick={() => setIsPhotoPickerOpen(prev => !prev)} className="inline-flex items-center gap-2 rounded-lg border border-[#D7DDE5] px-3 py-2 text-sm font-bold hover:bg-[#F8FAFC]">
                    <Camera size={16} />
                    사진 변경
                  </button>
                  <input ref={photoInputRef} type="file" accept="image/*" className="hidden" onChange={handleProfilePhotoChange} />
                </div>

                {isPhotoPickerOpen && (
                  <div className="mb-5 rounded-lg border border-[#DDE3EA] bg-[#F8FAFC] p-4">
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                      <div className="flex items-center gap-3">
                        {renderProfileAvatar('flex h-16 w-16 shrink-0 items-center justify-center text-2xl')}
                        <div>
                          <p className="text-sm font-extrabold text-black">프로필 사진 선택</p>
                          <p className="mt-1 text-xs font-semibold text-[#7A8495]">job_seeker_profile.profile_image_url에 대응되는 정보입니다.</p>
                        </div>
                      </div>
                      <button type="button" onClick={() => photoInputRef.current?.click()} className="inline-flex items-center justify-center gap-2 rounded-lg bg-[#0D6BEA] px-4 py-2 text-sm font-bold text-white hover:bg-[#0959C7]">
                        <Camera size={16} />
                        사진 업로드
                      </button>
                    </div>

                    <div className="mt-4">
                      <p className="mb-2 text-sm font-bold text-[#344054]">기본 프로필</p>
                      <div className="flex flex-wrap gap-2">
                        <button
                          type="button"
                          onClick={() => saveProfileAvatar({ type: 'initial', value: '' }, '기본 이니셜 프로필이 선택되었습니다.')}
                          aria-pressed={profileAvatar.type === 'initial'}
                          className={`flex h-12 w-12 items-center justify-center rounded-full border-2 bg-[#E4EFFF] text-base font-extrabold text-[#0D6BEA] ${
                            profileAvatar.type === 'initial' ? 'border-[#0D6BEA]' : 'border-transparent hover:border-[#B9D6FF]'
                          }`}
                        >
                          {avatarInitial || initialAvatar}
                        </button>
                        {defaultProfileOptions.map(option => (
                          <button
                            type="button"
                            key={option.id}
                            onClick={() => saveProfileAvatar({ type: 'preset', value: option.id }, '기본 프로필이 선택되었습니다.')}
                            aria-pressed={profileAvatar.type === 'preset' && profileAvatar.value === option.id}
                            className={`flex h-12 w-12 items-center justify-center rounded-full border-2 text-base font-extrabold ${
                              profileAvatar.type === 'preset' && profileAvatar.value === option.id ? 'border-[#0D6BEA]' : 'border-transparent hover:border-[#B9D6FF]'
                            }`}
                            style={{ backgroundColor: option.backgroundColor, color: option.color }}
                            title={`${option.label} 기본 프로필`}
                          >
                            {avatarInitial || initialAvatar}
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>
                )}

                <div className="grid gap-4 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">이름</span>
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.name} onChange={event => updateForm('name', event.target.value)} />
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">생년월일</span>
                    <input type="date" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.birthDate} onChange={event => updateForm('birthDate', event.target.value)} />
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">성별</span>
                    <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.gender} onChange={event => updateForm('gender', event.target.value)}>
                      <option value="MALE">남성</option>
                      <option value="FEMALE">여성</option>
                      <option value="OTHER">기타/선택 안함</option>
                    </select>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">이메일</span>
                    <div className="relative">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.email} onChange={event => updateForm('email', event.target.value)} />
                      <Mail size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-[#7A8495]" />
                    </div>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">전화번호</span>
                    <div className="relative">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.phone} onChange={event => updateForm('phone', event.target.value)} />
                      <Phone size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-[#7A8495]" />
                    </div>
                  </label>
                  <label className="relative block">
                    <span className="mb-2 block text-sm font-bold">현재 거주지역</span>
                    <button type="button" onClick={() => setIsResidenceRegionOpen(prev => !prev)} className="flex w-full items-center justify-between rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-left text-sm">
                      {profileForm.residenceRegion}
                      <span className="text-[#7A8495]">⌄</span>
                    </button>
                    {isResidenceRegionOpen && (
                      <div className="absolute left-0 right-0 top-full z-20 mt-1 max-h-56 overflow-y-auto rounded-lg border border-[#DCEAF3] bg-white shadow-lg">
                        {regionOptions.map(region => (
                          <button
                            type="button"
                            key={region}
                            onClick={() => {
                              updateForm('residenceRegion', region);
                              setIsResidenceRegionOpen(false);
                            }}
                            className={`block w-full px-3 py-2.5 text-left text-sm hover:bg-[#F4F8FC] ${
                              profileForm.residenceRegion === region ? 'bg-[#E4EFFF] font-bold text-[#0D6BEA]' : 'text-[#111827]'
                            }`}
                          >
                            {region}
                          </button>
                        ))}
                      </div>
                    )}
                  </label>
                </div>
              </section>

              <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
                <h2 className="mb-5 text-lg font-extrabold">구직 조건</h2>
                <div className="grid gap-4 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">희망 직무</span>
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.desiredJob} onChange={event => updateForm('desiredJob', event.target.value)} placeholder="예: 백엔드 개발자" />
                  </label>
                  <label className="relative block">
                    <span className="mb-2 block text-sm font-bold">희망 근무지역</span>
                    <button type="button" onClick={() => setIsDesiredRegionOpen(prev => !prev)} className="flex w-full items-center justify-between rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-left text-sm">
                      {profileForm.desiredRegion}
                      <span className="text-[#7A8495]">⌄</span>
                    </button>
                    {isDesiredRegionOpen && (
                      <div className="absolute left-0 right-0 top-full z-20 mt-1 max-h-56 overflow-y-auto rounded-lg border border-[#DCEAF3] bg-white shadow-lg">
                        {regionOptions.map(region => (
                          <button
                            type="button"
                            key={region}
                            onClick={() => {
                              updateForm('desiredRegion', region);
                              setIsDesiredRegionOpen(false);
                            }}
                            className={`block w-full px-3 py-2.5 text-left text-sm hover:bg-[#F4F8FC] ${
                              profileForm.desiredRegion === region ? 'bg-[#E4EFFF] font-bold text-[#0D6BEA]' : 'text-[#111827]'
                            }`}
                          >
                            {region}
                          </button>
                        ))}
                      </div>
                    )}
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">희망 고용형태</span>
                    <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.employmentType} onChange={event => updateForm('employmentType', event.target.value)}>
                      {employmentTypeOptions.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}
                    </select>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">경력 구분</span>
                    <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.careerType} onChange={event => updateForm('careerType', event.target.value)}>
                      {careerTypeOptions.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}
                    </select>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">경력 연수</span>
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.careerYears} onChange={event => updateForm('careerYears', formatCareerYears(event.target.value))} inputMode="numeric" placeholder="예: 3" />
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">희망 최소 연봉 <span className="text-[#7A8495]">(만원)</span></span>
                    <div className="relative">
                      <input
                        className="w-full rounded-lg border border-[#DCEAF3] py-3 pl-3 pr-24 text-sm font-bold outline-none focus:ring-4 focus:ring-[#0D6BEA]/15"
                        value={profileForm.minSalary}
                        onChange={event => updateForm('minSalary', formatSalaryInput(event.target.value))}
                        inputMode="numeric"
                        placeholder="예: 4,000"
                      />
                      <span className="pointer-events-none absolute right-3 top-1/2 inline-flex min-w-[70px] -translate-y-1/2 justify-end whitespace-nowrap text-sm font-extrabold text-[#596273]">
                        만원 이상
                      </span>
                    </div>
                    <div className="mt-2 flex flex-wrap gap-2">
                      {salaryPresets.map(salary => (
                        <button
                          type="button"
                          key={salary}
                          onClick={() => updateForm('minSalary', Number(salary).toLocaleString('ko-KR'))}
                          className={`rounded-full px-3 py-1.5 text-xs font-extrabold ${
                            profileForm.minSalary === Number(salary).toLocaleString('ko-KR') ? 'bg-[#0D6BEA] text-white' : 'bg-[#F1F3F6] text-[#344054] hover:bg-[#E4EFFF]'
                          }`}
                        >
                          {Number(salary).toLocaleString('ko-KR')}만원
                        </button>
                      ))}
                    </div>
                  </label>
                </div>

              </section>

              <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
                <div className="mb-5">
                  <h2 className="text-lg font-extrabold">희망 근무환경 및 편의지원</h2>
                  <p className="mt-1 text-sm font-semibold text-[#7A8495]">취업 시 필요하거나 선호하는 근무환경을 선택해주세요.</p>
                </div>
                <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
                  {workEnvironmentOptions.map(option => {
                    const inputId = `profile-${option.field}`;
                    const checked = profileForm[option.field];

                    return (
                      <div key={option.field} className="relative">
                        <input
                          id={inputId}
                          name={option.field}
                          type="checkbox"
                          checked={checked}
                          onChange={event => updateForm(option.field, event.target.checked)}
                          className="peer sr-only"
                        />
                        <label
                          htmlFor={inputId}
                          className={`flex min-h-12 cursor-pointer items-center gap-3 rounded-lg border px-4 py-3 text-sm font-bold transition ${
                            checked ? 'border-[#0D6BEA] bg-[#EEF5FF] text-[#0D6BEA]' : 'border-[#DCEAF3] bg-white text-[#344054] hover:bg-[#F8FAFC]'
                          } peer-focus-visible:ring-4 peer-focus-visible:ring-[#0D6BEA]/15`}
                        >
                          <span
                            aria-hidden="true"
                            className={`flex h-5 w-5 shrink-0 items-center justify-center rounded border ${
                              checked ? 'border-[#0D6BEA] bg-[#0D6BEA] text-white' : 'border-[#B8C2CC] bg-white text-transparent'
                            }`}
                          >
                            {checked && <Check size={14} strokeWidth={3} />}
                          </span>
                          <span>{option.label}</span>
                        </label>
                      </div>
                    );
                  })}
                </div>
              </section>

              <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
                <h2 className="mb-5 text-lg font-extrabold">연락 및 공개 설정</h2>
                <div className="grid gap-4 sm:grid-cols-2">
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">연락 가능 시작시간</span>
                    <div className="relative">
                      <input type="time" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.contactTimeStart} onChange={event => updateForm('contactTimeStart', event.target.value)} />
                      <Clock3 size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-[#7A8495]" />
                    </div>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">연락 가능 종료시간</span>
                    <div className="relative">
                      <input type="time" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.contactTimeEnd} onChange={event => updateForm('contactTimeEnd', event.target.value)} />
                      <Clock3 size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-[#7A8495]" />
                    </div>
                  </label>
                  <label className="block">
                    <span className="mb-2 block text-sm font-bold">선호 연락 방식</span>
                    <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.contactMethod} onChange={event => updateForm('contactMethod', event.target.value)}>
                      {contactMethodOptions.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}
                    </select>
                  </label>
                  <div className="block">
                    <span className="mb-2 block text-sm font-bold">프로필 공개 여부</span>
                    <button
                      type="button"
                      onClick={() => updateForm('profilePublic', !profileForm.profilePublic)}
                      className={`flex h-12 w-full items-center justify-between rounded-lg border px-4 text-sm font-extrabold ${
                        profileForm.profilePublic ? 'border-[#0D6BEA] bg-[#EEF5FF] text-[#0D6BEA]' : 'border-[#DCEAF3] bg-white text-[#596273]'
                      }`}
                    >
                      <span>{profileForm.profilePublic ? '공개' : '비공개'}</span>
                      {profileForm.profilePublic ? <Eye size={18} /> : <EyeOff size={18} />}
                    </button>
                  </div>
                </div>
              </section>

              <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
                <h2 className="mb-5 text-lg font-extrabold">자기소개</h2>
                <textarea
                  value={profileForm.introduction}
                  onChange={event => updateForm('introduction', event.target.value.slice(0, maxIntroLength))}
                  rows={6}
                  placeholder="지원 시 참고할 자기소개를 입력하세요."
                  className="w-full resize-none rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm outline-none focus:ring-4 focus:ring-[#0D6BEA]/15"
                />
                <p className="mt-2 text-right text-sm font-bold text-[#7A8495]">{profileForm.introduction.length} / {maxIntroLength}자</p>
              </section>
            </>
          )}
        </section>
      </main>

      {editingApplicationJob && (
        <div className="fixed inset-0 z-50 flex items-center justify-center overflow-x-hidden bg-black/45 px-4 py-6">
          <div className="max-h-[92vh] w-full max-w-2xl overflow-y-auto overflow-x-hidden rounded-xl bg-white shadow-xl">
            <div className="flex items-start justify-between gap-4 border-b border-[#E7ECF2] px-6 py-5">
              <div>
                <p className="text-sm font-bold text-[#0D6BEA]">{editingApplicationJob.company}</p>
                <h2 className="mt-1 text-xl font-extrabold text-black">지원서 수정</h2>
                <p className="mt-1 text-sm font-semibold text-[#7A8495]">{editingApplicationJob.title}</p>
              </div>
              <button
                type="button"
                onClick={closeApplicationEdit}
                className="flex h-9 w-9 items-center justify-center rounded-full bg-[#F1F3F6] text-lg font-bold text-[#596273] hover:bg-[#E1E6EE]"
                aria-label="지원서 수정 창 닫기"
              >
                x
              </button>
            </div>

            <div className="px-6 py-6">
              <div className="rounded-lg bg-[#F8FAFC] px-4 py-3 text-sm font-bold text-[#344054]">
                공고명: <span className="text-black">{editingApplicationJob.title}</span>
              </div>

              <div className="mt-5 divide-y divide-[#E7ECF2] border-t border-[#D7DDE5]">
                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">성명</span>
                  <input
                    value={applicationEditForm.name}
                    onChange={event => updateApplicationEditForm('name', event.target.value)}
                    className="h-11 w-full rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                  />
                </label>

                <div className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <p className="text-sm font-bold text-[#596273]">휴대전화</p>
                  <div className="grid min-w-0 grid-cols-1 gap-2 sm:grid-cols-3">
                    {(['phone1', 'phone2', 'phone3'] as const).map((field, index) => (
                      <input
                        key={field}
                        value={applicationEditForm[field]}
                        onChange={event => updateApplicationEditForm(field, event.target.value.replace(/\D/g, '').slice(0, index === 0 ? 3 : 4))}
                        className="h-11 min-w-0 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                      />
                    ))}
                  </div>
                </div>

                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">이메일</span>
                  <input
                    value={applicationEditForm.email}
                    onChange={event => updateApplicationEditForm('email', event.target.value)}
                    placeholder="email@example.com"
                    className="h-11 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                  />
                </label>

                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">고용형태</span>
                  <select
                    value={applicationEditForm.employmentType}
                    onChange={event => updateApplicationEditForm('employmentType', event.target.value)}
                    className="h-11 rounded border border-[#D7DDE5] bg-white px-3 text-sm font-bold text-[#344054] outline-none focus:border-[#0D6BEA]"
                  >
                    <option value="아르바이트">아르바이트</option>
                    <option value="정규/계약직">정규/계약직</option>
                    <option value="인턴">인턴</option>
                  </select>
                </label>
              </div>

              <label className="mt-5 flex items-start gap-2 rounded-lg bg-[#F8FAFC] px-4 py-3 text-sm font-bold text-[#344054]">
                <input
                  type="checkbox"
                  checked={applicationEditForm.privacyAgreed}
                  onChange={event => updateApplicationEditForm('privacyAgreed', event.target.checked)}
                  className="mt-1"
                />
                수정된 이름, 연락처, 이메일을 채용 담당자에게 전달하는 데 동의합니다.
              </label>

              {applicationEditError && <p className="mt-4 rounded-lg bg-[#FFF5F5] px-4 py-3 text-sm font-bold text-[#D92D20]">{applicationEditError}</p>}

              <div className="mt-6 flex flex-col gap-3 border-t border-[#E7ECF2] pt-5 sm:flex-row sm:items-center sm:justify-between">
                <button
                  type="button"
                  onClick={deleteApplication}
                  className="inline-flex items-center justify-center gap-2 rounded-lg border border-[#F2B8B5] px-5 py-3 text-sm font-extrabold text-[#D92D20] hover:bg-[#FFF5F5]"
                >
                  <Trash2 size={17} />
                  지원서 삭제
                </button>
                <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
                  <button type="button" onClick={closeApplicationEdit} className="rounded-lg border border-[#D7DDE5] px-5 py-3 text-sm font-extrabold text-[#344054] hover:bg-[#F8FAFC]">
                    취소
                  </button>
                  <button type="button" onClick={saveApplicationEdit} className="inline-flex items-center justify-center gap-2 rounded-lg bg-[#0D6BEA] px-6 py-3 text-sm font-extrabold text-white hover:bg-[#0959C7]">
                    <Save size={17} />
                    수정 저장
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {isPreviewOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
            <div className="flex items-start justify-between gap-4">
              <div className="flex min-w-0 items-center gap-3">
                {renderProfileAvatar('flex h-14 w-14 shrink-0 items-center justify-center text-xl')}
                <div className="min-w-0">
                  <p className="text-sm font-bold text-[#0D6BEA]">프로필 미리보기</p>
                  <h2 className="mt-1 truncate text-2xl font-extrabold">{profileForm.name}</h2>
                  <p className="mt-1 truncate text-sm font-semibold text-[#7A8495]">{profileForm.desiredJob || '희망 직무 미입력'}</p>
                </div>
              </div>
              <button type="button" onClick={() => setIsPreviewOpen(false)} className="rounded-lg border border-[#D7DDE5] px-3 py-2 text-sm font-bold">
                닫기
              </button>
            </div>
            <div className="mt-5 grid gap-3 text-sm">
              <p><b>성별</b> {genderLabels[profileForm.gender] || profileForm.gender}</p>
              <p><b>전화번호</b> {profileForm.phone}</p>
              <p><b>이메일</b> {profileForm.email || '미입력'}</p>
              <p><b>현재 거주지역</b> {profileForm.residenceRegion}</p>
              <p><b>희망 근무지역</b> {profileForm.desiredRegion}</p>
              <p><b>경력</b> {careerTypeOptions.find(option => option.value === profileForm.careerType)?.label} / {profileForm.careerYears || 0}년</p>
              <p><b>희망 최소 연봉</b> {profileForm.minSalary ? `${profileForm.minSalary}만원 이상` : '미입력'}</p>
              <p><b>근무 방식</b> {selectedWorkTypes.length > 0 ? selectedWorkTypes.join(', ') : '미선택'}</p>
              <p><b>연락 가능 시간</b> {profileForm.contactTimeStart} ~ {profileForm.contactTimeEnd}</p>
              <p><b>선호 연락 방식</b> {contactMethodOptions.find(option => option.value === profileForm.contactMethod)?.label}</p>
              <p><b>공개 여부</b> {profileForm.profilePublic ? '공개' : '비공개'}</p>
              <p className="whitespace-pre-line"><b>자기소개</b><br />{profileForm.introduction || '미입력'}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
