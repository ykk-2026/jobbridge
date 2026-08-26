import {
  Accessibility,
  ArrowRight,
  Bookmark,
  BookmarkCheck,
  BriefcaseBusiness,
  Building2,
  CheckCircle2,
  Clock3,
  MapPin,
  Search,
  SlidersHorizontal,
  Sparkles,
  Star,
  Target,
  WalletCards,
  X,
} from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import type { Page } from '@/app/types';

interface JobsPageProps {
  mode?: 'all' | 'saved' | 'recommended';
  navigate: (page: Page, jobId?: string) => void;
  bookmarks: Set<string>;
  onBookmark: (id: string) => void;
  initialQuery?: string;
}

interface JobListing {
  id: string;
  company: string;
  initial: string;
  color: string;
  title: string;
  region: string;
  location: string;
  workType: string;
  salary: string;
  deadline: string;
  score: number;
  tags: string[];
  accessibility: string[];
}

const jobs: JobListing[] = [
  {
    id: 'job-1',
    company: 'Samsung SDS',
    initial: 'S',
    color: '#2563EB',
    title: 'Java 백엔드 개발자',
    region: '서울',
    location: '서울 송파구',
    workType: '재택근무 가능',
    salary: '월 320만원 이상',
    deadline: 'D-12',
    score: 98,
    tags: ['Spring Boot', 'AWS', '정규직'],
    accessibility: ['장애인 주차', '엘리베이터', '화상 면접'],
  },
  {
    id: 'job-2',
    company: 'kakao',
    initial: 'k',
    color: '#F97316',
    title: 'React 프론트엔드 개발자',
    region: '경기',
    location: '경기 성남시',
    workType: '하이브리드',
    salary: '월 350만원 이상',
    deadline: 'D-18',
    score: 94,
    tags: ['React', 'TypeScript', '경력 2년'],
    accessibility: ['배리어프리', '보조기기 지원', '유연근무'],
  },
  {
    id: 'job-3',
    company: 'LG CNS',
    initial: 'L',
    color: '#A855F7',
    title: '데이터 분석가',
    region: '서울',
    location: '서울 강서구',
    workType: '유연근무',
    salary: '월 300만원 이상',
    deadline: 'D-7',
    score: 92,
    tags: ['Python', 'SQL', 'Tableau'],
    accessibility: ['장애인 주차', '화상 면접', '접근성 우수'],
  },
  {
    id: 'job-4',
    company: 'Naver',
    initial: 'N',
    color: '#16A34A',
    title: '서비스 기획자',
    region: '경기',
    location: '경기 성남시',
    workType: '재택근무 가능',
    salary: '월 330만원 이상',
    deadline: 'D-20',
    score: 90,
    tags: ['서비스 기획', '데이터 분석', 'PM'],
    accessibility: ['보조기기 지원', '엘리베이터', '유연근무'],
  },
  {
    id: 'job-5',
    company: 'SK C&C',
    initial: 'SK',
    color: '#EF4444',
    title: 'UI/UX 디자이너',
    region: '서울',
    location: '서울 중구',
    workType: '유연근무',
    salary: '월 280만원 이상',
    deadline: 'D-9',
    score: 88,
    tags: ['Figma', 'UX 리서치', '디자인시스템'],
    accessibility: ['화상 면접', '장애인 주차', '접근성 확인'],
  },
  {
    id: 'job-6',
    company: 'Hyundai IT&E',
    initial: 'H',
    color: '#0EA5E9',
    title: '시스템 운영 엔지니어',
    region: '서울',
    location: '서울 영등포구',
    workType: '교대근무 없음',
    salary: '월 270만원 이상',
    deadline: 'D-5',
    score: 84,
    tags: ['Linux', '모니터링', 'ITSM'],
    accessibility: ['엘리베이터', '보조기기 지원', '접근성 확인'],
  },
  {
    id: 'job-7',
    company: 'Coupang',
    initial: 'C',
    color: '#DC2626',
    title: '물류 운영 데이터 담당자',
    region: '서울',
    location: '서울 잠실',
    workType: '시차출퇴근',
    salary: '월 290만원 이상',
    deadline: 'D-3',
    score: 86,
    tags: ['Excel', 'SQL', '운영 관리'],
    accessibility: ['엘리베이터', '장애인 주차', '장애친화'],
  },
  {
    id: 'job-8',
    company: 'Line Plus',
    initial: 'L',
    color: '#06C755',
    title: 'QA 테스트 엔지니어',
    region: '경기',
    location: '경기 성남시',
    workType: '하이브리드',
    salary: '월 310만원 이상',
    deadline: 'D-11',
    score: 89,
    tags: ['QA', '테스트 자동화', 'Jira'],
    accessibility: ['배리어프리', '보조기기 지원', '화상 면접'],
  },
  {
    id: 'job-9',
    company: 'Woowa Brothers',
    initial: 'W',
    color: '#14B8A6',
    title: '서비스 운영 매니저',
    region: '서울',
    location: '서울 송파구',
    workType: '유연근무',
    salary: '월 300만원 이상',
    deadline: 'D-14',
    score: 87,
    tags: ['서비스 운영', 'CS', '데이터 분석'],
    accessibility: ['장애인 주차', '엘리베이터', '화상 면접'],
  },
  {
    id: 'job-10',
    company: 'Toss',
    initial: 'T',
    color: '#2563EB',
    title: '고객 데이터 분석 담당자',
    region: '서울',
    location: '서울 강남구',
    workType: '재택근무 가능',
    salary: '월 340만원 이상',
    deadline: 'D-6',
    score: 93,
    tags: ['SQL', 'Python', '데이터 분석'],
    accessibility: ['보조기기 지원', '접근성 우수', '유연근무'],
  },
];

const partTimeJobs: JobListing[] = [
  {
    id: 'job-11',
    company: '스타벅스 코리아',
    initial: 'S',
    color: '#00704A',
    title: '매장 바리스타 알바',
    region: '서울',
    location: '서울 마포구',
    workType: '파트타임 알바',
    salary: '시급 12,000원',
    deadline: 'D-3',
    score: 92,
    tags: ['알바', '고객응대', '오늘 등록'],
    accessibility: ['유연근무', '엘리베이터', '보조기기 지원'],
  },
  {
    id: 'job-12',
    company: '올리브영',
    initial: 'O',
    color: '#65A30D',
    title: '매장 진열 및 계산 보조 알바',
    region: '경기',
    location: '경기 성남시',
    workType: '주말 알바',
    salary: '시급 11,500원',
    deadline: 'D-6',
    score: 88,
    tags: ['알바', '매장관리', '초보 가능'],
    accessibility: ['장애인 주차', '유연근무', '화상 면접'],
  },
  {
    id: 'job-13',
    company: '쿠팡풀필먼트',
    initial: 'C',
    color: '#DC2626',
    title: '물류센터 포장 검수 알바',
    region: '인천',
    location: '인천 서구',
    workType: '단기 알바',
    salary: '일급 105,000원',
    deadline: 'D-2',
    score: 84,
    tags: ['알바', '단기근무', '운영'],
    accessibility: ['장애인 주차', '엘리베이터', '보조기기 지원'],
  },
  {
    id: 'job-14',
    company: '배달의민족 B마트',
    initial: 'B',
    color: '#14B8A6',
    title: '상품 피킹 및 재고 정리 알바',
    region: '부산',
    location: '부산 해운대구',
    workType: '오후 알바',
    salary: '시급 12,300원',
    deadline: 'D-9',
    score: 86,
    tags: ['알바', '재고관리', '운영'],
    accessibility: ['유연근무', '장애인 주차', '엘리베이터'],
  },
  {
    id: 'job-15',
    company: '교보문고',
    initial: 'K',
    color: '#2563EB',
    title: '도서 정리 및 고객 안내 알바',
    region: '대구',
    location: '대구 중구',
    workType: '평일 알바',
    salary: '시급 11,200원',
    deadline: 'D-12',
    score: 90,
    tags: ['알바', '고객지원', '오늘 등록'],
    accessibility: ['화상 면접', '보조기기 지원', '유연근무'],
  },
];

const allJobs = [...jobs, ...partTimeJobs];
const regions = ['전체', '서울', '경기', '인천', '부산', '대구', '전국'];
const companies = ['전체', ...Array.from(new Set(allJobs.map(job => job.company)))];
const quickFilters = [
  { label: '알바 공고', query: '알바', icon: BriefcaseBusiness },
  { label: '재택근무', query: '재택근무', icon: BriefcaseBusiness },
  { label: '하이브리드', query: '하이브리드', icon: BriefcaseBusiness },
  { label: '유연근무', query: '유연근무', icon: BriefcaseBusiness },
  { label: '장애인 주차', query: '장애인 주차', icon: Accessibility },
  { label: '보조기기 지원', query: '보조기기', icon: Accessibility },
  { label: '화상 면접', query: '화상 면접', icon: Search },
];

const getDeadlineDays = (deadline: string) => Number(deadline.replace('D-', ''));

export function JobsPage({ mode = 'all', navigate, bookmarks, onBookmark, initialQuery = '' }: JobsPageProps) {
  const [query, setQuery] = useState(initialQuery);
  const [selectedRegion, setSelectedRegion] = useState('전체');
  const [selectedCompany, setSelectedCompany] = useState('전체');
  const [companyPanelOpen, setCompanyPanelOpen] = useState(false);
  const [urgentOnly, setUrgentOnly] = useState(false);
  const [recommendedOnly, setRecommendedOnly] = useState(false);
  const [sort, setSort] = useState<'latest' | 'deadline'>('latest');
  const submitSearch = () => {
    setCompanyPanelOpen(false);
  };

  useEffect(() => {
    if (initialQuery === 'D-3') {
      setQuery('');
      setUrgentOnly(true);
      setSort('deadline');
      setCompanyPanelOpen(false);
      return;
    }

    setQuery(initialQuery);
    setUrgentOnly(false);
    setCompanyPanelOpen(false);
  }, [initialQuery]);

  const filteredJobs = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    return allJobs
      .filter(job => {
        if (mode === 'saved' && !bookmarks.has(job.id)) return false;
        if (mode === 'recommended' && job.score < 90) return false;
        if (selectedRegion !== '전체' && selectedRegion !== '전국' && job.region !== selectedRegion) return false;
        if (selectedCompany !== '전체' && job.company !== selectedCompany) return false;
        if (urgentOnly && getDeadlineDays(job.deadline) > 7) return false;
        if (recommendedOnly && job.score < 90) return false;
        if (!normalizedQuery) return true;

        const target = [job.title, job.company, job.location, job.workType, job.salary, job.deadline, ...job.tags, ...job.accessibility].join(' ').toLowerCase();
        return target.includes(normalizedQuery);
      })
      .sort((a, b) => {
        if (sort === 'latest') return Number(b.id.replace('job-', '')) - Number(a.id.replace('job-', ''));
        return Number(a.deadline.replace('D-', '')) - Number(b.deadline.replace('D-', ''));
      });
  }, [bookmarks, mode, query, selectedCompany, selectedRegion, sort, urgentOnly, recommendedOnly]);

  const resetFilters = () => {
    setQuery('');
    setSelectedRegion('전체');
    setSelectedCompany('전체');
    setUrgentOnly(false);
    setRecommendedOnly(false);
    setSort('latest');
  };

  const title = mode === 'saved' ? '관심공고' : mode === 'recommended' ? 'AI 추천 일자리' : '채용정보';
  const hasActiveFilters = Boolean(query.trim()) || selectedRegion !== '전체' || selectedCompany !== '전체' || urgentOnly || recommendedOnly || sort !== 'latest';
  const recommendedJobs = useMemo(() => [...allJobs].sort((a, b) => b.score - a.score).slice(0, 4), []);

  return (
    <div className="min-h-screen bg-[#F6F7F9] text-[#111827]">
      <main className="mx-auto max-w-[1256px] px-5 py-5 sm:px-8">
        <section className="overflow-hidden rounded-xl border border-[#DDE3EA] bg-white shadow-sm">
          <div className="bg-[#EEF5FF] px-5 py-6 sm:px-7">
            <div className="grid gap-5 lg:grid-cols-[1fr_320px] lg:items-center">
              <div>
                <span className="inline-flex items-center gap-1.5 rounded-full bg-white px-4 py-1.5 text-xs font-extrabold text-[#0D6BEA] shadow-sm">
                  <Sparkles size={14} />
                  장애친화 채용
                </span>
                <h1 className="mt-3 text-3xl font-extrabold leading-tight text-black">{title}</h1>
                <p className="mt-2 max-w-xl text-sm font-semibold leading-6 text-[#596273]">
                  지역, 기업, 직무, 접근성 조건을 조합해서 바로 지원할 공고를 찾아보세요.
                </p>
                <div className="mt-5 flex flex-wrap gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      setRecommendedOnly(prev => !prev);
                      setSort('latest');
                    }}
                    aria-pressed={recommendedOnly}
                    className={`inline-flex h-11 items-center gap-2 rounded-lg px-4 text-sm font-extrabold ${
                      recommendedOnly ? 'bg-[#0D6BEA] text-white' : 'bg-white text-[#0D6BEA] shadow-sm hover:bg-[#F8FAFC]'
                    }`}
                  >
                    <Target size={17} />
                    AI 추천만 보기
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setUrgentOnly(prev => !prev);
                      setSort('deadline');
                    }}
                    aria-pressed={urgentOnly}
                    className={`inline-flex h-11 items-center gap-2 rounded-lg px-4 text-sm font-extrabold ${
                      urgentOnly ? 'bg-[#D92D20] text-white' : 'bg-white text-[#D92D20] shadow-sm hover:bg-[#F8FAFC]'
                    }`}
                  >
                    <Clock3 size={17} />
                    마감 임박
                  </button>
                  <button
                    type="button"
                    onClick={() => setCompanyPanelOpen(prev => !prev)}
                    className="inline-flex h-11 items-center gap-2 rounded-lg bg-white px-4 text-sm font-extrabold text-[#344054] shadow-sm hover:bg-[#F8FAFC]"
                  >
                    기업 선택 <ArrowRight size={16} />
                  </button>
                </div>
              </div>

              <div className="rounded-xl border border-[#D7E6FF] bg-white p-4 shadow-sm">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-extrabold text-black">오늘의 추천 요약</p>
                  <span className="rounded-full bg-[#E7F8EF] px-2.5 py-1 text-xs font-extrabold text-[#14843C]">LIVE</span>
                </div>
                <div className="mt-4 grid grid-cols-3 gap-2">
                  {[
                    ['추천', `${allJobs.filter(job => job.score >= 90).length}건`],
                    ['마감임박', `${allJobs.filter(job => getDeadlineDays(job.deadline) <= 7).length}건`],
                    ['알바', `${partTimeJobs.length}건`],
                  ].map(([label, value]) => (
                    <button
                      type="button"
                      key={label}
                      onClick={() => {
                        if (label === '추천') setRecommendedOnly(true);
                        if (label === '마감임박') {
                          setUrgentOnly(true);
                          setSort('deadline');
                        }
                        if (label === '알바') setQuery('알바');
                      }}
                      className="rounded-lg bg-[#F8FAFC] px-3 py-3 text-left hover:bg-[#EEF5FF]"
                    >
                      <span className="block text-xs font-bold text-[#7A8495]">{label}</span>
                      <span className="mt-1 block text-lg font-black text-[#0D6BEA]">{value}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          <div className="grid gap-3 px-5 pb-5 pt-5 lg:grid-cols-[1fr_180px]">
            <div className="relative">
              <Search size={19} className="absolute left-4 top-1/2 -translate-y-1/2 text-black" />
              <input
                value={query}
                onChange={event => setQuery(event.target.value)}
                onKeyDown={event => {
                  if (event.key === 'Enter') submitSearch();
                }}
                placeholder="직무, 회사, 지역, 편의시설 검색"
                className="h-12 w-full rounded-lg border border-[#D7DDE5] bg-white pl-11 pr-4 text-sm font-semibold outline-none placeholder:text-[#8A94A6] focus:ring-4 focus:ring-[#0D6BEA]/15"
              />
            </div>
            <button
              type="button"
              onClick={() => setSort(sort === 'latest' ? 'deadline' : 'latest')}
              className="inline-flex h-12 items-center justify-center gap-2 rounded-lg border border-[#D7DDE5] bg-white text-sm font-bold hover:bg-[#F8FAFC]"
            >
              <SlidersHorizontal size={17} />
              {sort === 'latest' ? '등록순' : '마감순'}
            </button>
          </div>

          {hasActiveFilters && (
            <div className="flex flex-wrap items-center gap-2 border-t border-[#E7ECF2] px-5 pb-5 pt-4">
              <span className="text-xs font-extrabold text-[#7A8495]">적용 조건</span>
              {query.trim() && (
                <button type="button" onClick={() => setQuery('')} className="inline-flex items-center gap-1 rounded-full bg-[#EEF5FF] px-3 py-1 text-xs font-extrabold text-[#0D6BEA]">
                  검색: {query.trim()} <X size={13} />
                </button>
              )}
              {selectedRegion !== '전체' && (
                <button type="button" onClick={() => setSelectedRegion('전체')} className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1 text-xs font-extrabold text-[#344054]">
                  지역: {selectedRegion} <X size={13} />
                </button>
              )}
              {selectedCompany !== '전체' && (
                <button type="button" onClick={() => setSelectedCompany('전체')} className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1 text-xs font-extrabold text-[#344054]">
                  기업: {selectedCompany} <X size={13} />
                </button>
              )}
              {urgentOnly && (
                <button type="button" onClick={() => setUrgentOnly(false)} className="inline-flex items-center gap-1 rounded-full bg-[#FFF1F1] px-3 py-1 text-xs font-extrabold text-[#D92D20]">
                  마감 7일 이내 <X size={13} />
                </button>
              )}
              {recommendedOnly && (
                <button type="button" onClick={() => setRecommendedOnly(false)} className="inline-flex items-center gap-1 rounded-full bg-[#EEF5FF] px-3 py-1 text-xs font-extrabold text-[#0D6BEA]">
                  AI 추천 90% 이상 <X size={13} />
                </button>
              )}
              {sort !== 'latest' && (
                <button type="button" onClick={() => setSort('latest')} className="inline-flex items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1 text-xs font-extrabold text-[#344054]">
                  마감순 <X size={13} />
                </button>
              )}
              <button type="button" onClick={resetFilters} className="ml-auto text-xs font-extrabold text-[#596273] hover:text-[#0D6BEA]">
                조건 초기화
              </button>
            </div>
          )}
        </section>

        {mode === 'all' && (
          <section className="mt-5 rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-extrabold text-black">AI가 추천하는 일자리</h2>
                <p className="mt-1 text-xs font-bold text-[#7A8495]">매칭 점수가 높은 공고를 먼저 모았습니다.</p>
              </div>
              <button
                type="button"
                onClick={() => {
                  setRecommendedOnly(true);
                  setSort('latest');
                }}
                className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]"
              >
                추천만 보기 <ArrowRight size={15} />
              </button>
            </div>

            <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
              {recommendedJobs.map(job => (
                <article key={job.id} className="rounded-lg border border-[#E1E6EE] bg-white p-4 transition hover:border-[#B9D6FF] hover:shadow-sm">
                  <div className="flex items-start justify-between gap-3">
                    <button type="button" onClick={() => navigate('job-detail', job.id)} className="flex min-w-0 gap-3 text-left">
                      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-sm font-black text-white" style={{ backgroundColor: job.color }}>
                        {job.initial}
                      </span>
                      <span className="min-w-0">
                        <span className="block truncate text-xs font-extrabold text-[#596273]">{job.company}</span>
                        <span className="mt-1 block truncate text-sm font-extrabold text-black">{job.title}</span>
                        <span className="mt-2 inline-flex rounded-full bg-[#E7F8EF] px-2.5 py-1 text-xs font-extrabold text-[#14843C]">
                          매칭 {job.score}%
                        </span>
                      </span>
                    </button>
                    <button
                      type="button"
                      aria-label={bookmarks.has(job.id) ? '관심 공고 해제' : '관심 공고 저장'}
                      onClick={() => onBookmark(job.id)}
                      className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg border border-[#D7DDE5] text-[#596273] hover:bg-[#F8FAFC]"
                    >
                      {bookmarks.has(job.id) ? <BookmarkCheck size={15} /> : <Bookmark size={15} />}
                    </button>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {[job.location, job.workType].map(item => (
                      <span key={item} className="rounded-full bg-[#F1F3F6] px-2.5 py-1 text-[11px] font-bold text-[#596273]">
                        {item}
                      </span>
                    ))}
                  </div>
                </article>
              ))}
            </div>
          </section>
        )}

        <div className="mt-5 grid gap-5 xl:grid-cols-[1fr_320px]">
          <section className="space-y-4">
            <div className="flex items-center justify-between rounded-xl border border-[#DDE3EA] bg-white px-5 py-4 shadow-sm">
              <p className="text-sm font-bold text-[#596273]">
                총 <span className="text-[#0D6BEA]">{filteredJobs.length}</span>개 공고
              </p>
              <p className="text-xs font-semibold text-[#8A94A6]">검색과 필터가 바로 반영됩니다.</p>
            </div>

            {filteredJobs.length > 0 ? (
              filteredJobs.map(job => (
                <article key={job.id} className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm transition hover:border-[#B9D6FF] hover:shadow-md">
                  <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                    <button type="button" onClick={() => navigate('job-detail', job.id)} className="flex min-w-0 flex-1 gap-4 text-left">
                      <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg text-lg font-extrabold text-white" style={{ backgroundColor: job.color }}>
                        {job.initial}
                      </span>
                      <span className="min-w-0 flex-1">
                        <span className="block truncate text-sm font-extrabold text-[#596273]">{job.company}</span>
                        <span className="mt-1 block text-xl font-extrabold leading-7 text-black">{job.title}</span>

                        <span className="mt-3 flex flex-wrap gap-x-4 gap-y-2 text-sm font-bold text-[#596273]">
                          <span className="inline-flex items-center gap-1.5">
                            <MapPin size={15} className="text-[#7A8495]" />
                            {job.location}
                          </span>
                          <span className="inline-flex items-center gap-1.5">
                            <BriefcaseBusiness size={15} className="text-[#7A8495]" />
                            {job.workType}
                          </span>
                          <span className="inline-flex items-center gap-1.5">
                            <WalletCards size={15} className="text-[#7A8495]" />
                            {job.salary}
                          </span>
                          <span className={`font-extrabold ${getDeadlineDays(job.deadline) <= 3 ? 'text-[#D92D20]' : 'text-[#0D6BEA]'}`}>
                            {job.deadline}
                          </span>
                        </span>
                      </span>
                    </button>

                    <div className="flex shrink-0 gap-2 lg:justify-end">
                      <button
                        type="button"
                        aria-label={bookmarks.has(job.id) ? '관심 공고 해제' : '관심 공고 저장'}
                        onClick={() => onBookmark(job.id)}
                        className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-[#D7DDE5] bg-white text-[#344054] hover:bg-[#F8FAFC]"
                      >
                        {bookmarks.has(job.id) ? <BookmarkCheck size={17} /> : <Bookmark size={17} />}
                      </button>
                      <button
                        type="button"
                        onClick={() => navigate('job-detail', job.id)}
                        className="inline-flex h-10 items-center justify-center gap-1 rounded-lg bg-[#0D6BEA] px-4 text-sm font-extrabold text-white hover:bg-[#0959C7]"
                      >
                        상세 보기 <ArrowRight size={15} />
                      </button>
                    </div>
                  </div>

                  <div className="mt-4 flex flex-wrap gap-2 border-t border-[#E7ECF2] pt-4">
                    {job.tags.slice(0, 3).map(tag => (
                      <span key={tag} className="rounded-full bg-[#EEF5FF] px-3 py-1 text-xs font-extrabold text-[#0D6BEA]">
                        {tag}
                      </span>
                    ))}
                    {job.accessibility.slice(0, 3).map(item => (
                      <span key={item} className="inline-flex items-center gap-1 rounded-full bg-[#EAF8F8] px-3 py-1 text-xs font-extrabold text-[#217A83]">
                        <Accessibility size={13} />
                        {item}
                      </span>
                    ))}
                  </div>
                </article>
              ))
            ) : (
              <div className="rounded-xl border border-[#DDE3EA] bg-white p-10 text-center shadow-sm">
                <p className="text-lg font-extrabold text-black">조건에 맞는 공고가 없습니다.</p>
                <button type="button" onClick={resetFilters} className="mt-4 rounded-lg bg-[#0D6BEA] px-5 py-2.5 text-sm font-bold text-white">
                  조건 초기화
                </button>
              </div>
            )}
          </section>

          <aside className="space-y-5">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
              <div className="mb-4 flex items-center gap-2">
                <MapPin size={20} className="text-[#596273]" />
                <h2 className="text-lg font-extrabold text-black">지역별 채용</h2>
              </div>
              <div className="grid grid-cols-2 gap-2.5">
                {regions.map(region => (
                  <button
                    type="button"
                    key={region}
                    onClick={() => setSelectedRegion(region)}
                    className={`rounded-full px-3 py-2 text-xs font-extrabold transition ${
                      selectedRegion === region ? 'bg-[#0D6BEA] text-white' : 'bg-[#F1F3F6] text-black hover:bg-[#E4EFFF]'
                    }`}
                  >
                    {region}
                  </button>
                ))}
              </div>
            </section>

            {companyPanelOpen && (
              <section className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
                <div className="mb-4 flex items-center gap-2">
                  <Building2 size={20} className="text-[#596273]" />
                  <h2 className="text-lg font-extrabold text-black">기업별 채용</h2>
                </div>
                <div className="space-y-2">
                  {companies.map(company => (
                    <button
                      type="button"
                      key={company}
                      onClick={() => setSelectedCompany(company)}
                      className={`flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm font-bold ${
                        selectedCompany === company ? 'bg-[#E4EFFF] text-[#0D6BEA]' : 'bg-[#F8FAFC] text-black hover:bg-[#F1F3F6]'
                      }`}
                    >
                      {company}
                      {selectedCompany === company && <CheckCircle2 size={16} />}
                    </button>
                  ))}
                </div>
              </section>
            )}

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
              <div className="mb-4 flex items-center gap-2">
                <Star size={20} className="text-[#596273]" />
                <h2 className="text-lg font-extrabold text-black">빠른 조건</h2>
              </div>
              <div className="grid grid-cols-1 gap-2 sm:grid-cols-2 xl:grid-cols-1">
                {quickFilters.map(filter => {
                  const Icon = filter.icon;
                  return (
                    <button
                      type="button"
                      key={filter.label}
                      onClick={() => {
                        setQuery(filter.query);
                        setSelectedCompany('전체');
                      }}
                      className="flex w-full items-center gap-2 rounded-lg bg-[#F8FAFC] px-3 py-2 text-sm font-bold hover:bg-[#E4EFFF]"
                    >
                      <Icon size={16} /> {filter.label}
                    </button>
                  );
                })}
              </div>
            </section>
          </aside>
        </div>
      </main>
    </div>
  );
}
