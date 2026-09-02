import {
  Accessibility,
  ArrowRight,
  Bookmark,
  BookmarkCheck,
  BriefcaseBusiness,
  CheckCircle2,
  ClipboardList,
  Clock3,
  FileText,
  Heart,
  Home,
  MapPin,
  MessageCircle,
  Monitor,
  ParkingSquare,
  Search,
  ShieldCheck,
  Sparkles,
  Target,
  UserRound,
} from 'lucide-react';
import { useState } from 'react';
import type { Page } from '@/app/types';

interface MainPageProps {
  navigate: (page: Page, jobId?: string) => void;
  bookmarks: Set<string>;
  onBookmark: (id: string) => void;
  onSearch: (query: string) => void;
}

const recommendedJobs = [
  { id: 'job-1', company: 'ABC테크', initial: 'A', color: '#53657F', title: 'Java 백엔드 개발자', location: '서울 강남구', match: 98, salary: '4,000만+', tags: ['정규직', '재택근무 가능'] },
  { id: 'job-2', company: '대한소프트', initial: 'D', color: '#48A6D8', title: '웹 퍼블리셔', location: '서울 마포구', match: 88, salary: '3,200만+', tags: ['계약직', '유연근무'] },
  { id: 'job-4', company: '리유', initial: 'R', color: '#4FC3A1', title: 'UI/UX 디자이너', location: '서울 영등포구', match: 95, salary: '3,500만+', tags: ['정규직', '시각장애 우대'] },
  { id: 'job-10', company: '해피', initial: 'H', color: '#F5A84B', title: '고객 상담 매니저', location: '부산 해운대구', match: 92, salary: '3,000만+', tags: ['정규직', '청각장애 우대'] },
];

const quickActions = [
  { label: 'AI 맞춤 추천', helper: '내 프로필 기반 추천', icon: Target, action: 'ai', color: '#1F64E8' },
  { label: '간편 지원', helper: '이력서로 빠른 지원', icon: FileText, action: 'apply', color: '#0EA5E9' },
  { label: '관심 공고', helper: '저장한 공고 관리', icon: Bookmark, action: 'saved', color: '#7C3AED' },
  { label: '지원 현황', helper: '진행 상태 확인', icon: BriefcaseBusiness, action: 'applications', color: '#16A34A' },
  { label: '커뮤니티', helper: '정보 공유와 소통', icon: MessageCircle, action: 'community', color: '#F59E0B' },
];

const accessibilityJobs = [
  { label: '휠체어 접근 가능', count: '38건', icon: Accessibility, query: '휠체어' },
  { label: '재택근무 가능', count: '24건', icon: Home, query: '재택근무' },
  { label: '유연근무 가능', count: '19건', icon: Clock3, query: '유연근무' },
  { label: '장애인 주차시설', count: '31건', icon: ParkingSquare, query: '장애인 주차' },
  { label: '보조공학기기 지원', count: '12건', icon: Monitor, query: '보조기기' },
];

const latestJobs = [
  { id: 'job-15', category: '서비스', company: '교보문고', title: '도서 정리 및 고객 안내 알바', location: '대구 중구', type: '파트타임', deadline: 'D-7', color: '#22C55E' },
  { id: 'job-12', category: '서비스', company: 'XYZ 컴퍼니', title: '매장 진열 및 계산 보조 알바', location: '경기 성남시', type: '주말 알바', deadline: 'D-6', color: '#7C3AED' },
  { id: 'job-3', category: 'IT/개발', company: 'LG CNS', title: '데이터 분석가', location: '서울 강서구', type: '정규직', deadline: 'D-7', color: '#A855F7' },
  { id: 'job-1', category: 'IT/개발', company: 'ABC테크', title: 'Java 백엔드 개발자', location: '서울 강남구', type: '정규직', deadline: 'D-12', color: '#53657F' },
  { id: 'job-7', category: '사무/관리', company: '코리아오피스', title: '문서 정리 및 사무보조', location: '서울 중구', type: '계약직', deadline: 'D-5', color: '#0EA5E9' },
  { id: 'job-4', category: '디자인', company: '리유', title: 'UI/UX 디자이너', location: '서울 영등포구', type: '정규직', deadline: 'D-9', color: '#4FC3A1' },
  { id: 'job-13', category: '제조', company: '쿠팡풀필먼트', title: '물류센터 포장 검수 알바', location: '인천 서구', type: '단기 알바', deadline: 'D-2', color: '#DC2626' },
];
const latestJobCategories = ['IT/개발', '사무/관리', '서비스', '디자인', '제조'];

const notices = [
  { title: '일이음 서비스 리뉴얼 오픈 안내', date: '2025.05.20' },
  { title: '이력서 작성 가이드 업데이트', date: '2025.05.18' },
  { title: '장애인 고용 우수 기업 채용관 OPEN', date: '2025.05.15' },
];

function HeroIllustration() {
  return (
    <div className="pointer-events-none relative hidden h-[250px] w-[560px] shrink-0 lg:block">
      <div className="absolute bottom-3 right-16 h-[190px] w-[330px] rounded-[56px] bg-[#DCEBFF]" />
      <div className="absolute bottom-8 left-16 h-[92px] w-12 rounded-t-[18px] bg-[#B8D3F0]" />
      <div className="absolute bottom-8 left-36 h-[64px] w-10 rounded-t-[16px] bg-[#8FCAC2]" />
      <div className="absolute bottom-8 right-2 h-[34px] w-5 rounded-t-full bg-[#8FCAC2]" />

      <div className="absolute left-8 top-8 w-[142px] rounded-[8px] bg-white p-4 shadow-[0_10px_28px_rgba(30,64,120,0.14)]">
        <p className="text-[11px] font-black text-[#111827]">맞춤 일자리 추천</p>
        <svg viewBox="0 0 110 52" className="mt-2 h-[52px] w-full" aria-hidden="true">
          <polyline points="6,40 28,27 46,34 66,17 86,24 104,12" fill="none" stroke="#1F64E8" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
          {[6, 28, 46, 66, 86, 104].map((x, index) => (
            <circle key={x} cx={x} cy={[40, 27, 34, 17, 24, 12][index]} r="3" fill="#1F64E8" />
          ))}
        </svg>
      </div>

      <div className="absolute right-36 top-12 flex h-16 w-16 items-center justify-center rounded-[10px] bg-white text-[#1F64E8] shadow-[0_10px_28px_rgba(30,64,120,0.14)]">
        <Search size={30} />
      </div>
      <div className="absolute bottom-24 right-5 flex h-16 w-16 items-center justify-center rounded-[10px] bg-white text-[#1F64E8] shadow-[0_10px_28px_rgba(30,64,120,0.14)]">
        <Heart size={28} fill="currentColor" />
      </div>
      <div className="absolute bottom-20 left-[188px] flex h-14 w-14 items-center justify-center rounded-[10px] bg-white text-[#1F64E8] shadow-[0_10px_28px_rgba(30,64,120,0.14)]">
        <BriefcaseBusiness size={23} />
      </div>

      <svg className="absolute bottom-5 right-[122px] h-[208px] w-[210px]" viewBox="0 0 210 208" aria-hidden="true">
        <circle cx="112" cy="156" r="42" fill="#FFFFFF" stroke="#13294B" strokeWidth="12" />
        <path d="M80 154h78" stroke="#13294B" strokeWidth="12" strokeLinecap="round" />
        <path d="M73 96v47c0 16 11 28 27 28h30" fill="none" stroke="#13294B" strokeWidth="12" strokeLinecap="round" />
        <rect x="112" y="90" width="86" height="68" rx="8" fill="#44506A" />
        <rect x="132" y="143" width="50" height="6" rx="3" fill="#AAB5C6" />
        <path d="M59 80c0-23 19-42 42-42h6c22 0 40 18 40 40v54H59V80z" fill="#2367E8" />
        <circle cx="104" cy="48" r="30" fill="#F5B176" />
        <path d="M72 20h54v11c0 17-14 31-31 31H72V20z" fill="#0F2C57" />
        <path d="M59 98l-20-43" stroke="#F5B176" strokeWidth="16" strokeLinecap="round" />
        <path d="M72 130h82" stroke="#2367E8" strokeWidth="12" strokeLinecap="round" />
      </svg>
    </div>
  );
}

export function MainPage({ navigate, bookmarks, onBookmark, onSearch }: MainPageProps) {
  const [selectedLatestCategory, setSelectedLatestCategory] = useState('IT/개발');
  const displayedLatestJobs = latestJobs.filter(job => selectedLatestCategory === '전체' || job.category === selectedLatestCategory);

  const runQuickAction = (action: (typeof quickActions)[number]['action']) => {
    if (action === 'ai') navigate('ai-recommend');
    if (action === 'apply') onSearch('');
    if (action === 'saved') navigate('saved');
    if (action === 'applications') navigate('applications');
    if (action === 'community') navigate('community');
  };

  return (
    <div className="min-h-screen bg-[#F4F7FB] text-[#111827]">
      <main>
        <section className="border-b border-[#DDE8F8] bg-[#EEF5FF]">
          <div className="mx-auto flex min-h-[260px] max-w-[1240px] items-center justify-between gap-8 px-6">
            <div className="w-full max-w-[560px] py-8">
              <h1 className="text-[30px] font-black leading-[1.28] tracking-normal text-black sm:text-[34px]">
                AI가 당신에게 딱 맞는
                <br />
                <span className="text-[#0D6BEA]">일자리를</span> 추천해드려요
              </h1>
              <p className="mt-5 text-[15px] font-semibold leading-6 text-[#344054]">
                장애인 맞춤 일자리 플랫폼, 일이음에서
                <br />
                새로운 가능성을 이어가세요.
              </p>
              <div className="mt-6 flex flex-wrap gap-3">
                <button type="button" onClick={() => navigate('ai-recommend')} className="inline-flex h-[44px] items-center gap-2 rounded-[6px] bg-[#1F64E8] px-7 text-[14px] font-extrabold text-white shadow-sm hover:bg-[#1754C8]">
                  <Sparkles size={16} />
                  AI 일자리 추천받기
                </button>
                <button type="button" onClick={() => navigate('user-dashboard')} className="inline-flex h-[44px] items-center gap-2 rounded-[6px] border border-[#C9D5E5] bg-white px-7 text-[14px] font-extrabold text-[#344054] shadow-sm hover:bg-[#F8FAFC]">
                  <UserRound size={16} />
                  내 프로필 관리
                </button>
              </div>
            </div>
            <HeroIllustration />
          </div>
        </section>

        <div className="mx-auto max-w-[1240px] px-6 pb-6">
          <section className="rounded-b-xl border border-t-0 border-[#DDE3EA] bg-white p-5 shadow-[0_8px_22px_rgba(15,23,42,0.06)]">
            <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
              <div className="flex flex-wrap items-end gap-3">
                <h2 className="text-xl font-black text-black">AI가 추천하는 일자리</h2>
                <p className="pb-0.5 text-xs font-semibold text-[#7A8495]">회원님의 프로필과 접근성 조건을 함께 반영했어요</p>
              </div>
              <button type="button" onClick={() => navigate('ai-recommend')} className="inline-flex h-9 items-center gap-1 rounded-lg bg-[#EEF5FF] px-3 text-xs font-extrabold text-[#1F64E8] hover:bg-[#DCEBFF]">
                더 많은 추천 보기 <ArrowRight size={14} />
              </button>
            </div>

            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              {recommendedJobs.map(job => (
                <article key={job.id} className="group flex min-h-[178px] flex-col justify-between rounded-xl border border-[#E1E6EE] bg-gradient-to-b from-white to-[#F8FAFC] p-4 transition hover:-translate-y-0.5 hover:border-[#9EC5FF] hover:shadow-[0_10px_24px_rgba(31,100,232,0.12)]">
                  <div className="flex items-start justify-between gap-3">
                    <button type="button" onClick={() => navigate('job-detail', job.id)} className="flex min-w-0 gap-3 text-left">
                      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-[7px] text-lg font-black text-white" style={{ backgroundColor: job.color }}>
                        {job.initial}
                      </span>
                      <span className="min-w-0">
                        <span className="inline-flex rounded-full bg-[#E7F8EF] px-2 py-0.5 text-[10px] font-black text-[#14843C]">적합 {job.match}%</span>
                        <span className="mt-1 block truncate text-[11px] font-extrabold text-[#596273]">{job.company}</span>
                        <span className="mt-1 block truncate text-[15px] font-black text-black">{job.title}</span>
                        <span className="mt-2 flex items-center gap-1 text-[11px] font-semibold text-[#7A8495]">
                          <MapPin size={11} /> {job.location}
                        </span>
                        <span className="mt-2 block text-[11px] font-black text-[#1F64E8]">{job.salary}</span>
                      </span>
                    </button>
                    <button
                      type="button"
                      aria-label={bookmarks.has(job.id) ? '관심 공고 해제' : '관심 공고 저장'}
                      onClick={() => onBookmark(job.id)}
                      className="flex h-7 w-7 shrink-0 items-center justify-center rounded-md text-[#8A94A6] hover:bg-[#F8FAFC]"
                    >
                      {bookmarks.has(job.id) ? <BookmarkCheck size={16} /> : <Bookmark size={16} />}
                    </button>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {job.tags.map(tag => (
                      <span key={tag} className="rounded-full bg-[#F1F4F8] px-2.5 py-1 text-[10px] font-bold text-[#344054]">{tag}</span>
                    ))}
                  </div>
                  <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-[#E7ECF2]">
                    <div className="h-full rounded-full bg-[#1F64E8]" style={{ width: `${job.match}%` }} />
                  </div>
                </article>
              ))}
            </div>
          </section>

          <div className="mt-4 grid gap-4 lg:grid-cols-2">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-[0_8px_22px_rgba(15,23,42,0.05)]">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-black text-black">일이음을 더 편리하게 이용하세요</h2>
                  <p className="mt-1 text-xs font-semibold text-[#7A8495]">자주 쓰는 기능을 빠르게 실행하세요</p>
                </div>
                <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-[#EEF5FF] text-[#1F64E8]">
                  <Sparkles size={18} />
                </span>
              </div>
              <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-5">
                {quickActions.map(item => {
                  const Icon = item.icon;
                  return (
                    <button key={item.label} type="button" onClick={() => runQuickAction(item.action)} className="min-h-[118px] rounded-xl border border-[#EDF1F6] bg-[#FBFCFE] px-3 py-4 text-center transition hover:border-[#B9D6FF] hover:bg-[#F4F8FF]">
                      <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl text-white shadow-sm" style={{ backgroundColor: item.color }}>
                        <Icon size={23} />
                      </span>
                      <span className="mt-3 block text-[13px] font-black text-black">{item.label}</span>
                      <span className="mt-1 block text-[11px] font-semibold leading-4 text-[#7A8495]">{item.helper}</span>
                    </button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-[0_8px_22px_rgba(15,23,42,0.05)]">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h2 className="text-xl font-black text-black">접근성 좋은 일자리</h2>
                  <p className="mt-1 text-xs font-semibold text-[#7A8495]">필요한 근무환경 조건으로 바로 찾아보세요</p>
                </div>
                <button type="button" onClick={() => onSearch('접근성')} className="inline-flex h-9 shrink-0 items-center gap-1 rounded-lg bg-[#EAF8F3] px-3 text-xs font-extrabold text-[#14843C] hover:bg-[#DDF4EA]">
                  전체 보기 <ArrowRight size={14} />
                </button>
              </div>
              <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-5">
                {accessibilityJobs.map(item => {
                  const Icon = item.icon;
                  return (
                    <button key={item.label} type="button" onClick={() => onSearch(item.query)} className="min-h-[118px] rounded-xl border border-[#E0F2EA] bg-[#F8FFFC] px-3 py-4 text-center transition hover:border-[#8DDCC2] hover:bg-[#F0FCF8]">
                      <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-[#DDF7ED] text-[#14843C]">
                        <Icon size={23} />
                      </span>
                      <span className="mt-3 block text-[12px] font-black leading-4 text-black">{item.label}</span>
                      <span className="mt-2 block text-[11px] font-black text-[#14843C]">{item.count}</span>
                    </button>
                  );
                })}
              </div>
            </section>
          </div>

          <div className="mt-4 grid gap-4 lg:grid-cols-2">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-[0_8px_22px_rgba(15,23,42,0.05)]">
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-xl font-black text-black">최신 채용공고</h2>
                <button type="button" onClick={() => onSearch('')} className="inline-flex h-9 items-center gap-1 rounded-lg bg-[#EEF5FF] px-3 text-xs font-extrabold text-[#1F64E8]">
                  전체 보기 <ArrowRight size={14} />
                </button>
              </div>
              <div className="mb-3 flex flex-wrap gap-2 border-b border-[#E7ECF2] pb-3 text-xs font-bold text-[#475467]">
                {latestJobCategories.map(tab => (
                  <button
                    key={tab}
                    type="button"
                    onClick={() => setSelectedLatestCategory(tab)}
                    className={`rounded-full px-3 py-1.5 ${
                      selectedLatestCategory === tab ? 'bg-[#1F64E8] text-white' : 'bg-[#F1F4F8] hover:bg-[#E4EFFF] hover:text-[#1F64E8]'
                    }`}
                  >
                    {tab}
                  </button>
                ))}
              </div>
              <div className="space-y-3">
                {displayedLatestJobs.map(job => (
                  <article key={job.id} className="flex items-center justify-between gap-4 rounded-xl border border-[#EDF1F6] bg-[#FBFCFE] px-4 py-3 transition hover:border-[#B9D6FF] hover:bg-white">
                    <button type="button" onClick={() => navigate('job-detail', job.id)} className="flex min-w-0 items-center gap-3 text-left">
                      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-[7px] text-lg font-black text-white" style={{ backgroundColor: job.color }}>
                        {job.company.slice(0, 1)}
                      </span>
                      <span className="min-w-0">
                        <span className="block truncate text-[11px] font-bold text-[#7A8495]">{job.company}</span>
                        <span className="mt-1 block truncate text-[14px] font-black text-black">{job.title}</span>
                        <span className="mt-1 flex items-center gap-1 text-[11px] font-semibold text-[#7A8495]">
                          <MapPin size={11} /> {job.location} · {job.type}
                        </span>
                      </span>
                    </button>
                    <span className="shrink-0 rounded-full bg-[#FFF1F1] px-2.5 py-1 text-xs font-black text-[#D92D20]">{job.deadline}</span>
                  </article>
                ))}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-[0_8px_22px_rgba(15,23,42,0.05)]">
              <div className="grid gap-5 md:grid-cols-[1fr_220px]">
                <div>
                  <h2 className="text-xl font-black text-black">공지사항</h2>
                  <div className="mt-4 space-y-3">
                    {notices.map(notice => (
                      <button key={notice.title} type="button" onClick={() => navigate('support')} className="flex w-full items-center justify-between gap-4 rounded-xl border border-[#EDF1F6] bg-[#FBFCFE] px-4 py-3 text-left hover:border-[#B9D6FF] hover:text-[#1F64E8]">
                        <span className="flex min-w-0 items-center gap-2">
                          <CheckCircle2 size={15} className="shrink-0 text-[#1F64E8]" />
                          <span className="truncate text-[13px] font-bold">{notice.title}</span>
                        </span>
                        <span className="shrink-0 text-[11px] font-semibold text-[#7A8495]">{notice.date}</span>
                      </button>
                    ))}
                  </div>
                </div>
                <div className="rounded-xl bg-gradient-to-b from-[#EEF5FF] to-[#E5F0FF] p-5">
                  <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-white text-[#1F64E8] shadow-sm">
                    <ShieldCheck size={20} />
                  </span>
                  <p className="mt-4 text-[14px] font-black text-[#1F64E8]">일이음 이용 가이드</p>
                  <p className="mt-3 text-[13px] font-bold leading-6 text-[#344054]">
                    처음 이용하시나요?
                    <br />
                    가이드를 통해
                    <br />
                    쉽게 시작해보세요!
                  </p>
                  <div className="mt-3 flex justify-end text-[#6D8EDB]">
                    <ClipboardList size={54} />
                  </div>
                  <button type="button" onClick={() => navigate('guide')} className="mt-1 rounded-[6px] bg-[#1F64E8] px-4 py-2 text-[12px] font-black text-white hover:bg-[#1754C8]">
                    자세히 보기
                  </button>
                </div>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
