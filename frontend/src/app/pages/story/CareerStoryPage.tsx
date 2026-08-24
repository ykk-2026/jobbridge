import { ArrowRight, BookOpenText, BriefcaseBusiness, CheckCircle2, Clock3, Search, Star } from 'lucide-react';
import { useMemo, useState } from 'react';
import type { Page } from '@/app/types';

interface CareerStoryPageProps {
  navigate: (page: Page, jobId?: string) => void;
}

const stories = [
  {
    id: 'story-1',
    category: '취업후기',
    title: '재택근무 백엔드 개발자로 전환한 지원 사례',
    summary: '접근성 조건과 기술 스택을 함께 비교해 Java 백엔드 공고에 지원한 과정입니다.',
    tags: ['개발', '재택근무', '면접 준비'],
    readTime: '5분',
    jobId: 'job-1',
  },
  {
    id: 'story-2',
    category: '알바가이드',
    title: '초보 알바 지원 전 확인해야 할 근무 조건',
    summary: '시급, 근무시간, 휴게시간, 근로계약서 작성 여부를 빠르게 확인하는 방법입니다.',
    tags: ['알바', '초보 가능', '근로계약'],
    readTime: '4분',
    jobId: 'job-11',
  },
  {
    id: 'story-3',
    category: '면접팁',
    title: '화상 면접에서 접근성 요청을 자연스럽게 전달하기',
    summary: '면접 전 필요한 지원 사항을 담당자에게 명확하게 전달하는 문장 예시를 정리했습니다.',
    tags: ['화상 면접', '접근성', '커뮤니케이션'],
    readTime: '6분',
    jobId: 'job-12',
  },
  {
    id: 'story-4',
    category: '직무소개',
    title: '데이터 분석가가 하는 일과 준비 스킬',
    summary: 'Python, SQL, 시각화 도구 중심으로 실제 공고에서 요구하는 역량을 살펴봅니다.',
    tags: ['데이터', 'Python', 'SQL'],
    readTime: '7분',
    jobId: 'job-3',
  },
];

const categories = ['전체', '취업후기', '알바가이드', '면접팁', '직무소개'];

export function CareerStoryPage({ navigate }: CareerStoryPageProps) {
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('전체');

  const filteredStories = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    return stories.filter(story => {
      if (category !== '전체' && story.category !== category) return false;
      if (!normalizedQuery) return true;

      const target = [story.category, story.title, story.summary, ...story.tags].join(' ').toLowerCase();
      return target.includes(normalizedQuery);
    });
  }, [category, query]);

  return (
    <div className="min-h-screen bg-[#F6F7F9] text-[#111827]">
      <main className="mx-auto max-w-[1256px] px-5 py-6 sm:px-8">
        <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <span className="rounded-full bg-[#FFF7E8] px-4 py-1.5 text-xs font-extrabold text-[#B45309]">취업 준비 콘텐츠</span>
              <h1 className="mt-3 text-3xl font-extrabold text-black">취업스토리</h1>
              <p className="mt-2 text-sm font-semibold text-[#596273]">지원 전 확인할 실전 가이드와 합격 사례를 모았습니다.</p>
            </div>
            <button type="button" onClick={() => navigate('jobs')} className="inline-flex h-11 items-center justify-center gap-2 rounded-lg bg-[#0D6BEA] px-5 text-sm font-extrabold text-white hover:bg-[#0959C7]">
              채용공고 보기 <ArrowRight size={17} />
            </button>
          </div>

          <div className="mt-5 grid gap-3 lg:grid-cols-[1fr_auto]">
            <div className="relative">
              <Search size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-[#344054]" />
              <input
                value={query}
                onChange={event => setQuery(event.target.value)}
                placeholder="키워드, 직무, 면접, 알바 검색"
                className="h-12 w-full rounded-lg border border-[#D7DDE5] bg-white pl-11 pr-4 text-sm font-semibold outline-none focus:ring-4 focus:ring-[#0D6BEA]/15"
              />
            </div>
            <div className="flex flex-wrap gap-2">
              {categories.map(item => (
                <button
                  type="button"
                  key={item}
                  onClick={() => setCategory(item)}
                  className={`h-12 rounded-lg px-4 text-sm font-extrabold ${category === item ? 'bg-[#0D6BEA] text-white' : 'bg-[#F1F3F6] text-[#344054] hover:bg-[#E4EFFF]'}`}
                >
                  {item}
                </button>
              ))}
            </div>
          </div>
        </section>

        <section className="mt-5 grid gap-4 lg:grid-cols-2">
          {filteredStories.map(story => (
            <article key={story.id} className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm transition hover:border-[#0D6BEA]">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="inline-flex items-center gap-1 rounded-full bg-[#E4EFFF] px-3 py-1 text-xs font-extrabold text-[#0D6BEA]">
                    <BookOpenText size={14} /> {story.category}
                  </p>
                  <h2 className="mt-3 text-xl font-extrabold leading-snug text-black">{story.title}</h2>
                </div>
                <span className="inline-flex shrink-0 items-center gap-1 rounded-full bg-[#F1F3F6] px-3 py-1 text-xs font-bold text-[#596273]">
                  <Clock3 size={13} /> {story.readTime}
                </span>
              </div>
              <p className="mt-3 text-sm font-semibold leading-7 text-[#596273]">{story.summary}</p>
              <div className="mt-4 flex flex-wrap gap-2">
                {story.tags.map(tag => (
                  <span key={tag} className="rounded-full bg-[#F8FAFC] px-3 py-1 text-xs font-bold text-[#344054]">{tag}</span>
                ))}
              </div>
              <div className="mt-5 flex items-center justify-between border-t border-[#E7ECF2] pt-4">
                <span className="inline-flex items-center gap-1 text-sm font-bold text-[#14843C]">
                  <CheckCircle2 size={15} /> 관련 공고 연결됨
                </span>
                <button type="button" onClick={() => navigate('job-detail', story.jobId)} className="inline-flex items-center gap-1 text-sm font-extrabold text-[#0D6BEA]">
                  공고 보기 <ArrowRight size={15} />
                </button>
              </div>
            </article>
          ))}
        </section>

        <section className="mt-5 grid gap-4 rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm md:grid-cols-3">
          {[
            ['이력서 점검', '지원 전 기본 정보와 희망 조건을 확인하세요.'],
            ['면접 준비', '접근성 요청, 근무 가능 시간, 장비 지원을 미리 정리하세요.'],
            ['알바 체크', '시급, 휴게시간, 근로계약서, 지급일을 확인하세요.'],
          ].map(([title, desc]) => (
            <div key={title} className="rounded-lg bg-[#F8FAFC] px-4 py-4">
              <p className="flex items-center gap-2 text-sm font-extrabold text-black"><Star size={16} className="text-[#F59E0B]" /> {title}</p>
              <p className="mt-2 text-sm font-semibold leading-6 text-[#596273]">{desc}</p>
            </div>
          ))}
        </section>
      </main>
    </div>
  );
}
