import {
  Accessibility,
  ArrowRight,
  BarChart3,
  Bell,
  BriefcaseBusiness,
  Building2,
  CalendarDays,
  CircleUserRound,
  Clock3,
  Home,
  MapPin,
  Search,
  ShieldCheck,
  Star,
} from 'lucide-react';
import { useState } from 'react';
import type { Page } from '@/app/types';

interface MainPageProps {
  navigate: (page: Page, jobId?: string) => void;
  bookmarks: Set<string>;
  onBookmark: (id: string) => void;
}

const searchTags = ['원격/재택', '재택근무', '장애인 주차', '일반데이터', '화상 면접', '서울 개발자'];

const recommendedJobs = [
  { title: 'Java 백엔드 개발자', location: '서울 송파구', score: 98 },
  { title: 'React 프론트엔드 개발자', location: '경기 성남시', score: 94 },
  { title: '데이터 분석가', location: '서울 강서구', score: 92 },
];

const quickMenus = [
  { label: '지역별', helper: '가까운 공고', icon: MapPin },
  { label: '재택 가능', helper: '출퇴근 부담 없이', icon: Home },
  { label: '장애친화', helper: '접근성 확인', icon: Accessibility },
  { label: '오늘 마감', helper: '바로 지원', icon: CalendarDays },
  { label: '급구', helper: '빠른 채용', icon: Bell },
  { label: '추천순', helper: '조건 매칭', icon: Star },
];

const todayStats = [
  { title: '접근성 검증 완료', count: '312건', helper: '시설 사진과 상세 조건 제공', icon: ShieldCheck },
  { title: '재택/하이브리드', count: '486건', helper: '출퇴근 부담을 줄이는 공고', icon: Home },
  { title: '오늘 뜬 공고', count: '94건', helper: '최근 등록순으로 확인', icon: Clock3 },
  { title: '즉시 지원 가능', count: '139건', helper: '간편 지원 기업', icon: BriefcaseBusiness },
];

const companies = [
  { name: 'Samsung SDS', count: '채용 242건', tags: ['장애인 우대', '재택 가능'], color: '#2563EB' },
  { name: 'kakao', count: '채용 18건', tags: ['하이브리드', '화상 면접'], color: '#F97316' },
  { name: 'Naver', count: '채용 21건', tags: ['보조기기 지원', '접근성 우수'], color: '#16A34A' },
  { name: 'LG CNS', count: '채용 15건', tags: ['장애인 주차', '유연근무'], color: '#A855F7' },
];

const profiles = [
  { name: '김민서', age: '24세', role: 'Java 백엔드 개발자', location: '서울', career: '경력 2년', score: 92, tags: ['재택근무 가능', '장애인 주차'], initial: '김' },
  { name: '이준호', age: '27세', role: 'React 프론트엔드 개발자', location: '경기 성남', career: '경력 3년', score: 89, tags: ['하이브리드', '배리어프리'], initial: '이' },
  { name: '박서준', age: '29세', role: '데이터 분석가', location: '서울 강서', career: '경력 2년', score: 87, tags: ['보조기기 지원', '화상면접'], initial: '박' },
];

const regions = ['서울', '경기', '인천', '부산', '대구', '대전', '광주', '울산', '세종', '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주', '전국'];

const bottomStats = [
  { label: '가입 사용자', value: '12,847', icon: CircleUserRound },
  { label: '진행 중인 채용', value: '4,218', icon: BarChart3 },
  { label: '추천 만족도', value: '98%', icon: Star },
];

export function MainPage({ navigate }: MainPageProps) {
  const [query, setQuery] = useState('');
  const goJobs = () => navigate('jobs');

  return (
    <div className="min-h-screen bg-[#F6F7F9] text-[#111827]">
      <main className="mx-auto max-w-[1256px] px-5 py-5 sm:px-8">
        <section className="grid gap-4 rounded-xl border border-[#DDE3EA] bg-[#F4F8FC] p-4 shadow-sm lg:grid-cols-[1fr_420px] lg:p-4">
          <div className="flex min-w-0 flex-col justify-start">
            <div className="mb-3 flex flex-wrap gap-2.5">
              <span className="rounded-full bg-[#E4EFFF] px-4 py-1.5 text-xs font-bold text-[#0D6BEA]">장애친화 채용</span>
              <span className="rounded-full bg-[#E7F8EF] px-4 py-1.5 text-xs font-bold text-[#14843C]">접근성 조건 확인</span>
            </div>

            <h1 className="text-[27px] font-extrabold leading-[1.22] tracking-normal text-black sm:text-[30px] lg:text-[32px]">
              일하기 편한 조건으로
              <br />
              공고를 찾아보세요
            </h1>
            <p className="mt-3 text-sm font-semibold text-[#7A8495] sm:text-base">
              지역, 직무, 근무방식, 편의시설을 한 번에 검색할 수 있습니다.
            </p>

            <div className="mt-4 flex flex-col gap-3 sm:flex-row">
              <div className="relative min-w-0 flex-1">
                <Search size={20} strokeWidth={2.6} className="absolute left-5 top-1/2 -translate-y-1/2 text-black" />
                <input
                  value={query}
                  onChange={event => setQuery(event.target.value)}
                  placeholder="직무, 회사, 지역, 편의시설 검색"
                  className="h-12 w-full rounded-lg border border-[#D7DDE5] bg-white pl-12 pr-4 text-sm font-semibold text-[#111827] outline-none placeholder:text-[#8A94A6] focus:ring-4 focus:ring-[#0D6BEA]/15"
                />
              </div>
              <button
                type="button"
                onClick={goJobs}
                className="inline-flex h-12 items-center justify-center gap-2 rounded-lg bg-[#0D6BEA] px-6 text-base font-bold text-white shadow-sm hover:bg-[#0959C7]"
              >
                검색 <ArrowRight size={20} />
              </button>
            </div>

            <div className="mt-3 flex flex-wrap gap-2.5">
              {searchTags.map(tag => (
                <button
                  type="button"
                  key={tag}
                  onClick={goJobs}
                  className="rounded-full border border-[#D8DEE7] bg-white px-3.5 py-1.5 text-xs font-bold text-[#344054] shadow-sm hover:border-[#0D6BEA] hover:text-[#0D6BEA]"
                >
                  #{tag}
                </button>
              ))}
            </div>
          </div>

          <aside className="rounded-xl border border-[#DDE3EA] bg-white p-3 shadow-sm">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold text-[#8A94A6]">내 프로필 기준</p>
                <h2 className="mt-1 text-lg font-extrabold text-[#111827]">추천 공고 42건</h2>
              </div>
              <span className="rounded-full bg-[#DDFCEB] px-3 py-1 text-sm font-extrabold text-[#14843C]">98%</span>
            </div>

            <div className="mt-3 space-y-2">
              {recommendedJobs.map(job => (
                <button
                  type="button"
                  key={job.title}
                  onClick={goJobs}
                  className="w-full rounded-lg border border-[#E1E6EE] bg-[#F8FAFC] px-3 py-2 text-left transition hover:border-[#0D6BEA] hover:bg-white"
                >
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-sm font-extrabold text-[#26324D]">{job.title}</p>
                    <span className="text-sm font-extrabold text-[#14843C]">{job.score}%</span>
                  </div>
                  <p className="mt-1 text-xs font-semibold text-[#8A94A6]">{job.location} · 접근성 조건 일치</p>
                </button>
              ))}
            </div>

            <button type="button" onClick={goJobs} className="mt-3 inline-flex w-full items-center justify-center gap-2 text-sm font-extrabold text-[#0D6BEA]">
              전체 보기 <ArrowRight size={16} />
            </button>
          </aside>
        </section>

        <div className="mt-5 grid gap-5 xl:grid-cols-[1fr_340px]">
          <div className="space-y-5">
            <section className="rounded-xl border border-[#DDE3EA] bg-white px-4 py-3 shadow-sm">
              <div className="grid grid-cols-2 divide-y divide-[#E7ECF2] md:grid-cols-6 md:divide-x md:divide-y-0">
                {quickMenus.map(item => {
                  const Icon = item.icon;
                  return (
                    <button type="button" key={item.label} onClick={goJobs} className="px-3 py-4 text-center transition hover:bg-[#F8FAFC]">
                      <Icon size={26} strokeWidth={2.2} className="mx-auto mb-2 text-[#596273]" />
                      <p className="text-base font-extrabold text-[#111A44]">{item.label}</p>
                      <p className="mt-1 text-xs font-semibold text-[#8A94A6]">{item.helper}</p>
                    </button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white px-5 py-5 shadow-sm">
              <div className="mb-5 flex items-center justify-between gap-4">
                <h2 className="text-lg font-extrabold text-black">오늘 바로 볼 채용</h2>
                <button type="button" onClick={goJobs} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  전체 보기 <ArrowRight size={16} />
                </button>
              </div>

              <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                {todayStats.map(item => {
                  const Icon = item.icon;
                  return (
                    <button type="button" key={item.title} onClick={goJobs} className="rounded-lg border border-[#E1E6EE] bg-[#F8FAFC] px-4 py-3.5 text-left transition hover:border-[#0D6BEA] hover:bg-white">
                      <Icon size={22} strokeWidth={2.3} className="mb-3 text-[#596273]" />
                      <p className="text-xs font-bold text-[#8A94A6]">{item.title}</p>
                      <p className="mt-2 text-2xl font-extrabold leading-none text-[#111827]">{item.count}</p>
                      <p className="mt-3 text-xs font-semibold text-[#14843C]">{item.helper}</p>
                    </button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white px-5 py-5 shadow-sm">
              <div className="mb-5 flex items-center justify-between gap-4">
                <h2 className="text-lg font-extrabold text-black">장애친화 우수 기업</h2>
                <button type="button" onClick={goJobs} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  기업 더 보기 <ArrowRight size={16} />
                </button>
              </div>

              <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                {companies.map(company => (
                  <button type="button" key={company.name} onClick={goJobs} className="rounded-lg border border-[#E1E6EE] bg-white px-4 py-3.5 text-left transition hover:border-[#0D6BEA]">
                    <div className="flex items-center gap-3">
                      <span className="flex h-10 w-10 items-center justify-center rounded-lg text-lg font-extrabold text-white" style={{ backgroundColor: company.color }}>
                        {company.name.slice(0, 1)}
                      </span>
                      <div>
                        <p className="text-sm font-extrabold text-[#111827]">{company.name}</p>
                        <p className="text-xs font-semibold text-[#8A94A6]">{company.count}</p>
                      </div>
                    </div>
                    <div className="mt-3 flex flex-wrap gap-2">
                      {company.tags.map(tag => (
                        <span key={tag} className="rounded-full bg-[#EAF8F8] px-3 py-1 text-xs font-bold text-[#3A8F98]">
                          {tag}
                        </span>
                      ))}
                    </div>
                  </button>
                ))}
              </div>
            </section>

            <section className="grid overflow-hidden rounded-xl border border-[#DDE3EA] bg-white shadow-sm sm:grid-cols-3">
              {bottomStats.map(item => {
                const Icon = item.icon;
                return (
                  <div key={item.label} className="flex items-center justify-center gap-3 border-b border-[#E7ECF2] px-5 py-4 sm:border-b-0 sm:border-r last:border-r-0">
                    <Icon size={28} strokeWidth={2.1} className="text-[#596273]" />
                    <div>
                      <p className="text-2xl font-extrabold leading-none text-[#111827]">{item.value}</p>
                      <p className="mt-1 text-xs font-semibold text-[#718096]">{item.label}</p>
                    </div>
                  </div>
                );
              })}
            </section>
          </div>

          <aside className="space-y-5">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
              <div className="mb-4 flex items-center justify-between gap-4">
                <h2 className="text-lg font-extrabold text-black">구직자 프로필</h2>
                <button type="button" onClick={() => navigate('user-dashboard')} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  관리 <ArrowRight size={16} />
                </button>
              </div>

              <div className="space-y-4">
                {profiles.map(profile => (
                  <article key={profile.name} className="border-b border-[#E7ECF2] pb-4 last:border-b-0 last:pb-0">
                    <div className="flex items-start gap-3">
                      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-[#E6F6FA] text-lg font-extrabold text-[#176B87]">
                        {profile.initial}
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <p className="text-sm font-extrabold text-black">
                              {profile.name} <span className="font-semibold text-[#718096]">({profile.age})</span>
                            </p>
                            <p className="mt-1 text-sm font-extrabold text-black">{profile.role}</p>
                            <p className="mt-1 text-xs font-semibold text-[#718096]">
                              {profile.location} · {profile.career}
                            </p>
                          </div>
                          <div className="text-right">
                            <p className="text-[11px] font-bold text-[#8A94A6]">AI 적합도</p>
                            <p className="mt-1 text-sm font-extrabold text-[#14843C]">{profile.score}%</p>
                          </div>
                        </div>
                        <div className="mt-3 flex flex-wrap gap-2">
                          {profile.tags.map(tag => (
                            <span key={tag} className="rounded-full bg-[#EAF8F8] px-2.5 py-1 text-[11px] font-bold text-[#3A8F98]">
                              {tag}
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-4 shadow-sm">
              <div className="mb-4 flex items-center gap-2">
                <Building2 size={20} className="text-[#596273]" />
                <h2 className="text-lg font-extrabold text-black">지역별 채용</h2>
              </div>
              <div className="grid grid-cols-3 gap-2.5">
                {regions.map(region => (
                  <button type="button" key={region} onClick={goJobs} className="rounded-full bg-[#F1F3F6] px-3 py-2 text-xs font-extrabold text-black transition hover:bg-[#0D6BEA] hover:text-white">
                    {region}
                  </button>
                ))}
              </div>
            </section>
          </aside>
        </div>
      </main>
    </div>
  );
}
