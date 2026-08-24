import { CheckCircle2, ChevronDown, HelpCircle, Mail, MapPin, MessageSquareText, Phone, Search, Send } from 'lucide-react';
import { useMemo, useState } from 'react';

const faqItems = [
  {
    category: '지원',
    question: '공고 지원 후 어디서 확인하나요?',
    answer: '로그인 후 프로필의 지원관리에서 지원 완료 공고와 진행 상태를 확인할 수 있습니다.',
  },
  {
    category: '계정',
    question: '기업회원은 채용 관리를 어디서 하나요?',
    answer: '기업회원으로 로그인한 뒤 프로필 메뉴에서 등록 공고와 지원자 관리를 확인할 수 있습니다.',
  },
  {
    category: '접근성',
    question: '접근성 정보가 미확인인 경우 어떻게 하나요?',
    answer: '공고 상세의 안심번호 문의 또는 고객지원 문의를 통해 담당자 확인을 요청할 수 있습니다.',
  },
  {
    category: '알바',
    question: '알바 공고에서 꼭 확인해야 할 정보는 무엇인가요?',
    answer: '시급 또는 일급, 근무시간, 휴게시간, 근로계약서 작성 여부, 임금 지급일을 확인하세요.',
  },
];

const categories = ['전체', '지원', '계정', '접근성', '알바'];

export function SupportPage() {
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('전체');
  const [openedFaqId, setOpenedFaqId] = useState(faqItems[0].question);
  const [form, setForm] = useState({ type: '지원 문의', name: '', email: '', message: '' });
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState('');

  const filteredFaqs = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    return faqItems.filter(item => {
      if (category !== '전체' && item.category !== category) return false;
      if (!normalizedQuery) return true;

      return [item.category, item.question, item.answer].join(' ').toLowerCase().includes(normalizedQuery);
    });
  }, [category, query]);

  const updateForm = (field: keyof typeof form, value: string) => {
    setForm(prev => ({ ...prev, [field]: value }));
    setError('');
    setSubmitted(false);
  };

  const submitInquiry = () => {
    if (!form.name.trim() || !form.email.includes('@') || form.message.trim().length < 10) {
      setError('이름, 이메일, 문의 내용을 정확히 입력해 주세요. 문의 내용은 10자 이상이어야 합니다.');
      return;
    }

    setSubmitted(true);
    setError('');
    setForm(prev => ({ ...prev, message: '' }));
  };

  return (
    <div className="min-h-screen bg-[#F6F7F9] text-[#111827]">
      <main className="mx-auto max-w-[1256px] px-5 py-6 sm:px-8">
        <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
          <span className="rounded-full bg-[#E4EFFF] px-4 py-1.5 text-xs font-extrabold text-[#0D6BEA]">고객센터</span>
          <h1 className="mt-3 text-3xl font-extrabold text-black">고객지원</h1>
          <p className="mt-2 text-sm font-semibold text-[#596273]">채용정보와 지원 과정에서 필요한 도움을 빠르게 확인하세요.</p>

          <div className="mt-5 grid gap-3 lg:grid-cols-[1fr_auto]">
            <div className="relative">
              <Search size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-[#344054]" />
              <input
                value={query}
                onChange={event => setQuery(event.target.value)}
                placeholder="지원, 로그인, 접근성, 알바 문의 검색"
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

        <div className="mt-5 grid gap-5 lg:grid-cols-[1fr_360px]">
          <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
            <h2 className="flex items-center gap-2 text-lg font-extrabold text-black">
              <HelpCircle size={20} />
              자주 묻는 질문
            </h2>
            <div className="mt-4 divide-y divide-[#E7ECF2]">
              {filteredFaqs.map(item => (
                <div key={item.question} className="py-4">
                  <button
                    type="button"
                    onClick={() => setOpenedFaqId(prev => (prev === item.question ? '' : item.question))}
                    className="flex w-full items-center justify-between gap-4 text-left"
                  >
                    <span>
                      <span className="rounded-full bg-[#F1F3F6] px-2.5 py-1 text-xs font-extrabold text-[#596273]">{item.category}</span>
                      <span className="mt-2 block text-base font-extrabold text-black">{item.question}</span>
                    </span>
                    <ChevronDown size={19} className={`shrink-0 transition ${openedFaqId === item.question ? 'rotate-180 text-[#0D6BEA]' : 'text-[#8A94A6]'}`} />
                  </button>
                  {openedFaqId === item.question && <p className="mt-3 rounded-lg bg-[#F8FAFC] px-4 py-3 text-sm font-semibold leading-7 text-[#344054]">{item.answer}</p>}
                </div>
              ))}
            </div>
          </section>

          <aside className="space-y-5">
            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="flex items-center gap-2 text-lg font-extrabold text-black">
                <MessageSquareText size={20} />
                1:1 문의
              </h2>
              <div className="mt-4 grid gap-3">
                <select value={form.type} onChange={event => updateForm('type', event.target.value)} className="h-11 rounded-lg border border-[#D7DDE5] bg-white px-3 text-sm font-bold outline-none">
                  <option>지원 문의</option>
                  <option>기업회원 문의</option>
                  <option>접근성 정보 문의</option>
                  <option>계정 문의</option>
                </select>
                <input value={form.name} onChange={event => updateForm('name', event.target.value)} placeholder="이름" className="h-11 rounded-lg border border-[#D7DDE5] px-3 text-sm font-semibold outline-none" />
                <input value={form.email} onChange={event => updateForm('email', event.target.value)} placeholder="이메일" className="h-11 rounded-lg border border-[#D7DDE5] px-3 text-sm font-semibold outline-none" />
                <textarea value={form.message} onChange={event => updateForm('message', event.target.value)} placeholder="문의 내용을 입력해 주세요." className="min-h-28 rounded-lg border border-[#D7DDE5] px-3 py-3 text-sm font-semibold outline-none" />
              </div>
              {error && <p className="mt-3 rounded-lg bg-[#FFF5F5] px-3 py-2 text-sm font-bold text-[#D92D20]">{error}</p>}
              {submitted && (
                <p className="mt-3 flex items-center gap-2 rounded-lg bg-[#E7F8EF] px-3 py-2 text-sm font-bold text-[#14843C]">
                  <CheckCircle2 size={16} /> 문의가 접수되었습니다.
                </p>
              )}
              <button type="button" onClick={submitInquiry} className="mt-4 inline-flex h-11 w-full items-center justify-center gap-2 rounded-lg bg-[#0D6BEA] text-sm font-extrabold text-white hover:bg-[#0959C7]">
                <Send size={17} />
                문의 접수
              </button>
            </section>

            <section className="rounded-xl border border-[#DDE3EA] bg-white p-5 shadow-sm">
              <h2 className="text-lg font-extrabold text-black">연락처</h2>
              <div className="mt-4 space-y-3 text-sm font-bold text-[#596273]">
                <p className="flex items-center gap-2"><Phone size={16} /> 1588-0000</p>
                <p className="flex items-center gap-2"><Mail size={16} /> help@ileeum.ai</p>
                <p className="flex items-center gap-2"><MapPin size={16} /> 서울, 대한민국</p>
              </div>
            </section>
          </aside>
        </div>
      </main>
    </div>
  );
}
