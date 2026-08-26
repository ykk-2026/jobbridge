import {
  ArrowLeft,
  BadgeCheck,
  Bookmark,
  BookmarkCheck,
  BriefcaseBusiness,
  Building2,
  CalendarDays,
  Check,
  CheckCircle2,
  Clock3,
  FileText,
  HeartHandshake,
  MapPin,
  MessageCircleQuestion,
  Phone,
  Send,
  ShieldCheck,
  Users,
  WalletCards,
} from 'lucide-react';
import { useMemo, useState } from 'react';
import { mockJobs } from '@/app/data/mockData';
import type { ApplicationFormData, CurrentUser, Page } from '@/app/types';

interface JobDetailPageProps {
  jobId: string | null;
  currentUser: CurrentUser | null;
  navigate: (page: Page, jobId?: string) => void;
  bookmarked: boolean;
  applied: boolean;
  onBookmark: (id: string) => void;
  onApply: (id: string, formData: ApplicationFormData) => Promise<void>;
  onRequireLoginForApply: (id: string) => void;
}

const accessibilityLabels = [
  { key: 'elevator' as const, label: '엘리베이터' },
  { key: 'parking' as const, label: '장애인 주차' },
  { key: 'wheelchair' as const, label: '휠체어 접근' },
  { key: 'restroom' as const, label: '장애인 화장실' },
  { key: 'guideDog' as const, label: '안내견 동반' },
  { key: 'hearingLoop' as const, label: '보청 지원' },
];

const applicationPasswordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;
const getNormalizedJobId = (jobId: string | null) => jobId?.replace(/^job-/, '') || '';
const isPartTimeJob = (job: (typeof mockJobs)[number]) => job.category === 'PartTime' || job.requirements.includes('알바');

const getJobMeta = (job: (typeof mockJobs)[number]) => {
  const partTime = isPartTimeJob(job);
  const remote = job.isRemote || job.workType.includes('재택');

  return {
    employment: partTime ? '아르바이트' : '정규/계약직',
    schedule: partTime ? job.workType.replace(' 알바', '') || '협의 가능' : remote ? '주 5일, 유연근무' : '주 5일, 사무실 근무',
    hours: partTime ? '하루 4~6시간, 시간 협의' : remote ? '09:00~18:00, 선택근무 가능' : '09:00~18:00',
    payType: job.salary.includes('시급') ? '시급' : job.salary.includes('일급') ? '일급' : '월급/연봉',
    applyMethods: partTime ? ['온라인 지원', '문자 지원', '전화 문의'] : ['온라인 지원', '이메일 지원'],
    safeBadges: partTime ? ['기업 인증', '근로계약서 작성', '임금 조건 공개'] : ['기업 인증', '접근성 정보 확인', '채용 절차 공개'],
  };
};

export function JobDetailPage({
  jobId,
  currentUser,
  navigate,
  bookmarked,
  applied,
  onBookmark,
  onApply,
  onRequireLoginForApply,
}: JobDetailPageProps) {
  const normalizedJobId = getNormalizedJobId(jobId);
  const job = mockJobs.find(item => item.id === normalizedJobId) || mockJobs[0];
  const meta = useMemo(() => getJobMeta(job), [job]);
  const initialPhoneParts = (currentUser?.phone || '').split('-');
  const [isApplyOpen, setIsApplyOpen] = useState(false);
  const [submittedApplied, setSubmittedApplied] = useState(false);
  const [applyError, setApplyError] = useState('');
  const [applicationForm, setApplicationForm] = useState({
    name: currentUser?.name || '',
    phone1: initialPhoneParts[0] || '010',
    phone2: initialPhoneParts[1] || '',
    phone3: initialPhoneParts[2] || '',
    email: currentUser?.email || '',
    employmentType: meta.employment,
    password: '',
    passwordConfirm: '',
    privacyAgreed: false,
  });

  const activeAccessibility = accessibilityLabels.filter(item => job.accessibility[item.key]);
  const inactiveAccessibility = accessibilityLabels.filter(item => !job.accessibility[item.key]);
  const isApplied = applied || submittedApplied;

  const updateApplicationForm = (field: keyof typeof applicationForm, value: string | boolean) => {
    setApplicationForm(prev => ({ ...prev, [field]: value }));
    setApplyError('');
  };

  const handleApply = () => {
    if (!currentUser) {
      window.alert('로그인 후 지원할 수 있습니다.');
      onRequireLoginForApply(job.id);
      return;
    }

    if (currentUser.role !== 'personal') {
      window.alert('구직자 계정으로만 지원할 수 있습니다.');
      return;
    }

    if (isApplied) return;

    setApplicationForm(prev => ({
      ...prev,
      name: prev.name || currentUser.name || '',
      email: prev.email || currentUser.email || '',
      employmentType: prev.employmentType || meta.employment,
    }));
    setApplyError('');
    setIsApplyOpen(true);
  };

  const submitApplication = async () => {
    const phoneParts = [applicationForm.phone1, applicationForm.phone2, applicationForm.phone3].map(value => String(value).trim());

    if (!applicationForm.privacyAgreed) {
      setApplyError('개인정보 수집 및 이용에 동의해 주세요.');
      return;
    }

    if (!applicationForm.name.trim() || /\s/.test(applicationForm.name)) {
      setApplyError('성명은 공백 없이 입력해 주세요.');
      return;
    }

    if (phoneParts.some(part => !/^\d+$/.test(part)) || phoneParts[1].length < 3 || phoneParts[2].length < 4) {
      setApplyError('연락 가능한 휴대전화 번호를 정확히 입력해 주세요.');
      return;
    }

    if (!applicationForm.email.trim() || !applicationForm.email.includes('@')) {
      setApplyError('지원 결과를 받을 이메일을 입력해 주세요.');
      return;
    }

    if (!applicationPasswordPattern.test(applicationForm.password)) {
      setApplyError('비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해 주세요.');
      return;
    }

    if (applicationForm.password !== applicationForm.passwordConfirm) {
      setApplyError('비밀번호와 비밀번호 확인 값이 일치해야 합니다.');
      return;
    }

    const now = new Date().toISOString();
    try {
      await onApply(job.id, {
        name: applicationForm.name.trim(),
        phone: phoneParts.join('-'),
        email: applicationForm.email.trim(),
        employmentType: applicationForm.employmentType,
        privacyAgreed: applicationForm.privacyAgreed,
        submittedAt: now,
        updatedAt: now,
      });
      setSubmittedApplied(true);
      setIsApplyOpen(false);
      window.alert('지원서가 MariaDB에 제출되었습니다.');
    } catch (error) {
      setApplyError(error instanceof Error ? error.message : '지원서 제출에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-[#F6F7F9] text-[#111827]">
      <main className="mx-auto max-w-[1120px] px-4 py-5 sm:px-8">
        <button type="button" onClick={() => navigate('jobs')} className="mb-4 inline-flex items-center gap-2 text-sm font-extrabold text-[#344054] hover:text-[#0D6BEA]">
          <ArrowLeft size={17} />
          채용공고 목록
        </button>

        <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
          <div className="flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between">
            <div className="flex min-w-0 gap-4">
              <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-lg text-xl font-extrabold text-white" style={{ backgroundColor: job.companyColor }}>
                {job.companyInitials}
              </div>
              <div className="min-w-0">
                <p className="flex flex-wrap items-center gap-2 text-sm font-bold text-[#7A8495]">
                  <Building2 size={15} />
                  {job.company}
                  <span className="inline-flex items-center gap-1 rounded-full bg-[#EAF8F8] px-2 py-0.5 text-xs text-[#217A83]">
                    <BadgeCheck size={13} /> 인증 기업
                  </span>
                </p>
                <h1 className="mt-2 text-2xl font-extrabold leading-tight text-black sm:text-3xl">{job.title}</h1>
                <div className="mt-3 flex flex-wrap gap-2 text-sm font-bold text-[#596273]">
                  <span className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1">
                    <MapPin size={14} /> {job.location}
                  </span>
                  <span className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1">
                    <BriefcaseBusiness size={14} /> {job.workType}
                  </span>
                  <span className="inline-flex items-center gap-1 rounded-full bg-[#FFF7E8] px-3 py-1 text-[#B45309]">
                    <WalletCards size={14} /> {job.salary}
                  </span>
                </div>
              </div>
            </div>

            <div className="flex flex-col gap-2 sm:flex-row lg:flex-col lg:items-stretch">
              <button
                type="button"
                onClick={() => onBookmark(`job-${job.id}`)}
                className="inline-flex h-11 min-w-[92px] items-center justify-center gap-2 whitespace-nowrap rounded-lg border border-[#D7DDE5] px-4 text-sm font-bold hover:bg-[#F8FAFC]"
              >
                {bookmarked ? <BookmarkCheck size={17} /> : <Bookmark size={17} />}
                {bookmarked ? '저장됨' : '저장'}
              </button>
              <button
                type="button"
                onClick={handleApply}
                disabled={isApplied}
                className={`inline-flex h-11 min-w-[116px] items-center justify-center gap-2 whitespace-nowrap rounded-lg px-5 text-sm font-extrabold ${
                  isApplied ? 'bg-[#E7F8EF] text-[#14843C]' : 'bg-[#0D6BEA] text-white hover:bg-[#0959C7]'
                }`}
              >
                {isApplied ? <CheckCircle2 size={17} /> : <Send size={17} />}
                {isApplied ? '지원 완료' : '지원하기'}
              </button>
            </div>
          </div>
        </section>

        <section className="mt-5 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {[
            { label: '급여', value: job.salary, icon: WalletCards },
            { label: '근무시간', value: meta.hours, icon: Clock3 },
            { label: '근무일', value: meta.schedule, icon: CalendarDays },
            { label: '모집인원', value: `${job.headcount}명`, icon: Users },
          ].map(item => {
            const Icon = item.icon;
            return (
              <div key={item.label} className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
                <p className="flex items-center gap-2 text-xs font-extrabold text-[#7A8495]">
                  <Icon size={16} /> {item.label}
                </p>
                <p className="mt-2 text-base font-extrabold text-black">{item.value}</p>
              </div>
            );
          })}
        </section>

        <div className="mt-5 grid gap-5 lg:grid-cols-[1fr_320px]">
          <section className="space-y-5">
            <article className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">주요 업무</h2>
              <p className="mt-3 text-sm font-semibold leading-7 text-[#344054]">{job.description}</p>
              <div className="mt-4 flex flex-wrap gap-2">
                {job.requirements.map(requirement => (
                  <span key={requirement} className="rounded-full bg-[#E4EFFF] px-3 py-1.5 text-xs font-extrabold text-[#0D6BEA]">
                    {requirement}
                  </span>
                ))}
              </div>
            </article>

            <article className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">근무 조건</h2>
              <div className="mt-4 grid gap-3 sm:grid-cols-2">
                {[
                  ['고용형태', meta.employment],
                  ['급여형태', meta.payType],
                  ['근무지', job.location],
                  ['지원방법', meta.applyMethods.join(', ')],
                ].map(([label, value]) => (
                  <div key={label} className="rounded-lg bg-[#F8FAFC] px-4 py-3">
                    <p className="text-xs font-bold text-[#7A8495]">{label}</p>
                    <p className="mt-2 text-sm font-extrabold text-black">{value}</p>
                  </div>
                ))}
              </div>
            </article>

            <article className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">복리후생 및 안심 배지</h2>
              <div className="mt-4 grid gap-2 sm:grid-cols-2">
                {[...meta.safeBadges, ...job.benefits].map(benefit => (
                  <span key={benefit} className="inline-flex items-center gap-2 rounded-lg bg-[#F8FAFC] px-3 py-2 text-sm font-bold text-[#344054]">
                    <Check size={15} className="text-[#14843C]" />
                    {benefit}
                  </span>
                ))}
              </div>
            </article>

            <article className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">접근성 정보</h2>
              <div className="mt-4 grid gap-2 sm:grid-cols-2">
                {activeAccessibility.map(item => (
                  <span key={item.key} className="inline-flex items-center gap-2 rounded-lg bg-[#EAF8F8] px-3 py-2 text-sm font-bold text-[#217A83]">
                    <CheckCircle2 size={15} />
                    {item.label}
                  </span>
                ))}
                {inactiveAccessibility.map(item => (
                  <span key={item.key} className="inline-flex items-center gap-2 rounded-lg bg-[#F1F3F6] px-3 py-2 text-sm font-bold text-[#7A8495]">
                    <CheckCircle2 size={15} />
                    {item.label} 미확인
                  </span>
                ))}
              </div>
            </article>

            <article className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">전형 절차</h2>
              <div className="mt-4 grid gap-3 sm:grid-cols-4">
                {['간편 지원', '담당자 확인', isPartTimeJob(job) ? '면접/연락' : '면접 안내', '최종 결과'].map((step, index) => (
                  <div key={step} className="rounded-lg border border-[#E1E6EE] bg-white px-4 py-3">
                    <span className="flex h-7 w-7 items-center justify-center rounded-full bg-[#E4EFFF] text-xs font-extrabold text-[#0D6BEA]">{index + 1}</span>
                    <p className="mt-3 text-sm font-extrabold text-black">{step}</p>
                  </div>
                ))}
              </div>
            </article>
          </section>

          <aside className="space-y-5 lg:sticky lg:top-20 lg:self-start">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">지원 정보</h2>
              <div className="mt-4 space-y-3 text-sm font-bold text-[#344054]">
                <p className="flex items-center justify-between gap-3">
                  <span className="inline-flex items-center gap-2 text-[#7A8495]"><CalendarDays size={16} /> 마감일</span>
                  <span>{job.deadline}</span>
                </p>
                <p className="flex items-center justify-between gap-3">
                  <span className="inline-flex items-center gap-2 text-[#7A8495]"><Users size={16} /> 모집 인원</span>
                  <span>{job.headcount}명</span>
                </p>
                <p className="flex items-center justify-between gap-3">
                  <span className="inline-flex items-center gap-2 text-[#7A8495]"><ShieldCheck size={16} /> AI 매칭</span>
                  <span className="text-[#0D6BEA]">{job.aiScore}%</span>
                </p>
              </div>
              <button
                type="button"
                onClick={handleApply}
                disabled={isApplied}
                className={`mt-5 inline-flex h-12 w-full items-center justify-center gap-2 rounded-lg text-sm font-extrabold ${
                  isApplied ? 'bg-[#E7F8EF] text-[#14843C]' : 'bg-[#0D6BEA] text-white hover:bg-[#0959C7]'
                }`}
              >
                {isApplied ? <CheckCircle2 size={17} /> : <Send size={17} />}
                {isApplied ? '지원 완료' : '즉시 지원하기'}
              </button>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="flex items-center gap-2 text-lg font-extrabold text-black">
                <MessageCircleQuestion size={19} />
                빠른 질문
              </h2>
              <div className="mt-3 space-y-2">
                {[
                  `초보도 지원할 수 있나요? ${job.requirements.includes('초보 가능') ? '가능합니다.' : '요구 역량을 확인해 주세요.'}`,
                  `근무 시간은? ${meta.hours}`,
                  `지원 방법은? ${meta.applyMethods.join(', ')}`,
                ].map(question => (
                  <p key={question} className="rounded-lg bg-[#F8FAFC] px-3 py-2 text-sm font-bold leading-6 text-[#344054]">
                    {question}
                  </p>
                ))}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">기업 정보</h2>
              <p className="mt-3 text-sm font-semibold leading-6 text-[#596273]">{job.companyDesc}</p>
              <div className="mt-4 rounded-lg bg-[#F8FAFC] px-4 py-3">
                <p className="text-sm font-extrabold text-black">{job.company} 채용담당자</p>
                <p className="mt-2 flex items-center gap-2 text-sm font-bold text-[#596273]">
                  <Phone size={15} />
                  안심번호로 문의 가능
                </p>
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">AI 추천 이유</h2>
              <div className="mt-3 space-y-2">
                {job.aiReasons.slice(0, 3).map(reason => (
                  <p key={reason} className="flex gap-2 rounded-lg bg-[#F8FAFC] px-3 py-2 text-sm font-bold leading-6 text-[#344054]">
                    <HeartHandshake size={16} className="mt-0.5 shrink-0 text-[#14843C]" />
                    {reason}
                  </p>
                ))}
              </div>
            </section>
          </aside>
        </div>
      </main>

      <div className="sticky bottom-0 z-40 border-t border-[#DDE3EA] bg-white/95 px-4 py-3 backdrop-blur lg:hidden">
        <div className="mx-auto grid max-w-[1120px] grid-cols-[52px_1fr] gap-2">
          <button
            type="button"
            onClick={() => onBookmark(`job-${job.id}`)}
            aria-label={bookmarked ? '저장 해제' : '공고 저장'}
            className="flex h-12 items-center justify-center rounded-lg border border-[#D7DDE5] text-[#344054]"
          >
            {bookmarked ? <BookmarkCheck size={20} /> : <Bookmark size={20} />}
          </button>
          <button
            type="button"
            onClick={handleApply}
            disabled={isApplied}
            className={`flex h-12 items-center justify-center gap-2 rounded-lg text-sm font-extrabold ${
              isApplied ? 'bg-[#E7F8EF] text-[#14843C]' : 'bg-[#0D6BEA] text-white'
            }`}
          >
            {isApplied ? <CheckCircle2 size={17} /> : <Send size={17} />}
            {isApplied ? '지원 완료' : '지원하기'}
          </button>
        </div>
      </div>

      {isApplyOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center overflow-x-hidden bg-black/45 px-4 py-6">
          <div className="max-h-[92vh] w-full max-w-2xl overflow-y-auto overflow-x-hidden rounded-xl bg-white shadow-xl">
            <div className="flex items-start justify-between gap-4 border-b border-[#E7ECF2] px-6 py-5">
              <div>
                <p className="text-sm font-bold text-[#0D6BEA]">{job.company}</p>
                <h2 className="mt-1 text-xl font-extrabold text-black">간편 지원</h2>
                <p className="mt-1 text-sm font-semibold text-[#7A8495]">{job.title}</p>
              </div>
              <button
                type="button"
                onClick={() => setIsApplyOpen(false)}
                className="flex h-9 w-9 items-center justify-center rounded-full bg-[#F1F3F6] text-lg font-bold text-[#596273] hover:bg-[#E1E6EE]"
                aria-label="지원 창 닫기"
              >
                x
              </button>
            </div>

            <div className="px-6 py-6">
              <div className="rounded-lg bg-[#F8FAFC] px-4 py-3 text-sm font-bold text-[#344054]">
                공고명: <span className="text-black">{job.title}</span>
              </div>

              <div className="mt-5 divide-y divide-[#E7ECF2] border-t border-[#D7DDE5]">
                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">성명</span>
                  <input
                    value={applicationForm.name}
                    onChange={event => updateApplicationForm('name', event.target.value)}
                    className="h-11 w-full rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                  />
                </label>

                <div className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <p className="text-sm font-bold text-[#596273]">휴대전화</p>
                  <div className="grid min-w-0 grid-cols-1 gap-2 sm:grid-cols-3">
                    {(['phone1', 'phone2', 'phone3'] as const).map((field, index) => (
                      <input
                        key={field}
                        value={applicationForm[field]}
                        onChange={event => updateApplicationForm(field, event.target.value.replace(/\D/g, '').slice(0, index === 0 ? 3 : 4))}
                        className="h-11 min-w-0 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                      />
                    ))}
                  </div>
                </div>

                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">이메일</span>
                  <input
                    value={applicationForm.email}
                    onChange={event => updateApplicationForm('email', event.target.value)}
                    placeholder="email@example.com"
                    className="h-11 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                  />
                </label>

                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">고용형태</span>
                  <select
                    value={applicationForm.employmentType}
                    onChange={event => updateApplicationForm('employmentType', event.target.value)}
                    className="h-11 rounded border border-[#D7DDE5] bg-white px-3 text-sm font-bold text-[#344054] outline-none focus:border-[#0D6BEA]"
                  >
                    <option value="아르바이트">아르바이트</option>
                    <option value="정규/계약직">정규/계약직</option>
                    <option value="인턴">인턴</option>
                  </select>
                </label>

                <label className="grid gap-3 py-5 sm:grid-cols-[140px_minmax(0,1fr)]">
                  <span className="text-sm font-bold text-[#596273]">비밀번호</span>
                  <div className="grid gap-2">
                    <input
                      type="password"
                      value={applicationForm.password}
                      onChange={event => updateApplicationForm('password', event.target.value)}
                      placeholder="영문, 숫자, 특수문자 포함 8~20자"
                      className="h-11 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                    />
                    <input
                      type="password"
                      value={applicationForm.passwordConfirm}
                      onChange={event => updateApplicationForm('passwordConfirm', event.target.value)}
                      placeholder="비밀번호 확인"
                      className="h-11 rounded border border-[#D7DDE5] px-3 text-sm outline-none focus:border-[#0D6BEA]"
                    />
                  </div>
                </label>
              </div>

              <label className="mt-5 flex items-start gap-2 rounded-lg bg-[#F8FAFC] px-4 py-3 text-sm font-bold text-[#344054]">
                <input
                  type="checkbox"
                  checked={applicationForm.privacyAgreed}
                  onChange={event => updateApplicationForm('privacyAgreed', event.target.checked)}
                  className="mt-1"
                />
                지원 진행을 위해 이름, 연락처, 이메일을 채용 담당자에게 전달하는 데 동의합니다.
              </label>

              {applyError && <p className="mt-4 rounded-lg bg-[#FFF5F5] px-4 py-3 text-sm font-bold text-[#D92D20]">{applyError}</p>}

              <div className="mt-6 flex flex-col-reverse gap-2 border-t border-[#E7ECF2] pt-5 sm:flex-row sm:justify-end">
                <button type="button" onClick={() => setIsApplyOpen(false)} className="rounded-lg border border-[#D7DDE5] px-5 py-3 text-sm font-extrabold text-[#344054] hover:bg-[#F8FAFC]">
                  취소
                </button>
                <button type="button" onClick={submitApplication} className="inline-flex items-center gap-2 rounded-lg bg-[#0D6BEA] px-6 py-3 text-sm font-extrabold text-white hover:bg-[#0959C7]">
                  <FileText size={17} />
                  지원서 제출
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
