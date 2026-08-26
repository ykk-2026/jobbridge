import { useEffect, useRef, useState } from 'react';
import type { ChangeEvent } from 'react';
import {
  Bell,
  Bookmark,
  Briefcase,
  Calendar,
  ChevronDown,
  Clock,
  FileText,
  Lock,
  Mail,
  Phone,
  Plus,
  ShieldCheck,
  X,
  UserRound,
} from 'lucide-react';
import type { CurrentUser } from '@/app/types';
import { demoMemberId, getProfile, saveProfile } from '@/app/api/profileApi';

interface ProfilePageProps {
  currentUser: CurrentUser | null;
}

const menuItems = [
  { label: '내 프로필', icon: UserRound },
  { label: '이력서 관리', icon: FileText },
  { label: '지원 현황', icon: Briefcase },
  { label: '관심 공고', icon: Bookmark },
  { label: 'AI 추천 결과', icon: Bell },
  { label: '알림 설정', icon: Bell },
  { label: '계정 설정', icon: Lock },
];

const workTypeOptions = ['재택근무', '유연근무', '하이브리드 근무', '출퇴근 근무'];
const maxIntroLength = 500;

const genderToCode: Record<string, string> = {
  남성: 'MALE',
  여성: 'FEMALE',
  기타: 'OTHER',
};
const genderToLabel: Record<string, string> = {
  MALE: '남성',
  FEMALE: '여성',
  OTHER: '기타',
};
const employmentTypeToCode: Record<string, string> = {
  정규직: 'FULL_TIME',
  아르바이트: 'PART_TIME',
  계약직: 'CONTRACT',
  인턴: 'INTERNSHIP',
  프리랜서: 'FREELANCER',
  무관: 'ANY',
};
const employmentTypeToLabel: Record<string, string> = {
  FULL_TIME: '정규직',
  PART_TIME: '아르바이트',
  CONTRACT: '계약직',
  INTERNSHIP: '인턴',
  FREELANCER: '프리랜서',
  ANY: '무관',
};
const careerTypeToCode: Record<string, string> = {
  신입: 'ENTRY',
  경력: 'EXPERIENCED',
  무관: 'ANY',
};
const careerTypeToLabel: Record<string, string> = {
  ENTRY: '신입',
  EXPERIENCED: '경력',
  ANY: '무관',
};
const contactMethodToCode: Record<string, string> = {
  전화: 'PHONE',
  이메일: 'EMAIL',
  문자: 'SMS',
  카카오톡: 'KAKAO',
};
const contactMethodToLabel: Record<string, string> = {
  PHONE: '전화',
  EMAIL: '이메일',
  SMS: '문자',
  KAKAO: '카카오톡',
};

const resumeItems = [
  { title: '기본 이력서', updatedAt: '2026-08-01', status: '대표 이력서', completeness: '92%' },
  { title: '사무보조 지원용 이력서', updatedAt: '2026-07-22', status: '임시 저장', completeness: '76%' },
];

const applicationItems = [
  { company: '네오서비스', title: '고객지원 사무보조', status: '서류 검토중', date: '2026-08-02' },
  { company: '그린테크', title: '웹 운영 보조', status: '면접 제안', date: '2026-07-29' },
  { company: '서울케어', title: '데이터 입력 담당', status: '지원 완료', date: '2026-07-25' },
];

const savedJobItems = [
  { company: '브릿지랩', title: 'React 프론트엔드 보조', deadline: 'D-5', match: '96%' },
  { company: '위드워크', title: '장애인 채용 사무직', deadline: 'D-8', match: '93%' },
  { company: '케어링크', title: '고객 상담 매니저', deadline: 'D-12', match: '90%' },
];

const aiResultItems = [
  { title: '웹 서비스 운영 보조', reason: 'React 경험과 재택근무 조건이 잘 맞습니다.', score: '98%' },
  { title: '데이터 입력 담당', reason: '꼼꼼한 문서 처리와 접근성 조건이 적합합니다.', score: '94%' },
  { title: '고객지원 사무직', reason: '연락 가능 시간과 희망 지역이 일치합니다.', score: '91%' },
];

export function ProfilePage({ currentUser }: ProfilePageProps) {
  const memberId = Number(currentUser?.id) || demoMemberId;
  const initialName = currentUser?.name || '김민준';
  const initialEmail = currentUser?.email || 'minjun.kim@example.com';
  const initialBirthDate = currentUser?.birthDate || '1998-05-23';
  const initialGender = currentUser?.gender || '남성';
  const initialPreferredRole = currentUser?.preferredRole || '백엔드 개발자';
  const initialAvatar = currentUser?.avatar || initialName.slice(0, 1);

  const [activeMenu, setActiveMenu] = useState('내 프로필');
  const [savedMessage, setSavedMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [isPreviewOpen, setIsPreviewOpen] = useState(false);
  const [photoPreview, setPhotoPreview] = useState('');
  const [resumeStatus, setResumeStatus] = useState('대표 이력서가 설정되어 있습니다.');
  const [notificationSettings, setNotificationSettings] = useState({
    deadline: true,
    recommendation: true,
    application: true,
  });
  const [accountForm, setAccountForm] = useState({
    loginId: currentUser?.loginId || currentUser?.id || 'minjun_kim',
    password: '',
    newPassword: '',
  });
  const [profileForm, setProfileForm] = useState({
    name: initialName,
    birthDate: initialBirthDate,
    gender: initialGender,
    email: initialEmail,
    phone: '010-1234-5678',
    currentRegion: '서울특별시 송파구',
    preferredRole: initialPreferredRole,
    preferredRegion: '서울 전체',
    employmentType: '정규직',
    careerType: '경력',
    careerYears: '3',
    salary: '4,000',
    contactStart: '09:00',
    contactEnd: '18:00',
    contactMethod: '이메일',
    introduction:
      '새로운 기술을 배우는 것을 좋아하고,\n문제를 해결하며 성장하는 개발자가 되고 싶습니다.\n함께 성장할 수 있는 일을 찾고 있습니다.',
    openStatus: '공개',
    workTypes: ['재택근무', '유연근무', '하이브리드 근무'],
    jobSummary: 'Spring Boot와 React 기반 웹 서비스 개발 경험 3년',
    skills: ['JavaScript', 'React', 'Spring Boot', 'MySQL'],
    skillInput: '',
    certifications: '정보처리기사, SQLD',
    availableDate: '2026-08-12',
    finalEducation: '대학교 졸업',
    disabilitySupport: '휠체어 접근 가능한 사무공간, 높낮이 조절 책상 필요',
  });
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const avatar = photoPreview || initialAvatar;

  useEffect(() => {
    let active = true;

    getProfile(memberId)
      .then(profile => {
        if (!active) return;

        const workTypes = [
          profile.remotePreferred && '재택근무',
          profile.flexiblePreferred && '유연근무',
          profile.hybridPreferred && '하이브리드 근무',
          profile.onsitePreferred && '출퇴근 근무',
        ].filter((item): item is string => Boolean(item));

        setProfileForm(previous => ({
          ...previous,
          name: profile.name || previous.name,
          birthDate: profile.birthDate || previous.birthDate,
          gender: genderToLabel[profile.gender || ''] || profile.gender || previous.gender,
          email: profile.email || previous.email,
          phone: profile.phone || previous.phone,
          currentRegion: profile.residenceRegion || previous.currentRegion,
          preferredRole: profile.desiredJob || previous.preferredRole,
          preferredRegion: profile.desiredRegion || previous.preferredRegion,
          employmentType: employmentTypeToLabel[profile.employmentType || ''] || profile.employmentType || previous.employmentType,
          careerType: careerTypeToLabel[profile.careerType || ''] || profile.careerType || previous.careerType,
          careerYears: profile.careerYears?.toString() || previous.careerYears,
          salary: profile.minSalary?.toLocaleString('ko-KR') || previous.salary,
          contactStart: profile.contactTimeStart?.slice(0, 5) || previous.contactStart,
          contactEnd: profile.contactTimeEnd?.slice(0, 5) || previous.contactEnd,
          contactMethod: contactMethodToLabel[profile.contactMethod || ''] || profile.contactMethod || previous.contactMethod,
          introduction: profile.introduction || previous.introduction,
          openStatus: profile.profilePublic ? '공개' : '비공개',
          workTypes,
        }));
        setSavedMessage('Spring Boot에서 프로필을 불러왔습니다.');
      })
      .catch(error => {
        if (active) {
          setSavedMessage(`백엔드 연결 실패: ${error instanceof Error ? error.message : '알 수 없는 오류'}`);
        }
      });

    return () => {
      active = false;
    };
  }, [memberId]);

  const updateForm = (field: keyof typeof profileForm, value: string | string[]) => {
    setProfileForm(prev => ({ ...prev, [field]: value }));
    setSavedMessage('');
  };

  const handleMenuClick = (label: string) => {
    setActiveMenu(label);
    setSavedMessage(`${label} 메뉴를 선택했습니다.`);
  };

  const handleSave = async () => {
    setIsSaving(true);
    setSavedMessage('프로필을 저장하고 있습니다...');

    try {
      await saveProfile(memberId, {
        name: profileForm.name,
        birthDate: profileForm.birthDate || null,
        gender: genderToCode[profileForm.gender] || profileForm.gender,
        email: profileForm.email,
        phone: profileForm.phone,
        profileImageUrl: null,
        residenceRegion: profileForm.currentRegion,
        desiredJob: profileForm.preferredRole,
        desiredRegion: profileForm.preferredRegion,
        employmentType: employmentTypeToCode[profileForm.employmentType] || profileForm.employmentType,
        careerType: careerTypeToCode[profileForm.careerType] || profileForm.careerType,
        careerYears: Number.parseInt(profileForm.careerYears, 10) || 0,
        minSalary: Number.parseInt(profileForm.salary.replace(/[^0-9]/g, ''), 10) || 0,
        remotePreferred: profileForm.workTypes.includes('재택근무'),
        flexiblePreferred: profileForm.workTypes.includes('유연근무'),
        hybridPreferred: profileForm.workTypes.includes('하이브리드 근무'),
        onsitePreferred: profileForm.workTypes.includes('출퇴근 근무'),
        contactTimeStart: profileForm.contactStart || null,
        contactTimeEnd: profileForm.contactEnd || null,
        contactMethod: contactMethodToCode[profileForm.contactMethod] || profileForm.contactMethod,
        introduction: profileForm.introduction,
        profilePublic: profileForm.openStatus === '공개',
      });
      setSavedMessage('Spring Boot와 MariaDB에 프로필을 저장했습니다.');
      window.alert('프로필 정보가 저장되었습니다.');
    } catch (error) {
      setSavedMessage(`저장 실패: ${error instanceof Error ? error.message : '알 수 없는 오류'}`);
    } finally {
      setIsSaving(false);
    }
  };

  const handleConsult = () => {
    window.alert('상담 신청이 접수되었습니다. 담당자가 확인 후 연락드립니다.');
  };

  const handlePhotoChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
      window.alert('2MB 이하의 이미지만 등록할 수 있습니다.');
      event.target.value = '';
      return;
    }

    setPhotoPreview(URL.createObjectURL(file));
    setSavedMessage('프로필 사진이 변경되었습니다. 저장하기를 눌러 반영해 주세요.');
  };

  const handleWorkTypeToggle = (workType: string) => {
    setProfileForm(prev => {
      const checked = prev.workTypes.includes(workType);
      return {
        ...prev,
        workTypes: checked ? prev.workTypes.filter(item => item !== workType) : [...prev.workTypes, workType],
      };
    });
    setSavedMessage('');
  };

  const handleIntroChange = (value: string) => {
    updateForm('introduction', value.slice(0, maxIntroLength));
  };

  const handleAddSkill = () => {
    const nextSkill = profileForm.skillInput.trim();
    if (!nextSkill) return;
    if (profileForm.skills.includes(nextSkill)) {
      window.alert('이미 등록된 기술입니다.');
      return;
    }

    setProfileForm(prev => ({
      ...prev,
      skills: [...prev.skills, nextSkill],
      skillInput: '',
    }));
    setSavedMessage('');
  };

  const handleRemoveSkill = (skill: string) => {
    setProfileForm(prev => ({
      ...prev,
      skills: prev.skills.filter(item => item !== skill),
    }));
    setSavedMessage('');
  };

  const handleResumeAction = (message: string) => {
    setResumeStatus(message);
    setSavedMessage(message);
  };

  const handleApplyAction = (message: string) => {
    window.alert(message);
    setSavedMessage(message);
  };

  const updateNotification = (field: keyof typeof notificationSettings) => {
    setNotificationSettings(prev => ({ ...prev, [field]: !prev[field] }));
    setSavedMessage('알림 설정이 변경되었습니다.');
  };

  const updateAccountForm = (field: keyof typeof accountForm, value: string) => {
    setAccountForm(prev => ({ ...prev, [field]: value }));
    setSavedMessage('');
  };

  const handleAccountSave = () => {
    setSavedMessage('계정 설정이 저장되었습니다.');
    window.alert('계정 설정이 저장되었습니다.');
  };

  const renderMenuPanel = () => {
    switch (activeMenu) {
      case '이력서 관리':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-5">
              <div>
                <h2 className="font-bold text-foreground">이력서 관리</h2>
                <p className="text-sm text-muted-foreground mt-1">{resumeStatus}</p>
              </div>
              <button
                type="button"
                className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/90"
                onClick={() => handleResumeAction('새 이력서 작성 화면을 열었습니다.')}
              >
                새 이력서 작성
              </button>
            </div>
            <div className="space-y-3">
              {resumeItems.map(item => (
                <div key={item.title} className="rounded-lg border border-[#E2E8F0] p-4">
                  <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
                    <div>
                      <p className="font-semibold text-foreground">{item.title}</p>
                      <p className="text-sm text-muted-foreground mt-1">완성도 {item.completeness} · 최근 수정 {item.updatedAt}</p>
                      <span className="mt-2 inline-block rounded-full bg-[#EAF4FF] px-3 py-1 text-xs font-semibold text-primary">{item.status}</span>
                    </div>
                    <div className="flex gap-2">
                      <button type="button" className="rounded-lg border border-[#DCEAF3] px-3 py-2 text-sm font-semibold text-primary" onClick={() => handleResumeAction(`${item.title}을 수정합니다.`)}>수정</button>
                      <button type="button" className="rounded-lg border border-[#DCEAF3] px-3 py-2 text-sm font-semibold text-muted-foreground" onClick={() => handleResumeAction(`${item.title} 미리보기를 열었습니다.`)}>미리보기</button>
                      <button type="button" className="rounded-lg border border-[#DCEAF3] px-3 py-2 text-sm font-semibold text-muted-foreground" onClick={() => handleResumeAction(`${item.title}을 PDF로 저장했습니다.`)}>PDF</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>
        );

      case '지원 현황':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <h2 className="font-bold text-foreground mb-5">지원 현황</h2>
            <div className="grid sm:grid-cols-3 gap-3 mb-5">
              {['전체 3건', '면접 제안 1건', '서류 검토중 1건'].map(item => (
                <div key={item} className="rounded-lg bg-[#F7FAFC] p-4 text-sm font-semibold text-foreground">{item}</div>
              ))}
            </div>
            <div className="space-y-3">
              {applicationItems.map(item => (
                <div key={`${item.company}-${item.title}`} className="rounded-lg border border-[#E2E8F0] p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                  <div>
                    <p className="font-semibold text-foreground">{item.title}</p>
                    <p className="text-sm text-muted-foreground mt-1">{item.company} · 지원일 {item.date}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="rounded-full bg-[#EAF4FF] px-3 py-1 text-xs font-semibold text-primary">{item.status}</span>
                    <button type="button" className="rounded-lg border border-[#DCEAF3] px-3 py-2 text-sm font-semibold text-primary" onClick={() => handleApplyAction(`${item.company} 지원 상세를 확인합니다.`)}>상세보기</button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        );

      case '관심 공고':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <h2 className="font-bold text-foreground mb-5">관심 공고</h2>
            <div className="grid md:grid-cols-3 gap-3">
              {savedJobItems.map(item => (
                <div key={`${item.company}-${item.title}`} className="rounded-lg border border-[#E2E8F0] p-4">
                  <p className="text-sm text-muted-foreground">{item.company}</p>
                  <p className="font-semibold text-foreground mt-1">{item.title}</p>
                  <div className="mt-3 flex items-center justify-between text-sm">
                    <span className="font-semibold text-primary">AI {item.match}</span>
                    <span className="text-muted-foreground">{item.deadline}</span>
                  </div>
                  <button type="button" className="mt-4 w-full rounded-lg bg-primary px-3 py-2 text-sm font-semibold text-white" onClick={() => handleApplyAction(`${item.title} 공고를 확인합니다.`)}>공고 보기</button>
                </div>
              ))}
            </div>
          </section>
        );

      case 'AI 추천 결과':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-5">
              <div>
                <h2 className="font-bold text-foreground">AI 추천 결과</h2>
                <p className="text-sm text-muted-foreground mt-1">현재 프로필 기준 추천 만족도 98%</p>
              </div>
              <button type="button" className="rounded-lg border border-primary px-4 py-2 text-sm font-semibold text-primary" onClick={() => handleApplyAction('AI 추천 결과를 새로 계산했습니다.')}>다시 추천받기</button>
            </div>
            <div className="space-y-3">
              {aiResultItems.map(item => (
                <div key={item.title} className="rounded-lg border border-[#E2E8F0] p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="font-semibold text-foreground">{item.title}</p>
                      <p className="text-sm text-muted-foreground mt-1">{item.reason}</p>
                    </div>
                    <span className="rounded-full bg-[#EAF4FF] px-3 py-1 text-sm font-bold text-primary">{item.score}</span>
                  </div>
                  <button type="button" className="mt-4 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white" onClick={() => handleApplyAction(`${item.title} 추천 공고로 이동합니다.`)}>추천 공고 보기</button>
                </div>
              ))}
            </div>
          </section>
        );

      case '알림 설정':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <h2 className="font-bold text-foreground mb-5">알림 설정</h2>
            <div className="space-y-3">
              {[
                { key: 'deadline' as const, title: '마감 임박 공고 알림', desc: '관심 공고의 마감일이 가까워지면 알려드립니다.' },
                { key: 'recommendation' as const, title: 'AI 추천 공고 알림', desc: '프로필에 맞는 새 공고가 올라오면 알려드립니다.' },
                { key: 'application' as const, title: '지원 상태 변경 알림', desc: '서류 검토, 면접 제안 등 지원 상태 변경을 알려드립니다.' },
              ].map(item => (
                <div key={item.key} className="rounded-lg border border-[#E2E8F0] p-4 flex items-center justify-between gap-4">
                  <div>
                    <p className="font-semibold text-foreground">{item.title}</p>
                    <p className="text-sm text-muted-foreground mt-1">{item.desc}</p>
                  </div>
                  <button
                    type="button"
                    className={`w-14 rounded-full px-1 py-1 text-xs font-bold ${notificationSettings[item.key] ? 'bg-primary text-white' : 'bg-[#E2E8F0] text-muted-foreground'}`}
                    onClick={() => updateNotification(item.key)}
                  >
                    {notificationSettings[item.key] ? 'ON' : 'OFF'}
                  </button>
                </div>
              ))}
            </div>
          </section>
        );

      case '계정 설정':
        return (
          <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
            <h2 className="font-bold text-foreground mb-5">계정 설정</h2>
            <div className="grid sm:grid-cols-2 gap-4">
              <label className="block">
                <span className="block text-sm font-medium mb-2">로그인 아이디</span>
                <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.loginId} onChange={event => updateAccountForm('loginId', event.target.value)} />
              </label>
              <label className="block">
                <span className="block text-sm font-medium mb-2">현재 비밀번호</span>
                <input type="password" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.password} onChange={event => updateAccountForm('password', event.target.value)} />
              </label>
              <label className="block sm:col-span-2">
                <span className="block text-sm font-medium mb-2">새 비밀번호</span>
                <input type="password" className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={accountForm.newPassword} onChange={event => updateAccountForm('newPassword', event.target.value)} />
              </label>
            </div>
            <button type="button" className="mt-5 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white" onClick={handleAccountSave}>계정 정보 저장</button>
          </section>
        );

      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-[#F5F7FB]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="grid lg:grid-cols-[220px_1fr] gap-6">
          <aside className="hidden lg:flex flex-col gap-5">
            <div className="bg-white border border-[#E2E8F0] rounded-xl p-4">
              <h2 className="text-sm font-bold text-foreground mb-3">마이페이지</h2>
              <nav className="space-y-1">
                {menuItems.map(item => {
                  const Icon = item.icon;
                  return (
                    <button
                      type="button"
                      key={item.label}
                      className={`w-full flex items-center gap-2 rounded-lg px-3 py-2 text-sm ${
                        activeMenu === item.label ? 'bg-[#EAF4FF] text-primary font-semibold' : 'text-muted-foreground hover:bg-muted'
                      }`}
                      onClick={() => handleMenuClick(item.label)}
                    >
                      <Icon size={15} />
                      {item.label}
                    </button>
                  );
                })}
              </nav>
            </div>

            <div className="bg-white border border-[#E2E8F0] rounded-xl p-4">
              <p className="text-sm font-semibold text-foreground">도움이 필요하신가요?</p>
              <p className="text-xs text-muted-foreground mt-2 leading-5">프로필 작성과 접근성 조건 설정을 도와드립니다.</p>
              <button
                type="button"
                className="mt-4 w-full rounded-lg border border-[#CFE3F3] py-2 text-sm font-semibold text-primary hover:bg-[#F7FAFC]"
                onClick={handleConsult}
              >
                상담 신청하기
              </button>
            </div>
          </aside>

          <main>
            <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4 mb-5">
              <div>
                <p className="text-sm text-muted-foreground">입력한 정보를 바탕으로 맞춤 채용을 추천합니다.</p>
                <h1 className="text-2xl font-bold text-foreground mt-1">{activeMenu}</h1>
              </div>
              {activeMenu === '내 프로필' && (
              <div className="flex gap-2">
                <button
                  type="button"
                  className="rounded-lg border border-primary px-4 py-2 text-sm font-semibold text-primary hover:bg-primary/5"
                  onClick={() => setIsPreviewOpen(true)}
                >
                  미리보기
                </button>
                <button
                  type="button"
                  className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/90"
                  onClick={handleSave}
                  disabled={isSaving}
                >
                  {isSaving ? '저장 중...' : '저장하기'}
                </button>
              </div>
              )}
            </div>

            {savedMessage && (
              <div className="mb-5 rounded-lg border border-[#CFE3F3] bg-[#F7FAFC] px-4 py-3 text-sm font-semibold text-primary">
                {savedMessage}
              </div>
            )}

            {activeMenu === '내 프로필' ? (
              <>
            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <h2 className="font-bold text-foreground mb-5">기본 정보</h2>
              <div className="grid md:grid-cols-[150px_1fr] gap-6">
                <div className="flex flex-col items-center">
                  <div className="w-24 h-24 rounded-full bg-[#E5EAF0] text-primary flex items-center justify-center text-3xl font-bold overflow-hidden">
                    {photoPreview ? (
                      <img src={photoPreview} alt="프로필 사진 미리보기" className="h-full w-full object-cover" />
                    ) : (
                      avatar
                    )}
                  </div>
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/png,image/jpeg"
                    className="hidden"
                    onChange={handlePhotoChange}
                  />
                  <button
                    type="button"
                    className="mt-3 rounded-lg border border-[#DCEAF3] px-4 py-2 text-sm font-semibold text-primary hover:bg-[#F7FAFC]"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    사진 변경
                  </button>
                  <p className="text-xs text-muted-foreground mt-2">JPG, PNG 2MB 이하</p>
                </div>

                <div className="grid sm:grid-cols-2 gap-4">
                  <label className="block">
                    <span className="block text-sm font-medium mb-2">이름</span>
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.name} onChange={event => updateForm('name', event.target.value)} />
                  </label>

                  <label className="block">
                    <span className="block text-sm font-medium mb-2">생년월일</span>
                    <div className="relative">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.birthDate} onChange={event => updateForm('birthDate', event.target.value)} />
                      <Calendar size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </label>

                  <label className="block">
                    <span className="block text-sm font-medium mb-2">성별</span>
                    <div className="relative">
                      <select className="w-full appearance-none rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 pr-10 text-sm" value={profileForm.gender} onChange={event => updateForm('gender', event.target.value)}>
                        <option>남성</option>
                        <option>여성</option>
                        <option>선택 안 함</option>
                      </select>
                      <ChevronDown size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </label>

                  <label className="block">
                    <span className="block text-sm font-medium mb-2">이메일</span>
                    <div className="relative">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.email} onChange={event => updateForm('email', event.target.value)} />
                      <Mail size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </label>

                  <label className="block">
                    <span className="block text-sm font-medium mb-2">전화번호</span>
                    <div className="relative">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.phone} onChange={event => updateForm('phone', event.target.value)} />
                      <Phone size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </label>

                  <label className="block">
                    <span className="block text-sm font-medium mb-2">현재 거주지역</span>
                    <div className="relative">
                      <select className="w-full appearance-none rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 pr-10 text-sm" value={profileForm.currentRegion} onChange={event => updateForm('currentRegion', event.target.value)}>
                        <option>서울특별시 송파구</option>
                        <option>서울특별시 강남구</option>
                        <option>경기도 성남시</option>
                        <option>인천광역시</option>
                      </select>
                      <ChevronDown size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </label>
                </div>
              </div>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <h2 className="font-bold text-foreground mb-5">희망 근무 조건</h2>
              <div className="grid sm:grid-cols-3 gap-4">
                <label className="block">
                  <span className="block text-sm font-medium mb-2">희망 직무</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.preferredRole} onChange={event => updateForm('preferredRole', event.target.value)}>
                    <option>백엔드 개발자</option>
                    <option>프론트엔드 개발자</option>
                    <option>데이터 분석가</option>
                    <option>서비스 기획자</option>
                  </select>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">희망 근무지역</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.preferredRegion} onChange={event => updateForm('preferredRegion', event.target.value)}>
                    <option>서울 전체</option>
                    <option>경기 전체</option>
                    <option>인천 전체</option>
                    <option>전국</option>
                  </select>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">희망 고용 형태</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.employmentType} onChange={event => updateForm('employmentType', event.target.value)}>
                    <option>정규직</option>
                    <option>계약직</option>
                    <option>인턴</option>
                    <option>아르바이트</option>
                  </select>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">경력 구분</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.careerType} onChange={event => updateForm('careerType', event.target.value)}>
                    <option>신입</option>
                    <option>경력</option>
                    <option>무관</option>
                  </select>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">경력 연수</span>
                  <div className="flex items-center gap-2">
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.careerYears} onChange={event => updateForm('careerYears', event.target.value)} />
                    <span className="text-sm text-muted-foreground">년</span>
                  </div>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">희망 연봉</span>
                  <div className="flex items-center gap-2">
                    <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm" value={profileForm.salary} onChange={event => updateForm('salary', event.target.value)} />
                    <span className="text-sm text-muted-foreground whitespace-nowrap">만원 이상</span>
                  </div>
                </label>
              </div>

              <div className="mt-5">
                <p className="text-sm font-medium mb-3">근무 형태</p>
                <div className="flex flex-wrap gap-4 text-sm">
                  {workTypeOptions.map(item => (
                    <label key={item} className="inline-flex items-center gap-2 text-muted-foreground">
                      <input type="checkbox" checked={profileForm.workTypes.includes(item)} onChange={() => handleWorkTypeToggle(item)} />
                      {item}
                    </label>
                  ))}
                </div>
              </div>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <div className="mb-5">
                <h2 className="font-bold text-foreground">기업 확인 정보</h2>
                <p className="text-sm text-muted-foreground mt-1">기업이 서류 검토 전에 꼭 확인하는 핵심 정보를 정리해 주세요.</p>
              </div>

              <div className="grid sm:grid-cols-2 gap-4">
                <label className="block sm:col-span-2">
                  <span className="block text-sm font-medium mb-2">핵심 경력 요약</span>
                  <input
                    className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                    value={profileForm.jobSummary}
                    onChange={event => updateForm('jobSummary', event.target.value)}
                  />
                </label>

                <div className="block sm:col-span-2">
                  <span className="block text-sm font-medium mb-2">보유 기술</span>
                  <div className="flex gap-2">
                    <input
                      className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                      placeholder="예: Java, React, 포토샵, 고객 응대"
                      value={profileForm.skillInput}
                      onChange={event => updateForm('skillInput', event.target.value)}
                      onKeyDown={event => {
                        if (event.key === 'Enter') {
                          event.preventDefault();
                          handleAddSkill();
                        }
                      }}
                    />
                    <button
                      type="button"
                      className="shrink-0 inline-flex items-center gap-1 rounded-lg bg-primary px-4 py-3 text-sm font-semibold text-white hover:bg-primary/90"
                      onClick={handleAddSkill}
                    >
                      <Plus size={16} />
                      추가
                    </button>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-2">
                    {profileForm.skills.map(skill => (
                      <button
                        type="button"
                        key={skill}
                        className="inline-flex items-center gap-1 rounded-full border border-[#BFD8EA] bg-[#F5FAFF] px-3 py-2 text-sm font-semibold text-[#22618C]"
                        onClick={() => handleRemoveSkill(skill)}
                      >
                        {skill}
                        <X size={14} />
                      </button>
                    ))}
                  </div>
                </div>

                <label className="block">
                  <span className="block text-sm font-medium mb-2">자격증</span>
                  <input
                    className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                    value={profileForm.certifications}
                    onChange={event => updateForm('certifications', event.target.value)}
                  />
                </label>

                <label className="block">
                  <span className="block text-sm font-medium mb-2">출근 가능일</span>
                  <input
                    className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                    value={profileForm.availableDate}
                    onChange={event => updateForm('availableDate', event.target.value)}
                  />
                </label>

                <label className="block">
                  <span className="block text-sm font-medium mb-2">최종 학력</span>
                  <select
                    className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm"
                    value={profileForm.finalEducation}
                    onChange={event => updateForm('finalEducation', event.target.value)}
                  >
                    <option>고등학교 졸업</option>
                    <option>전문대 졸업</option>
                    <option>대학교 졸업</option>
                    <option>대학원 졸업</option>
                    <option>학력 무관</option>
                  </select>
                </label>

                <label className="block sm:col-span-2">
                  <span className="block text-sm font-medium mb-2">업무 환경 지원 필요사항</span>
                  <textarea
                    className="min-h-24 w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                    value={profileForm.disabilitySupport}
                    onChange={event => updateForm('disabilitySupport', event.target.value)}
                  />
                </label>
              </div>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <h2 className="font-bold text-foreground mb-5">연락 가능 설정</h2>
              <div className="grid sm:grid-cols-2 gap-4">
                <label className="block">
                  <span className="block text-sm font-medium mb-2">연락 가능 시간대</span>
                  <div className="flex gap-2">
                    <div className="relative flex-1">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.contactStart} onChange={event => updateForm('contactStart', event.target.value)} />
                      <Clock size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                    <div className="relative flex-1">
                      <input className="w-full rounded-lg border border-[#DCEAF3] px-3 py-3 pr-10 text-sm" value={profileForm.contactEnd} onChange={event => updateForm('contactEnd', event.target.value)} />
                      <Clock size={16} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                    </div>
                  </div>
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">선호 연락 방식</span>
                  <select className="w-full rounded-lg border border-[#DCEAF3] bg-white px-3 py-3 text-sm" value={profileForm.contactMethod} onChange={event => updateForm('contactMethod', event.target.value)}>
                    <option>이메일</option>
                    <option>문자</option>
                    <option>전화</option>
                  </select>
                </label>
              </div>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <h2 className="font-bold text-foreground mb-4">자기소개</h2>
              <textarea
                className="min-h-32 w-full rounded-lg border border-[#DCEAF3] px-3 py-3 text-sm"
                value={profileForm.introduction}
                maxLength={maxIntroLength}
                onChange={event => handleIntroChange(event.target.value)}
              />
              <p className="mt-2 text-right text-xs text-muted-foreground">
                {profileForm.introduction.length} / {maxIntroLength}자
              </p>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5 mb-5">
              <h2 className="font-bold text-foreground mb-4">공개 설정</h2>
              <div className="grid md:grid-cols-[1fr_1fr] gap-4 items-center">
                <div className="space-y-2 text-sm">
                  {['공개', '비공개'].map(status => (
                    <label key={status} className={`flex items-center gap-2 ${status === '비공개' ? 'text-muted-foreground' : ''}`}>
                      <input
                        name="openStatus"
                        type="radio"
                        checked={profileForm.openStatus === status}
                        onChange={() => updateForm('openStatus', status)}
                      />
                      {status}
                    </label>
                  ))}
                </div>
                <div className="rounded-lg bg-[#F7FAFC] px-4 py-3 text-sm text-muted-foreground flex items-start gap-3">
                  <ShieldCheck size={18} className="text-primary mt-0.5" />
                  <p>공개 설정 시 기업이 프로필을 확인하고 면접 제안을 보낼 수 있습니다.</p>
                </div>
              </div>
            </section>

            <section className="bg-white border border-[#E2E8F0] rounded-xl p-5">
              <h2 className="font-bold text-foreground mb-4">계정 정보</h2>
              <div className="grid sm:grid-cols-3 gap-3 text-sm">
                <div className="rounded-lg bg-[#F7FAFC] p-4">
                  <p className="text-muted-foreground">계정 상태</p>
                  <p className="font-semibold text-[#14A38B] mt-1">활성</p>
                </div>
                <div className="rounded-lg bg-[#F7FAFC] p-4">
                  <p className="text-muted-foreground">가입일</p>
                  <p className="font-semibold text-foreground mt-1">2024-05-10</p>
                </div>
                <div className="rounded-lg bg-[#F7FAFC] p-4">
                  <p className="text-muted-foreground">마지막 로그인</p>
                  <p className="font-semibold text-foreground mt-1">2026-08-01 14:30</p>
                </div>
              </div>
            </section>
              </>
            ) : (
              renderMenuPanel()
            )}
          </main>
        </div>
      </div>

      {isPreviewOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-sm text-muted-foreground">기업에게 보이는 프로필</p>
                <h2 className="text-xl font-bold text-foreground mt-1">{profileForm.name}</h2>
              </div>
              <button
                type="button"
                className="rounded-lg border border-[#DCEAF3] px-3 py-2 text-sm font-semibold text-muted-foreground hover:bg-[#F7FAFC]"
                onClick={() => setIsPreviewOpen(false)}
              >
                닫기
              </button>
            </div>

            <div className="mt-5 flex items-center gap-4">
              <div className="h-16 w-16 rounded-full bg-[#E5EAF0] text-primary flex items-center justify-center text-2xl font-bold overflow-hidden">
                {photoPreview ? (
                  <img src={photoPreview} alt="프로필 사진" className="h-full w-full object-cover" />
                ) : (
                  initialAvatar
                )}
              </div>
              <div>
                <p className="font-semibold text-foreground">{profileForm.preferredRole}</p>
                <p className="text-sm text-muted-foreground">{profileForm.email}</p>
              </div>
            </div>

            <div className="mt-5 grid grid-cols-2 gap-3 text-sm">
              <div className="rounded-lg bg-[#F7FAFC] p-3">
                <p className="text-muted-foreground">생년월일</p>
                <p className="font-semibold text-foreground mt-1">{profileForm.birthDate}</p>
              </div>
              <div className="rounded-lg bg-[#F7FAFC] p-3">
                <p className="text-muted-foreground">성별</p>
                <p className="font-semibold text-foreground mt-1">{profileForm.gender}</p>
              </div>
              <div className="rounded-lg bg-[#F7FAFC] p-3">
                <p className="text-muted-foreground">희망 지역</p>
                <p className="font-semibold text-foreground mt-1">{profileForm.preferredRegion}</p>
              </div>
              <div className="rounded-lg bg-[#F7FAFC] p-3">
                <p className="text-muted-foreground">공개 상태</p>
                <p className="font-semibold text-[#14A38B] mt-1">{profileForm.openStatus}</p>
              </div>
            </div>

            <div className="mt-5 rounded-lg bg-[#F7FAFC] p-4 text-sm">
              <p className="font-semibold text-foreground">{profileForm.jobSummary}</p>
              <div className="mt-3 flex flex-wrap gap-2">
                {profileForm.skills.map(skill => (
                  <span key={skill} className="rounded-full bg-white border border-[#DCEAF3] px-3 py-1 text-xs font-semibold text-primary">
                    {skill}
                  </span>
                ))}
              </div>
              <div className="mt-3 grid grid-cols-2 gap-3">
                <div>
                  <p className="text-muted-foreground">출근 가능일</p>
                  <p className="font-semibold text-foreground mt-1">{profileForm.availableDate}</p>
                </div>
                <div>
                  <p className="text-muted-foreground">최종 학력</p>
                  <p className="font-semibold text-foreground mt-1">{profileForm.finalEducation}</p>
                </div>
              </div>
              <p className="mt-3 text-muted-foreground">지원 필요사항</p>
              <p className="mt-1 text-foreground">{profileForm.disabilitySupport}</p>
              <p className="mt-3 text-muted-foreground">자기소개</p>
              <p className="mt-1 text-foreground whitespace-pre-line">{profileForm.introduction}</p>
            </div>

            <button
              type="button"
              className="mt-5 w-full rounded-lg bg-primary px-4 py-3 text-sm font-semibold text-white hover:bg-primary/90"
              onClick={() => setIsPreviewOpen(false)}
            >
              확인
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
