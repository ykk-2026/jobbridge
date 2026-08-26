import { Bell, LogOut, Menu, Search, UserRound, X } from 'lucide-react';
import { useState } from 'react';
import { BrandLogo } from '@/app/components/BrandLogo';
import type { CurrentUser, Page } from '@/app/types';

interface NavbarProps {
  currentPage: Page;
  navigate: (page: Page) => void;
  currentUser: CurrentUser | null;
  onLogout: () => void;
  onSearch: (query: string) => void;
}

interface NavItem {
  label: string;
  page: Page;
  badge?: string;
}

const navItems: NavItem[] = [
  { label: '채용정보', page: 'jobs' },
  { label: 'AI 추천일자리', page: 'ai-recommend', badge: 'NEW' },
  { label: '기업 정보', page: 'jobs' },
  { label: '커뮤니티', page: 'support' },
  { label: '이용안내', page: 'support' },
];

export function Navbar({ currentPage, navigate, currentUser, onLogout, onSearch }: NavbarProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const userPage: Page = currentUser?.role === 'corporate' ? 'corporate' : 'user-dashboard';

  const goTo = (page: Page) => {
    navigate(page);
    setMobileOpen(false);
  };

  const submitSearch = () => {
    onSearch(searchQuery.trim());
    setMobileOpen(false);
  };

  const logout = () => {
    onLogout();
    setMobileOpen(false);
  };

  return (
    <header className="sticky top-0 z-40 border-b border-[#E5EAF0] bg-white">
      <div className="mx-auto flex h-[66px] max-w-[1240px] items-center justify-between gap-5 px-6">
        <div className="flex min-w-0 flex-1 items-center gap-8">
          <button type="button" onClick={() => goTo('main')} className="shrink-0" aria-label="일이음 홈으로 이동">
            <BrandLogo compact />
          </button>

          <div className="relative hidden w-full max-w-[500px] md:block">
            <input
              value={searchQuery}
              onChange={event => setSearchQuery(event.target.value)}
              onKeyDown={event => {
                if (event.key === 'Enter') submitSearch();
              }}
              placeholder="직무, 회사, 지역, 키워드 검색"
              className="h-9 w-full rounded-[4px] border border-[#D3DAE5] bg-white px-4 pr-12 text-[13px] font-semibold text-[#111827] outline-none placeholder:text-[#8B95A6] focus:border-[#2563EB]"
            />
            <button
              type="button"
              onClick={submitSearch}
              aria-label="검색"
              className="absolute right-0 top-0 flex h-9 w-12 items-center justify-center rounded-r-[4px] bg-[#1F64E8] text-white hover:bg-[#1754C8]"
            >
              <Search size={17} strokeWidth={2.6} />
            </button>
          </div>
        </div>

        <div className="hidden items-center gap-5 md:flex">
          <button type="button" aria-label="알림" className="flex h-9 w-9 items-center justify-center rounded-md text-[#111827] hover:bg-[#F5F7FA]">
            <Bell size={18} />
          </button>
          {currentUser ? (
            <>
              <button type="button" onClick={() => goTo(userPage)} className="text-[13px] font-bold text-[#111827] hover:text-[#1F64E8]">
                프로필
              </button>
              <button type="button" onClick={logout} className="inline-flex h-9 items-center gap-2 rounded-md border border-[#D7DDE5] px-4 text-[13px] font-bold text-[#475467] hover:bg-[#F7F9FC]">
                <LogOut size={15} />
                로그아웃
              </button>
            </>
          ) : (
            <>
              <button type="button" onClick={() => goTo('login')} className="h-9 rounded-md border border-[#D7DDE5] px-5 text-[13px] font-bold text-[#1F2937] hover:bg-[#F7F9FC]">
                로그인
              </button>
              <button type="button" onClick={() => goTo('register')} className="h-9 rounded-md bg-[#1F64E8] px-5 text-[13px] font-bold text-white shadow-sm hover:bg-[#1754C8]">
                회원가입
              </button>
            </>
          )}
        </div>

        <button
          type="button"
          aria-label={mobileOpen ? '메뉴 닫기' : '메뉴 열기'}
          className="flex h-9 w-9 items-center justify-center rounded-md border border-[#D7DDE5] md:hidden"
          onClick={() => setMobileOpen(prev => !prev)}
        >
          {mobileOpen ? <X size={19} /> : <Menu size={19} />}
        </button>
      </div>

      <nav className="hidden border-t border-[#EFF2F6] bg-white md:block">
        <div className="mx-auto flex h-10 max-w-[1240px] items-center gap-[74px] px-6">
          {navItems.map(item => (
            <button
              type="button"
              key={item.label}
              onClick={() => goTo(item.page)}
              className={`relative flex h-full items-center gap-1 text-[13px] font-bold ${
                currentPage === item.page ? 'text-[#1F64E8]' : 'text-[#111827] hover:text-[#1F64E8]'
              }`}
            >
              {item.label}
              {item.badge && <span className="rounded-full bg-[#EAF1FF] px-1.5 py-0.5 text-[8px] font-black text-[#1F64E8]">{item.badge}</span>}
            </button>
          ))}
        </div>
      </nav>

      {mobileOpen && (
        <div className="border-t border-[#E5EAF0] bg-white px-5 py-4 md:hidden">
          <div className="relative mb-3">
            <input
              value={searchQuery}
              onChange={event => setSearchQuery(event.target.value)}
              onKeyDown={event => {
                if (event.key === 'Enter') submitSearch();
              }}
              placeholder="직무, 회사, 지역, 키워드 검색"
              className="h-10 w-full rounded-md border border-[#D7DDE5] px-3 pr-12 text-sm font-semibold outline-none"
            />
            <button type="button" onClick={submitSearch} aria-label="검색" className="absolute right-0 top-0 flex h-10 w-11 items-center justify-center rounded-r-md bg-[#1F64E8] text-white">
              <Search size={17} />
            </button>
          </div>
          <div className="space-y-1">
            {navItems.map(item => (
              <button
                type="button"
                key={item.label}
                onClick={() => goTo(item.page)}
                className={`block w-full rounded-lg px-3 py-3 text-left text-base font-bold ${
                  currentPage === item.page ? 'bg-[#EEF5FF] text-[#1F64E8]' : 'text-[#1F2A44] hover:bg-[#F7F9FC]'
                }`}
              >
                {item.label}
              </button>
            ))}
            <div className="mt-3 border-t border-[#E5EAF0] pt-3">
              {currentUser ? (
                <>
                  <button type="button" onClick={() => goTo(userPage)} className="flex w-full items-center gap-2 rounded-lg px-3 py-3 text-base font-bold hover:bg-[#F7F9FC]">
                    <UserRound size={18} />
                    프로필
                  </button>
                  <button type="button" onClick={logout} className="flex w-full items-center gap-2 rounded-lg px-3 py-3 text-base font-bold text-red-600 hover:bg-red-50">
                    <LogOut size={18} />
                    로그아웃
                  </button>
                </>
              ) : (
                <>
                  <button type="button" onClick={() => goTo('login')} className="block w-full rounded-lg px-3 py-3 text-left text-base font-bold hover:bg-[#F7F9FC]">
                    로그인
                  </button>
                  <button type="button" onClick={() => goTo('register')} className="block w-full rounded-lg px-3 py-3 text-left text-base font-bold text-[#1F64E8] hover:bg-[#EEF5FF]">
                    회원가입
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
