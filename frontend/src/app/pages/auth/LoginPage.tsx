import { ArrowLeft, Eye, EyeOff, X } from 'lucide-react';
import { useState } from 'react';
import { BrandLogo } from '@/app/components/BrandLogo';
import type { Page, RegisterFormData } from '@/app/types';

interface LoginPageProps {
  navigate: (page: Page) => void;
  onLogin: (loginId: string, password: string, keepLoggedIn?: boolean) => Promise<void>;
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
  const [isSubmitting, setIsSubmitting] = useState(false);

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
    if (isSubmitting) return;

    if (!loginId.trim() || !password) {
      setError('아이디와 비밀번호를 입력해 주세요.');
      return;
    }

    setIsSubmitting(true);
    try {
      await onLogin(loginId, password, autoLogin);
      window.setTimeout(finishLogin, 180);
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : '로그인에 실패했습니다.');
      setIsSubmitting(false);
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
    <div className="flex min-h-screen items-stretch justify-center bg-[#F3F7FF]">
      <main className="w-full max-w-4xl bg-white px-5 py-8 sm:px-8 lg:px-10 lg:py-10">
        <div className="mx-auto max-w-3xl">
          <button
            type="button"
            onClick={onBack}
            className="mb-5 inline-flex items-center gap-2 rounded-lg border border-border px-3 py-2 text-sm font-semibold text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <ArrowLeft size={16} />
            뒤로가기
          </button>

          <button type="button" onClick={() => navigate('main')} className="mb-8 flex items-center gap-2 font-bold text-foreground">
            <BrandLogo compact />
          </button>

          <div className="mb-8">
            <h1 className="text-2xl font-bold text-foreground">로그인</h1>
            <p className="mt-2 text-sm text-muted-foreground">아이디와 비밀번호를 입력해 주세요.</p>
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
                className="w-full rounded-lg border border-border px-3 py-3"
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
                  className="w-full rounded-lg border border-border px-3 py-3 pr-10"
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

          <button type="button" onClick={submitLogin} disabled={isSubmitting} className="mt-6 w-full rounded-lg bg-primary py-3.5 font-medium text-white shadow-sm hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-80">
            {isSubmitting ? '로그인 중...' : '로그인'}
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
