import {
  Accessibility,
  ArrowRight,
  Bookmark,
  BookmarkCheck,
  BriefcaseBusiness,
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
  Sparkles,
  Target,
  UserRound,
} from 'lucide-react';
import type { Page } from '@/app/types';

interface MainPageProps {
  navigate: (page: Page, jobId?: string) => void;
  bookmarks: Set<string>;
  onBookmark: (id: string) => void;
  onSearch: (query: string) => void;
}

const recommendedJobs = [
  { id: 'job-1', company: 'ABC테크', initial: 'A', color: '#53657F', title: 'Java 백엔드 개발자', location: '서울 강남구', match: 98, tags: ['정규직', '재택근무 가능'] },
  { id: 'job-2', company: '대한소프트', initial: 'D', color: '#48A6D8', title: '웹 퍼블리셔', location: '서울 마포구', match: 88, tags: ['계약직', '유연근무'] },
  { id: 'job-4', company: '리유', initial: 'R', color: '#4FC3A1', title: 'UI/UX 디자이너', location: '서울 영등포구', match: 95, tags: ['정규직', '시각장애 우대'] },
  { id: 'job-10', company: '해피', initial: 'H', color: '#F5A84B', title: '고객 상담 매니저', location: '부산 해운대구', match: 92, tags: ['정규직', '청각장애 우대'] },
];

const quickActions = [
  { label: 'AI 맞춤 추천', helper: '내게 맞는 일자리 추천', icon: Target, action: 'ai' },
  { label: '간편 지원', helper: '이력서 없이 간편하게 지원', icon: FileText, action: 'apply' },
  { label: '관심 공고', helper: '관심 공고를 한눈에 관리', icon: Bookmark, action: 'saved' },
  { label: '지원 현황', helper: '지원한 공고의 진행 상황 확인', icon: BriefcaseBusiness, action: 'applications' },
  { label: '커뮤니티', helper: '정보 공유와 합격 소통', icon: MessageCircle, action: 'support' },
];

const accessibilityJobs = [
  { label: '휠체어 접근 가능', icon: Accessibility, query: '휠체어' },
  { label: '재택근무 가능', icon: Home, query: '재택근무' },
  { label: '유연근무 가능', icon: Clock3, query: '유연근무' },
  { label: '장애인 주차시설', icon: ParkingSquare, query: '장애인 주차' },
  { label: '보조공학기기 지원', icon: Monitor, query: '보조기기' },
];

const latestJobs = [
  { id: 'job-15', company: '교보문고', title: '도서 정리 및 고객 안내 알바', location: '대구 중구', type: '파트타임', deadline: 'D-7', color: '#22C55E' },
  { id: 'job-12', company: 'XYZ 컴퍼니', title: '매장 진열 및 계산 보조 알바', location: '경기 성남시', type: '주말 알바', deadline: 'D-6', color: '#7C3AED' },
  { id: 'job-3', company: 'LG CNS', title: '데이터 분석가', location: '서울 강서구', type: '정규직', deadline: 'D-7', color: '#A855F7' },
];

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
  const runQuickAction = (action: (typeof quickActions)[number]['action']) => {
    if (action === 'ai') navigate('ai-recommend');
    if (action === 'apply') onSearch('간편 지원');
    if (action === 'saved') navigate('saved');
    if (action === 'applications') navigate('user-dashboard');
    if (action === 'support') navigate('support');
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
          <section className="rounded-b-[8px] border border-t-0 border-[#DDE3EA] bg-white p-4 shadow-[0_2px_8px_rgba(15,23,42,0.04)]">
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <div className="flex flex-wrap items-end gap-3">
                <h2 className="text-[19px] font-black text-black">AI가 추천하는 일자리</h2>
                <p className="pb-0.5 text-[12px] font-semibold text-[#7A8495]">회원님의 프로필을 기반으로 추천했어요</p>
              </div>
              <button type="button" onClick={() => navigate('ai-recommend')} className="inline-flex items-center gap-1 text-[12px] font-extrabold text-[#1F64E8]">
                더 많은 추천 보기 <ArrowRight size={14} />
              </button>
            </div>

            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              {recommendedJobs.map(job => (
                <article key={job.id} className="flex min-h-[148px] flex-col justify-between rounded-[8px] border border-[#E1E6EE] bg-white p-4 transition hover:border-[#B9D6FF] hover:shadow-sm">
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
                </article>
              ))}
            </div>
          </section>

          <div className="mt-4 grid gap-4 lg:grid-cols-2">
            <section className="rounded-[8px] border border-[#DDE3EA] bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.04)]">
              <h2 className="text-[19px] font-black text-black">일이음을 더 편리하게 이용하세요</h2>
              <div className="mt-5 grid grid-cols-2 gap-3 sm:grid-cols-5">
                {quickActions.map(item => {
                  const Icon = item.icon;
                  return (
                    <button key={item.label} type="button" onClick={() => runQuickAction(item.action)} className="min-h-[96px] rounded-[8px] px-2 py-1 text-center hover:bg-[#F7FAFF]">
                      <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-[10px] bg-[#EEF3FF] text-[#1F64E8]">
                        <Icon size={23} />
                      </span>
                      <span className="mt-3 block text-[13px] font-black text-black">{item.label}</span>
                      <span className="mt-1 block text-[10px] font-semibold leading-4 text-[#7A8495]">{item.helper}</span>
                    </button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-[8px] border border-[#DDE3EA] bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.04)]">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <h2 className="text-[19px] font-black text-black">접근성 좋은 일자리</h2>
                  <p className="mt-1 text-[11px] font-semibold text-[#7A8495]">장애 유형에 맞는 근무환경을 확인하세요</p>
                </div>
                <button type="button" onClick={() => onSearch('접근성')} className="inline-flex shrink-0 items-center gap-1 text-[12px] font-extrabold text-[#1F64E8]">
                  전체 보기 <ArrowRight size={14} />
                </button>
              </div>
              <div className="mt-5 grid grid-cols-2 gap-3 sm:grid-cols-5">
                {accessibilityJobs.map(item => {
                  const Icon = item.icon;
                  return (
                    <button key={item.label} type="button" onClick={() => onSearch(item.query)} className="min-h-[96px] rounded-[8px] px-2 py-1 text-center hover:bg-[#F4FBFA]">
                      <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-[10px] bg-[#EAF8F3] text-[#1B9A7E]">
                        <Icon size={23} />
                      </span>
                      <span className="mt-3 block text-[12px] font-black leading-4 text-black">{item.label}</span>
                    </button>
                  );
                })}
              </div>
            </section>
          </div>

          <div className="mt-4 grid gap-4 lg:grid-cols-2">
            <section className="rounded-[8px] border border-[#DDE3EA] bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.04)]">
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-[19px] font-black text-black">최신 채용공고</h2>
                <button type="button" onClick={() => onSearch('')} className="inline-flex items-center gap-1 text-[12px] font-extrabold text-[#1F64E8]">
                  전체 보기 <ArrowRight size={14} />
                </button>
              </div>
              <div className="mb-2 flex flex-wrap gap-x-8 gap-y-2 border-b border-[#E7ECF2] pb-3 text-[12px] font-bold text-[#475467]">
                {['전체', 'IT/개발', '사무/관리', '서비스', '디자인', '제조'].map((tab, index) => (
                  <button key={tab} type="button" onClick={() => onSearch(index === 0 ? '' : tab)} className={index === 0 ? 'text-[#1F64E8]' : 'hover:text-[#1F64E8]'}>
                    {tab}
                  </button>
                ))}
              </div>
              <div className="divide-y divide-[#E7ECF2]">
                {latestJobs.map(job => (
                  <article key={job.id} className="flex items-center justify-between gap-4 py-4">
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
                    <span className="shrink-0 text-[12px] font-black text-[#D92D20]">{job.deadline}</span>
                  </article>
                ))}
              </div>
            </section>

            <section className="rounded-[8px] border border-[#DDE3EA] bg-white p-5 shadow-[0_2px_8px_rgba(15,23,42,0.04)]">
              <div className="grid gap-5 md:grid-cols-[1fr_210px]">
                <div>
                  <h2 className="text-[19px] font-black text-black">공지사항</h2>
                  <div className="mt-4 divide-y divide-[#E7ECF2]">
                    {notices.map(notice => (
                      <button key={notice.title} type="button" onClick={() => navigate('support')} className="flex w-full items-center justify-between gap-4 py-4 text-left hover:text-[#1F64E8]">
                        <span className="truncate text-[13px] font-bold">{notice.title}</span>
                        <span className="shrink-0 text-[11px] font-semibold text-[#7A8495]">{notice.date}</span>
                      </button>
                    ))}
                  </div>
                </div>
                <div className="rounded-[8px] bg-[#EEF5FF] p-5">
                  <p className="text-[14px] font-black text-[#1F64E8]">일이음 이용 가이드</p>
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
                  <button type="button" onClick={() => navigate('support')} className="mt-1 rounded-[6px] bg-[#1F64E8] px-4 py-2 text-[12px] font-black text-white hover:bg-[#1754C8]">
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
