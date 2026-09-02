import { ArrowLeft, Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import { BrandLogo } from '@/app/components/BrandLogo';
import type { Page, RegisterFormData } from '@/app/types';
import { checkLoginIdAvailability } from '@/app/api/memberApi';

interface RegisterPageProps {
  navigate: (page: Page) => void;
  onRegister: (formData: RegisterFormData, passwordConfirm: string) => Promise<void>;
  onBack: () => void;
}

const initialForm: RegisterFormData = {
  loginId: '',
  password: '',
  birthDate: '',
  name: '',
  email: '',
  phone: '',
  gender: '',
  preferredRole: '',
};

const reservedIds = ['admin', 'demo', 'test', 'user', 'jobbridge', 'minjun_kim'];
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;
const phonePattern = /^010-\d{4}-\d{4}$/;

const isValidBirthDate = (value: string) => {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;

  const [year, month, day] = value.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  const today = new Date();

  return date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day && date <= today && year >= 1900;
};

const formatPhoneNumber = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 11);
  if (digits.length <= 3) return digits;
  if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
};

const formatBirthDate = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 8);
  if (digits.length <= 4) return digits;
  if (digits.length <= 6) return `${digits.slice(0, 4)}-${digits.slice(4)}`;
  return `${digits.slice(0, 4)}-${digits.slice(4, 6)}-${digits.slice(6)}`;
};

export function RegisterPage({ navigate, onRegister, onBack }: RegisterPageProps) {
  const [form, setForm] = useState<RegisterFormData>(initialForm);
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [showPwConfirm, setShowPwConfirm] = useState(false);
  const [idCheckMessage, setIdCheckMessage] = useState('');
  const [idAvailable, setIdAvailable] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const updateField = (field: keyof RegisterFormData, value: string) => {
    setForm(prev => ({ ...prev, [field]: value }));
    setErrors(prev => ({ ...prev, [field]: '' }));

    if (field === 'loginId') {
      setIdCheckMessage('');
      setIdAvailable(false);
    }
  };

  const checkLoginId = async () => {
    const loginId = form.loginId.trim().toLowerCase();

    if (!loginId) {
      setIdAvailable(false);
      setIdCheckMessage('아이디를 입력해 주세요.');
      return;
    }

    if (!/^[a-z0-9]{4,20}$/.test(loginId)) {
      setIdAvailable(false);
      setIdCheckMessage('아이디는 영문 소문자와 숫자 조합 4~20자로 입력해 주세요.');
      return;
    }

    try {
      const result = await checkLoginIdAvailability(loginId);
      setIdAvailable(result.available);
      setIdCheckMessage(result.message || (result.available ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.'));
      if (result.available) setErrors(prev => ({ ...prev, loginId: '' }));
    } catch (error) {
      setIdAvailable(false);
      setIdCheckMessage(error instanceof Error ? error.message : '아이디 중복 확인에 실패했습니다.');
    }
  };

  const validateForm = () => {
    const nextErrors: Record<string, string> = {};
    const loginId = form.loginId.trim().toLowerCase();
    const email = form.email.trim();

    if (!loginId) nextErrors.loginId = '아이디를 입력해 주세요.';
    else if (!/^[a-z0-9]{4,20}$/.test(loginId)) nextErrors.loginId = '아이디는 영문 소문자와 숫자 조합 4~20자로 입력해 주세요.';
    else if (!idAvailable) nextErrors.loginId = '아이디 중복확인을 해 주세요.';

    if (!passwordPattern.test(form.password)) nextErrors.password = '비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해 주세요.';
    if (form.password !== passwordConfirm) nextErrors.passwordConfirm = '비밀번호가 일치하지 않습니다.';
    if (!form.name.trim()) nextErrors.name = '이름을 입력해 주세요.';
    if (!email) nextErrors.email = '이메일을 입력해 주세요.';
    else if (!emailPattern.test(email)) nextErrors.email = '올바른 이메일 형식으로 입력해 주세요. 예: name@example.com';
    if (!isValidBirthDate(form.birthDate.trim())) nextErrors.birthDate = '생년월일은 YYYY-MM-DD 형식의 실제 날짜로 입력해 주세요. 예: 1999-01-01';
    if (!form.gender) nextErrors.gender = '성별을 선택해 주세요.';
    if (!phonePattern.test(form.phone)) nextErrors.phone = '전화번호는 010-1234-5678 형식으로 입력해 주세요.';

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const submitRegister = async () => {
    if (!validateForm()) return;

    try {
      await onRegister({
        ...form,
        loginId: form.loginId.trim().toLowerCase(),
        name: form.name.trim(),
        email: form.email.trim(),
        phone: form.phone,
        birthDate: form.birthDate.trim(),
      }, passwordConfirm);
      window.alert('회원가입이 완료되었습니다. 로그인해 주세요.');
      navigate('login');
    } catch (error) {
      setErrors(prev => ({ ...prev, submit: error instanceof Error ? error.message : '회원가입에 실패했습니다.' }));
    }
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
            <h1 className="text-2xl font-bold text-foreground">회원가입</h1>
            <p className="mt-2 text-sm text-muted-foreground">회원 정보를 정확히 입력해 주세요.</p>
          </div>

          <div className="mb-7">
            <h2 className="text-center text-xl font-bold text-foreground">개인 회원가입</h2>
            <div className="mt-4 h-px bg-border" />
          </div>

          <div className="space-y-4">
            <label className="block">
              <span className="mb-2 block text-sm font-medium">아이디 <span className="text-red-500">*</span></span>
              <div className="flex gap-2">
                <input
                  value={form.loginId}
                  onChange={event => updateField('loginId', event.target.value)}
                  onKeyDown={event => {
                    if (event.key === 'Enter') checkLoginId();
                  }}
                  className={`flex-1 rounded-lg border px-3 py-3 ${idCheckMessage ? (idAvailable ? 'border-green-500' : 'border-red-400') : 'border-border'}`}
                  placeholder="영문 소문자, 숫자 조합 4~20자"
                />
                <button type="button" onClick={checkLoginId} className="rounded-lg border border-border px-4 text-sm font-medium text-muted-foreground hover:bg-muted">
                  중복확인
                </button>
              </div>
              {idCheckMessage && <p className={`mt-2 text-sm ${idAvailable ? 'text-green-600' : 'text-red-500'}`}>{idCheckMessage}</p>}
              {errors.loginId && <p className="mt-2 text-sm text-red-500">{errors.loginId}</p>}
            </label>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-medium">비밀번호 <span className="text-red-500">*</span></span>
                <div className="relative">
                  <input
                    value={form.password}
                    onChange={event => updateField('password', event.target.value)}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitRegister();
                    }}
                    type={showPw ? 'text' : 'password'}
                    className="w-full rounded-lg border border-border px-3 py-3 pr-10"
                    placeholder="영문, 숫자, 특수문자 포함 8~20자"
                  />
                  <button type="button" aria-label="비밀번호 보기" onClick={() => setShowPw(prev => !prev)} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                    {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.password && <p className="mt-2 text-sm text-red-500">{errors.password}</p>}
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-medium">비밀번호 확인 <span className="text-red-500">*</span></span>
                <div className="relative">
                  <input
                    value={passwordConfirm}
                    onChange={event => {
                      setPasswordConfirm(event.target.value);
                      setErrors(prev => ({ ...prev, passwordConfirm: '' }));
                    }}
                    onKeyDown={event => {
                      if (event.key === 'Enter') submitRegister();
                    }}
                    type={showPwConfirm ? 'text' : 'password'}
                    className="w-full rounded-lg border border-border px-3 py-3 pr-10"
                    placeholder="비밀번호를 다시 입력해 주세요"
                  />
                  <button type="button" aria-label="비밀번호 확인 보기" onClick={() => setShowPwConfirm(prev => !prev)} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                    {showPwConfirm ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.passwordConfirm && <p className="mt-2 text-sm text-red-500">{errors.passwordConfirm}</p>}
              </label>
            </div>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-medium">이름 <span className="text-red-500">*</span></span>
                <input
                  value={form.name}
                  onChange={event => updateField('name', event.target.value)}
                  onKeyDown={event => {
                    if (event.key === 'Enter') submitRegister();
                  }}
                  className="w-full rounded-lg border border-border px-3 py-3"
                  placeholder="이름을 입력해 주세요"
                />
                {errors.name && <p className="mt-2 text-sm text-red-500">{errors.name}</p>}
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-medium">이메일 <span className="text-red-500">*</span></span>
                <input
                  value={form.email}
                  onChange={event => updateField('email', event.target.value)}
                  onKeyDown={event => {
                    if (event.key === 'Enter') submitRegister();
                  }}
                  type="email"
                  className="w-full rounded-lg border border-border px-3 py-3"
                  placeholder="예: name@example.com"
                />
                {errors.email && <p className="mt-2 text-sm text-red-500">{errors.email}</p>}
              </label>
            </div>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <label className="block">
                <span className="mb-2 block text-sm font-medium">생년월일 <span className="text-red-500">*</span></span>
                <input
                  value={form.birthDate}
                  onChange={event => updateField('birthDate', formatBirthDate(event.target.value))}
                  onKeyDown={event => {
                    if (event.key === 'Enter') submitRegister();
                  }}
                  type="text"
                  inputMode="numeric"
                  className="w-full rounded-lg border border-border px-3 py-3"
                  placeholder="예: 1999-01-01"
                />
                {errors.birthDate && <p className="mt-2 text-sm text-red-500">{errors.birthDate}</p>}
              </label>

              <label className="block">
                <span className="mb-2 block text-sm font-medium">성별 <span className="text-red-500">*</span></span>
                <select
                  value={form.gender}
                  onChange={event => updateField('gender', event.target.value)}
                  className="w-full rounded-lg border border-border bg-white px-3 py-3"
                >
                  <option value="">성별을 선택해 주세요</option>
                      <option value="MALE">남성</option>
                      <option value="FEMALE">여성</option>
                      <option value="OTHER">기타/선택 안함</option>
                </select>
                {errors.gender && <p className="mt-2 text-sm text-red-500">{errors.gender}</p>}
              </label>
            </div>

            <label className="block">
              <span className="mb-2 block text-sm font-medium">전화번호 <span className="text-red-500">*</span></span>
              <input
                value={form.phone}
                onChange={event => updateField('phone', formatPhoneNumber(event.target.value))}
                onKeyDown={event => {
                  if (event.key === 'Enter') submitRegister();
                }}
                inputMode="numeric"
                className="w-full rounded-lg border border-border px-3 py-3"
                placeholder="예: 010-1234-5678"
              />
              {errors.phone && <p className="mt-2 text-sm text-red-500">{errors.phone}</p>}
            </label>

            <label className="block">
              <span className="mb-2 block text-sm font-medium">희망 직무 <span className="text-xs text-muted-foreground">(프로필)</span></span>
              <input
                value={form.preferredRole}
                onChange={event => updateField('preferredRole', event.target.value)}
                onKeyDown={event => {
                  if (event.key === 'Enter') submitRegister();
                }}
                className="w-full rounded-lg border border-border px-3 py-3"
                placeholder="희망하는 직무를 입력해 주세요"
              />
            </label>
          </div>

          {errors.submit && <p className="mt-4 rounded-lg bg-red-50 px-4 py-3 text-sm font-semibold text-red-600">{errors.submit}</p>}

          <button type="button" onClick={submitRegister} className="mt-6 w-full rounded-lg bg-primary py-3.5 font-medium text-white shadow-sm hover:bg-primary/90">
            회원가입
          </button>
          <button type="button" onClick={() => navigate('login')} className="mt-4 w-full text-sm text-muted-foreground">
            이미 계정이 있으신가요? <span className="font-semibold text-primary">로그인</span>
          </button>
        </div>
      </main>
    </div>
  );
}
