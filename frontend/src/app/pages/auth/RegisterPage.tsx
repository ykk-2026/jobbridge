import { ArrowLeft, Briefcase, ClipboardList, Eye, EyeOff, ShieldCheck, Target } from 'lucide-react';
import { useState } from 'react';
import type { Page, RegisterFormData } from '@/app/types';

interface RegisterPageProps {
  navigate: (page: Page) => void;
  onRegister: (formData: RegisterFormData) => void;
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

  return (
    date.getFullYear() === year &&
    date.getMonth() === month - 1 &&
    date.getDate() === day &&
    date <= today &&
    year >= 1900
  );
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

  const checkLoginId = () => {
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

    if (reservedIds.includes(loginId)) {
      setIdAvailable(false);
      setIdCheckMessage('이미 사용 중인 아이디입니다.');
      return;
    }

    setIdAvailable(true);
    setIdCheckMessage('사용 가능한 아이디입니다.');
    setErrors(prev => ({ ...prev, loginId: '' }));
  };

  const validateForm = () => {
    const nextErrors: Record<string, string> = {};
    const loginId = form.loginId.trim().toLowerCase();

    if (!loginId) nextErrors.loginId = '아이디를 입력해 주세요.';
    else if (!/^[a-z0-9]{4,20}$/.test(loginId)) nextErrors.loginId = '아이디는 영문 소문자와 숫자 조합 4~20자로 입력해 주세요.';
    else if (!idAvailable) nextErrors.loginId = '아이디 중복확인을 해 주세요.';

    if (!passwordPattern.test(form.password)) nextErrors.password = '비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해 주세요.';
    if (form.password !== passwordConfirm) nextErrors.passwordConfirm = '비밀번호가 일치하지 않습니다.';
    if (!form.name.trim()) nextErrors.name = '이름을 입력해 주세요.';
    if (!emailPattern.test(form.email.trim())) nextErrors.email = '올바른 이메일 형식으로 입력해 주세요. 예: name@example.com';
    if (!isValidBirthDate(form.birthDate.trim())) nextErrors.birthDate = '생년월일은 YYYY-MM-DD 형식의 실제 날짜로 입력해 주세요. 예: 1999-01-01';
    if (!form.gender) nextErrors.gender = '성별을 선택해 주세요.';
    if (!phonePattern.test(form.phone)) nextErrors.phone = '전화번호는 010-1234-5678 형식으로 입력해 주세요.';

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const submitRegister = () => {
    if (!validateForm()) return;

    onRegister({
      ...form,
      loginId: form.loginId.trim().toLowerCase(),
      name: form.name.trim(),
      email: form.email.trim(),
      phone: form.phone,
      birthDate: form.birthDate.trim(),
    });
    window.alert('회원가입이 완료되었습니다. 로그인해 주세요.');
    navigate('login');
  };

  return (
    <div className="min-h-screen bg-[#F3F7FF] flex items-stretch justify-center">
      <aside className="hidden lg:flex w-[360px] bg-gradient-to-b from-white to-[#EAF4FF] px-8 py-10 flex-col justify-between border-r border-[#DCEAF3]">
        <div>
          <button type="button" onClick={() => navigate('main')} className="flex items-center gap-2 font-bold text-foreground mb-12">
            <span className="w-10 h-10 rounded-xl bg-primary text-white flex items-center justify-center">
              <Briefcase size={18} />
            </span>
            JobBridgeAI
          </button>

          <h1 className="text-3xl font-bold leading-tight text-foreground">
            당신의 가능성을
            <br />
            <span className="text-primary">일할 기회로</span>
          </h1>
          <p className="text-sm text-muted-foreground leading-6 mt-6">
            역량과 접근성 조건에 맞는 일자리를 추천하고, 지원 과정을 쉽게 관리할 수 있도록 도와드립니다.
          </p>

          <div className="space-y-7 mt-12">
            {[
              { title: '맞춤형 추천', desc: '입력한 조건에 맞는 채용 공고를 추천합니다.', icon: Target },
              { title: '간편한 지원', desc: '프로필을 기반으로 빠르게 지원할 수 있습니다.', icon: ClipboardList },
              { title: '안전한 관리', desc: '개인정보와 지원 현황을 안정적으로 관리합니다.', icon: ShieldCheck },
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

      <main className="w-full max-w-4xl bg-white px-5 py-8 sm:px-8 lg:px-10 lg:py-10">
        <div className="max-w-3xl mx-auto">
          <button
            type="button"
            onClick={onBack}
            className="mb-5 inline-flex items-center gap-2 rounded-lg border border-border px-3 py-2 text-sm font-semibold text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <ArrowLeft size={16} />
            뒤로가기
          </button>

          <button type="button" onClick={() => navigate('main')} className="lg:hidden flex items-center gap-2 font-bold text-foreground mb-8">
            <span className="w-10 h-10 rounded-xl bg-primary text-white flex items-center justify-center">
              <Briefcase size={18} />
            </span>
            JobBridgeAI
          </button>

          <div className="mb-8">
            <h1 className="text-2xl font-bold text-foreground">회원가입</h1>
            <p className="text-sm text-muted-foreground mt-2">회원 정보를 정확히 입력해 주세요.</p>
          </div>

          <div className="mb-7">
            <h2 className="text-center text-xl font-bold text-foreground">개인 회원가입</h2>
            <div className="mt-4 h-px bg-border" />
          </div>

          <div className="space-y-4">
            <label className="block">
              <span className="block text-sm font-medium mb-2">아이디 <span className="text-red-500">*</span></span>
              <div className="flex gap-2">
                <input
                  value={form.loginId}
                  onChange={event => updateField('loginId', event.target.value)}
                  className={`flex-1 px-3 py-3 rounded-lg border ${idCheckMessage ? (idAvailable ? 'border-green-500' : 'border-red-400') : 'border-border'}`}
                  placeholder="영문 소문자, 숫자 조합 4~20자"
                />
                <button type="button" onClick={checkLoginId} className="px-4 rounded-lg border border-border text-sm font-medium text-muted-foreground hover:bg-muted">
                  중복확인
                </button>
              </div>
              {idCheckMessage && <p className={`mt-2 text-sm ${idAvailable ? 'text-green-600' : 'text-red-500'}`}>{idCheckMessage}</p>}
              {errors.loginId && <p className="mt-2 text-sm text-red-500">{errors.loginId}</p>}
            </label>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <label className="block">
                <span className="block text-sm font-medium mb-2">비밀번호 <span className="text-red-500">*</span></span>
                <div className="relative">
                  <input
                    value={form.password}
                    onChange={event => updateField('password', event.target.value)}
                    type={showPw ? 'text' : 'password'}
                    className="w-full px-3 py-3 pr-10 rounded-lg border border-border"
                    placeholder="영문, 숫자, 특수문자 포함 8~20자"
                  />
                  <button type="button" aria-label="비밀번호 보기" onClick={() => setShowPw(prev => !prev)} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                    {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.password && <p className="mt-2 text-sm text-red-500">{errors.password}</p>}
              </label>

              <label className="block">
                <span className="block text-sm font-medium mb-2">비밀번호 확인 <span className="text-red-500">*</span></span>
                <div className="relative">
                  <input
                    value={passwordConfirm}
                    onChange={event => {
                      setPasswordConfirm(event.target.value);
                      setErrors(prev => ({ ...prev, passwordConfirm: '' }));
                    }}
                    type={showPwConfirm ? 'text' : 'password'}
                    className="w-full px-3 py-3 pr-10 rounded-lg border border-border"
                    placeholder="비밀번호를 다시 입력해 주세요"
                  />
                  <button type="button" aria-label="비밀번호 확인 보기" onClick={() => setShowPwConfirm(prev => !prev)} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                    {showPwConfirm ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.passwordConfirm && <p className="mt-2 text-sm text-red-500">{errors.passwordConfirm}</p>}
              </label>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <label className="block">
                <span className="block text-sm font-medium mb-2">이름 <span className="text-red-500">*</span></span>
                <input
                  value={form.name}
                  onChange={event => updateField('name', event.target.value)}
                  className="w-full px-3 py-3 rounded-lg border border-border"
                  placeholder="이름을 입력해 주세요"
                />
                {errors.name && <p className="mt-2 text-sm text-red-500">{errors.name}</p>}
              </label>

              <label className="block">
                <span className="block text-sm font-medium mb-2">이메일 <span className="text-red-500">*</span></span>
                <input
                  value={form.email}
                  onChange={event => updateField('email', event.target.value)}
                  type="email"
                  className="w-full px-3 py-3 rounded-lg border border-border"
                  placeholder="예: name@example.com"
                />
                {errors.email && <p className="mt-2 text-sm text-red-500">{errors.email}</p>}
              </label>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <label className="block">
                <span className="block text-sm font-medium mb-2">생년월일 <span className="text-red-500">*</span></span>
                <input
                  value={form.birthDate}
                  onChange={event => updateField('birthDate', formatBirthDate(event.target.value))}
                  type="text"
                  inputMode="numeric"
                  className="w-full px-3 py-3 rounded-lg border border-border"
                  placeholder="예: 1999-01-01"
                />
                {errors.birthDate && <p className="mt-2 text-sm text-red-500">{errors.birthDate}</p>}
              </label>

              <label className="block">
                <span className="block text-sm font-medium mb-2">성별 <span className="text-red-500">*</span></span>
                <select
                  value={form.gender}
                  onChange={event => updateField('gender', event.target.value)}
                  className="w-full px-3 py-3 rounded-lg border border-border bg-white"
                >
                  <option value="">성별을 선택해 주세요</option>
                  <option value="남성">남성</option>
                  <option value="여성">여성</option>
                  <option value="선택 안 함">선택 안 함</option>
                </select>
                {errors.gender && <p className="mt-2 text-sm text-red-500">{errors.gender}</p>}
              </label>
            </div>

            <label className="block">
              <span className="block text-sm font-medium mb-2">전화번호 <span className="text-red-500">*</span></span>
              <input
                value={form.phone}
                onChange={event => {
                  updateField('phone', formatPhoneNumber(event.target.value));
                  setErrors(prev => ({ ...prev, phone: '' }));
                }}
                inputMode="numeric"
                className="w-full px-3 py-3 rounded-lg border border-border"
                placeholder="예: 010-1234-5678"
              />
              {errors.phone && <p className="mt-2 text-sm text-red-500">{errors.phone}</p>}
            </label>

            <label className="block">
              <span className="block text-sm font-medium mb-2">희망 직무</span>
              <input
                value={form.preferredRole}
                onChange={event => updateField('preferredRole', event.target.value)}
                className="w-full px-3 py-3 rounded-lg border border-border"
                placeholder="희망하는 직무를 입력해 주세요"
              />
            </label>
          </div>

          <button type="button" onClick={submitRegister} className="mt-6 w-full py-3.5 rounded-lg bg-primary text-white font-medium shadow-sm hover:bg-primary/90">
            회원가입
          </button>
          <button type="button" onClick={() => navigate('login')} className="w-full mt-4 text-sm text-muted-foreground">
            이미 계정이 있으신가요? <span className="font-semibold text-primary">로그인</span>
          </button>
        </div>
      </main>
    </div>
  );
}
