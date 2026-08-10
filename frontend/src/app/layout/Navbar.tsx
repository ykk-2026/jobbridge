import { BriefcaseBusiness, LogOut, Menu, UserRound, X } from 'lucide-react';
import { useState } from 'react';
import type { CurrentUser, Page } from '@/app/types';

interface NavbarProps {
  currentPage: Page;
  navigate: (page: Page) => void;
  currentUser: CurrentUser | null;
  onLogout: () => void;
}

const navItems: Array<{ label: string; page: Page }> = [
  { label: '홈', page: 'main' },
  { label: '채용정보', page: 'jobs' },
  { label: '맞춤 추천', page: 'ai-recommend' },
  { label: '저장한 공고', page: 'saved' },
];

export function Navbar({ currentPage, navigate, currentUser, onLogout }: NavbarProps) {
  const [open, setOpen] = useState(false);
  const userPage: Page = currentUser?.role === 'corporate' ? 'corporate' : currentUser?.role === 'admin' ? 'admin' : 'user-dashboard';

  const goTo = (page: Page) => {
    navigate(page);
    setOpen(false);
  };

  const logout = () => {
    onLogout();
    setOpen(false);
  };

  return (
    <header className="sticky top-0 z-40 border-b border-[#E5EAF0] bg-white">
      <div className="mx-auto flex h-16 max-w-[1256px] items-center justify-between px-5 sm:px-8">
        <button type="button" onClick={() => goTo('main')} className="flex items-center gap-3">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-[#0D6BEA] text-white shadow-sm">
            <BriefcaseBusiness size={21} strokeWidth={2.2} />
          </span>
          <span className="text-xl font-bold tracking-normal text-[#111827]">JobBridgeAI</span>
        </button>

        <nav className="hidden items-center gap-10 md:flex">
          {navItems.map(item => (
            <button
              type="button"
              key={item.page}
              onClick={() => goTo(item.page)}
              className={`text-base font-bold transition ${
                currentPage === item.page ? 'text-[#0D6BEA]' : 'text-[#1F2A44] hover:text-[#0D6BEA]'
              }`}
            >
              {item.label}
            </button>
          ))}
        </nav>

        <div className="hidden items-center gap-5 md:flex">
          {currentUser ? (
            <>
              <button type="button" onClick={() => goTo(userPage)} className="text-base font-bold text-[#111827] hover:text-[#0D6BEA]">
                프로필
              </button>
              <button type="button" onClick={logout} className="inline-flex items-center gap-2 rounded-lg border border-[#DCE3EB] px-3.5 py-2 text-sm font-bold text-[#475467] hover:bg-[#F7F9FC]">
                <LogOut size={16} />
                로그아웃
              </button>
            </>
          ) : (
            <>
              <button type="button" onClick={() => goTo('login')} className="text-base font-bold text-[#111827] hover:text-[#0D6BEA]">
                로그인
              </button>
              <button type="button" onClick={() => goTo('register')} className="rounded-lg bg-[#0D6BEA] px-5 py-2.5 text-base font-bold text-white shadow-sm hover:bg-[#0959C7]">
                회원가입
              </button>
            </>
          )}
        </div>

        <button
          type="button"
          aria-label={open ? '메뉴 닫기' : '메뉴 열기'}
          className="flex h-9 w-9 items-center justify-center rounded-lg border border-[#DCE3EB] md:hidden"
          onClick={() => setOpen(prev => !prev)}
        >
          {open ? <X size={19} /> : <Menu size={19} />}
        </button>
      </div>

      {open && (
        <div className="border-t border-[#E5EAF0] bg-white px-5 py-4 md:hidden">
          <div className="space-y-1">
            {navItems.map(item => (
              <button
                type="button"
                key={item.page}
                onClick={() => goTo(item.page)}
                className={`block w-full rounded-lg px-3 py-3 text-left text-base font-bold ${
                  currentPage === item.page ? 'bg-[#EEF5FF] text-[#0D6BEA]' : 'text-[#1F2A44] hover:bg-[#F7F9FC]'
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
                  <button type="button" onClick={() => goTo('register')} className="block w-full rounded-lg px-3 py-3 text-left text-base font-bold text-[#0D6BEA] hover:bg-[#EEF5FF]">
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
