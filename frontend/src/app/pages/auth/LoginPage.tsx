import { ArrowLeft, ClipboardList, Eye, EyeOff, ShieldCheck, Target, X } from 'lucide-react';
import { useState } from 'react';
import { BrandLogo } from '@/app/components/BrandLogo';
import type { Page, RegisterFormData } from '@/app/types';

interface LoginPageProps {
  navigate: (page: Page) => void;
  onLogin: (loginId: string, password: string) => Promise<void>;
  onLoginSuccess?: () => void;
  registeredUser: RegisterFormData | null;
  onBack: () => void;
  onResetPassword: (newPassword: string) => void;
}

type FindMode = 'id' | 'password' | null;

const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;

const formatPhoneNumber = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 11);
  if (digits.length <= 3) return digits;
  if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
};

const isPhoneLike = (value: string) => /^\d|^-?\d/.test(value.replace(/\s/g, ''));

export function LoginPage({ navigate, onLogin, onLoginSuccess, registeredUser, onBack, onResetPassword }: LoginPageProps) {
  const [showPw, setShowPw] = useState(false);
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [autoLogin, setAutoLogin] = useState(false);
  const [error, setError] = useState('');
  const [findMode, setFindMode] = useState<FindMode>(null);
  const [findName, setFindName] = useState('');
  const [findContact, setFindContact] = useState('');
  const [findLoginId, setFindLoginId] = useState('');
  const [findResult, setFindResult] = useState('');
  const [isPasswordVerified, setIsPasswordVerified] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('');
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  const finishLogin = () => {
    if (onLoginSuccess) {
      onLoginSuccess();
      return;
    }

    navigate('main');
  };

  const closeFindModal = () => {
    setFindMode(null);
    setFindName('');
    setFindContact('');
    setFindLoginId('');
    setFindResult('');
    setIsPasswordVerified(false);
    setNewPassword('');
    setNewPasswordConfirm('');
  };

  const openFindModal = (mode: FindMode) => {
    setFindMode(mode);
    setFindName('');
    setFindContact('');
    setFindLoginId('');
    setFindResult('');
    setIsPasswordVerified(false);
    setNewPassword('');
    setNewPasswordConfirm('');
  };

  const submitLogin = async () => {
    if (!loginId.trim() || !password) {
      setError('아이디와 비밀번호를 입력해 주세요.');
      return;
    }

    setIsLoggingIn(true);
    setError('');
    try {
      await onLogin(loginId, password);
      if (autoLogin) localStorage.setItem('savedLoginId', loginId.trim());
      else localStorage.removeItem('savedLoginId');
      finishLogin();
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : '로그인에 실패했습니다.');
    } finally {
      setIsLoggingIn(false);
    }
  };

  const findLoginIdByInfo = () => {
    const contact = findContact.trim();
    const normalizedContact = isPhoneLike(contact) ? formatPhoneNumber(contact) : contact;

    if (!findName.trim() || !contact) {
      setFindResult('이름과 이메일 또는 전화번호를 입력해 주세요.');
      return;
    }

    if (
      registeredUser &&
      findName.trim() === registeredUser.name.trim() &&
      (normalizedContact === registeredUser.email.trim() || normalizedContact === registeredUser.phone)
    ) {
      setFindResult(`가입된 아이디는 ${registeredUser.loginId} 입니다.`);
      setLoginId(registeredUser.loginId);
      return;
    }

    if (findName.trim() === '데모' && (normalizedContact === 'demo@example.com' || normalizedContact === '010-1234-5678')) {
      setFindResult('가입된 아이디는 demo 입니다.');
      setLoginId('demo');
      return;
    }

    setFindResult('일치하는 회원 정보를 찾을 수 없습니다.');
  };

  const findPasswordByInfo = () => {
    if (!findLoginId.trim() || !findContact.trim()) {
      setFindResult('아이디와 이메일을 모두 입력해 주세요.');
      return;
    }

    if (registeredUser && findLoginId.trim() === registeredUser.loginId.trim() && findContact.trim() === registeredUser.email.trim()) {
      setFindResult('회원 정보가 확인되었습니다. 새 비밀번호를 입력해 주세요.');
      setIsPasswordVerified(true);
      return;
    }

    if (findLoginId.trim() === 'demo' && findContact.trim() === 'demo@example.com') {
      setFindResult('데모 계정은 비밀번호 변경 대상이 아닙니다. 비밀번호 password로 로그인해 주세요.');
      setIsPasswordVerified(false);
      return;
    }

    setFindResult('일치하는 회원 정보를 찾을 수 없습니다.');
    setIsPasswordVerified(false);
  };

  const submitNewPassword = () => {
    if (!passwordPattern.test(newPassword)) {
      setFindResult('새 비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해 주세요.');
      return;
    }

    if (newPassword !== newPasswordConfirm) {
      setFindResult('새 비밀번호가 일치하지 않습니다.');
      return;
    }

    onResetPassword(newPassword);
    setPassword('');
    setFindResult('비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요.');
    setTimeout(closeFindModal, 700);
  };

  const submitFindModal = () => {
    if (findMode === 'id') {
      findLoginIdByInfo();
      return;
    }

    if (isPasswordVerified) submitNewPassword();
    else findPasswordByInfo();
  };

  return (
    <div className="min-h-screen bg-[#F3F7FF] flex items-stretch justify-center">
      <aside className="hidden lg:flex w-[360px] bg-gradient-to-b from-white to-[#EAF4FF] px-8 py-10 flex-col justify-between border-r border-[#DCEAF3]">
        <div>
          <button type="button" onClick={() => navigate('main')} className="flex items-center gap-2 font-bold text-foreground mb-12">
            <BrandLogo compact />
          </button>

          <h1 className="text-3xl font-bold leading-tight text-foreground">
            다시 이어가는
            <br />
            <span className="text-primary">나에게 맞는 기회</span>
          </h1>
          <p className="text-sm text-muted-foreground leading-6 mt-6">
            저장한 공고, 지원 현황, 맞춤 추천을 로그인 후 한눈에 확인할 수 있습니다.
          </p>

          <div className="space-y-7 mt-12">
            {[
              { title: '맞춤 추천 유지', desc: '프로필 조건에 맞는 공고를 계속 추천합니다.', icon: Target },
              { title: '지원 현황 확인', desc: '제출한 지원서와 진행 상태를 확인할 수 있습니다.', icon: ClipboardList },
              { title: '안전한 계정 관리', desc: '개인정보와 저장 공고를 안전하게 관리합니다.', icon: ShieldCheck },
            ].map(item => {
              const Icon = item.icon;
              return (
                <div key={item.title} className="flex gap-4">
                  <span className="w-11 h-11 rounded-full bg-[#DDEBFF] text-primary flex items-center justify-center shrink-0">
                    <Icon size={20} />
                  </span>
                  <div>
                    <p className="font-semibold text-foreground">{item.title}</p>
                    <p className="text-sm text-muted-foreground mt-1 leading-5">{item.desc}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="h-28 rounded-t-[80px] bg-[#CFE8FF] opacity-70" />
      </aside>

      <main className="w-full max-w-3xl bg-white px-5 py-8 sm:px-8 lg:px-12 lg:py-10 flex items-center">
        <div className="w-full max-w-md mx-auto">
          <button
            type="button"
            onClick={onBack}
            className="mb-5 inline-flex items-center gap-2 rounded-lg border border-border px-3 py-2 text-sm font-semibold text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <ArrowLeft size={16} />
            뒤로가기
          </button>

          <button type="button" onClick={() => navigate('main')} className="lg:hidden flex items-center gap-2 font-bold text-foreground mb-8">
            <BrandLogo compact />
          </button>

          <div className="mb-8">
            <h1 className="text-2xl font-bold text-foreground">로그인</h1>
            <p className="text-sm text-muted-foreground mt-2">아이디와 비밀번호를 입력해 주세요.</p>
          </div>

          <div className="mb-7">
            <h2 className="text-center text-xl font-bold text-foreground">개인회원 로그인</h2>
            <div className="mt-4 h-px bg-border" />
          </div>

          <div className="space-y-4">
            <label className="block">
              <span className="block text-sm font-medium mb-2">아이디</span>
              <input
                value={loginId}
                onChange={event => {
                  setLoginId(event.target.value);
                  setError('');
                }}
                onKeyDown={event => {
                  if (event.key === 'Enter') submitLogin();
                }}
                className="w-full px-3 py-3 rounded-lg border border-border"
                placeholder="아이디를 입력해 주세요"
              />
            </label>

            <label className="block">
              <span className="block text-sm font-medium mb-2">비밀번호</span>
              <div className="relative">
                <input
                  value={password}
                  onChange={event => {
                    setPassword(event.target.value);
                    setError('');
                  }}
                  onKeyDown={event => {
                    if (event.key === 'Enter') submitLogin();
                  }}
                  type={showPw ? 'text' : 'password'}
                  className="w-full px-3 py-3 pr-10 rounded-lg border border-border"
                  placeholder="비밀번호를 입력해 주세요"
                />
                <button
                  type="button"
                  aria-label={showPw ? '비밀번호 숨기기' : '비밀번호 보기'}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                  onClick={() => setShowPw(!showPw)}
                >
                  {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </label>
          </div>

          {error && <p className="mt-3 text-sm text-red-500">{error}</p>}

          <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm">
            <label className="flex items-center gap-2 text-muted-foreground cursor-pointer">
              <input
                type="checkbox"
                checked={autoLogin}
                onChange={event => setAutoLogin(event.target.checked)}
              />
              자동 로그인
            </label>
            <div className="flex items-center gap-2 text-muted-foreground">
              <button type="button" className="hover:text-foreground" onClick={() => openFindModal('id')}>
                아이디 찾기
              </button>
              <span className="text-border">|</span>
              <button type="button" className="hover:text-foreground" onClick={() => openFindModal('password')}>
                비밀번호 찾기
              </button>
            </div>
          </div>

          <button type="button" onClick={submitLogin} disabled={isLoggingIn} className="mt-6 w-full py-3.5 rounded-lg bg-primary text-white font-medium shadow-sm hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60">
            {isLoggingIn ? '로그인 중...' : '로그인'}
          </button>
          <button type="button" onClick={() => navigate('register')} className="w-full mt-4 text-sm text-muted-foreground">
            아직 계정이 없으신가요? <span className="font-semibold text-primary">회원가입</span>
          </button>
        </div>
      </main>

      {findMode && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
            <div className="flex items-start justify-between gap-4">
              <div>
                <h2 className="text-xl font-bold text-foreground">{findMode === 'id' ? '아이디 찾기' : '비밀번호 찾기'}</h2>
                <p className="mt-1 text-sm text-muted-foreground">
                  {findMode === 'id' ? '가입한 이름과 이메일 또는 전화번호를 입력해 주세요.' : '가입한 아이디와 이메일을 입력해 주세요.'}
                </p>
              </div>
              <button type="button" onClick={closeFindModal} className="rounded-lg border border-border p-2 text-muted-foreground hover:bg-muted">
                <X size={16} />
              </button>
            </div>

            <div className="mt-5 space-y-4">
              {findMode === 'id' ? (
                <label className="block">
                  <span className="block text-sm font-medium mb-2">이름</span>
                  <input
                    value={findName}
                    onChange={event => setFindName(event.target.value)}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitFindModal();
                    }}
                    className="w-full px-3 py-3 rounded-lg border border-border"
                    placeholder="이름을 입력해 주세요"
                  />
                </label>
              ) : (
                <label className="block">
                  <span className="block text-sm font-medium mb-2">아이디</span>
                  <input
                    value={findLoginId}
                    onChange={event => setFindLoginId(event.target.value)}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitFindModal();
                    }}
                    className="w-full px-3 py-3 rounded-lg border border-border"
                    placeholder="아이디를 입력해 주세요"
                  />
                </label>
              )}

              <label className="block">
                <span className="block text-sm font-medium mb-2">{findMode === 'id' ? '이메일 또는 전화번호' : '이메일'}</span>
                <input
                  value={findContact}
                  onChange={event => {
                    const value = event.target.value;
                    setFindContact(findMode === 'id' && isPhoneLike(value) ? formatPhoneNumber(value) : value);
                  }}
                  onKeyDown={event => {
                    if (event.key === 'Enter') submitFindModal();
                  }}
                  className="w-full px-3 py-3 rounded-lg border border-border"
                  placeholder={findMode === 'id' ? '예: name@example.com 또는 010-1234-5678' : '예: name@example.com'}
                />
              </label>
            </div>

            {findResult && (
              <div className="mt-4 rounded-lg bg-[#F7FAFC] px-4 py-3 text-sm font-semibold text-primary">
                {findResult}
              </div>
            )}

            {findMode === 'password' && isPasswordVerified && (
              <div className="mt-4 space-y-4">
                <label className="block">
                  <span className="block text-sm font-medium mb-2">새 비밀번호</span>
                  <input
                    type="password"
                    value={newPassword}
                    onChange={event => setNewPassword(event.target.value)}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitFindModal();
                    }}
                    className="w-full px-3 py-3 rounded-lg border border-border"
                    placeholder="영문, 숫자, 특수문자 포함 8~20자"
                  />
                </label>
                <label className="block">
                  <span className="block text-sm font-medium mb-2">새 비밀번호 확인</span>
                  <input
                    type="password"
                    value={newPasswordConfirm}
                    onChange={event => setNewPasswordConfirm(event.target.value)}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitFindModal();
                    }}
                    className="w-full px-3 py-3 rounded-lg border border-border"
                    placeholder="새 비밀번호를 다시 입력해 주세요"
                  />
                </label>
              </div>
            )}

            <button
              type="button"
              className="mt-5 w-full rounded-lg bg-primary px-4 py-3 text-sm font-semibold text-white hover:bg-primary/90"
              onClick={submitFindModal}
            >
              {findMode === 'password' && isPasswordVerified ? '새 비밀번호 저장' : '확인'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
