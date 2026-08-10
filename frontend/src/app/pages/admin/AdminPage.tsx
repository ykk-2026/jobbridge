import { useState } from 'react';
import { AlertCircle, BarChart3, Briefcase, Building2, Check, ChevronDown, Search, TrendingUp, Users, X } from 'lucide-react';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { adminStats } from '@/app/data/mockData';

const mockMembers = [
  { id: '1', name: '김민준', email: 'minjun@example.com', joined: '2026-07-01', status: '활성', role: '구직자' },
  { id: '2', name: '이지은', email: 'jieun@example.com', joined: '2026-07-05', status: '활성', role: '구직자' },
  { id: '3', name: '박성민', email: 'sungmin@example.com', joined: '2026-07-10', status: '활성', role: '구직자' },
  { id: '4', name: 'Samsung SDS', email: 'hr@samsung-sds.com', joined: '2026-06-15', status: '인증완료', role: '기업' },
  { id: '5', name: 'Kakao', email: 'recruit@kakao.com', joined: '2026-06-20', status: '검토중', role: '기업' },
  { id: '6', name: '최수진', email: 'sujin@example.com', joined: '2026-07-20', status: '활성', role: '구직자' },
];

const mockJobs = [
  { id: '1', company: 'Samsung SDS', title: 'Java 백엔드 개발자', applicants: 128, status: '진행중', posted: '2026-07-15' },
  { id: '2', company: 'Kakao', title: '프론트엔드 개발자', applicants: 93, status: '진행중', posted: '2026-07-18' },
  { id: '3', company: 'LG CNS', title: '데이터 분석가', applicants: 64, status: '진행중', posted: '2026-07-20' },
  { id: '4', company: 'SK C&C', title: 'UI/UX 디자이너', applicants: 41, status: '마감임박', posted: '2026-07-22' },
];

export function AdminPage() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'members' | 'companies' | 'jobs'>('dashboard');
  const [searchQuery, setSearchQuery] = useState('');

  const tabs = [
    { id: 'dashboard', label: '대시보드', icon: BarChart3 },
    { id: 'members', label: '회원', icon: Users },
    { id: 'companies', label: '기업', icon: Building2 },
    { id: 'jobs', label: '채용공고', icon: Briefcase },
  ] as const;

  const topStats = [
    { label: '전체 회원', value: adminStats.totalUsers.toLocaleString(), sub: `이번 달 +${adminStats.newUsersThisMonth}`, icon: Users, color: '#1B6EF3' },
    { label: '등록 기업', value: adminStats.totalCompanies.toLocaleString(), sub: '1,021개 인증 완료', icon: Building2, color: '#10B981' },
    { label: '채용공고', value: adminStats.totalJobs.toLocaleString(), sub: `이번 달 +${adminStats.newJobsThisMonth}`, icon: Briefcase, color: '#8B5CF6' },
    { label: '지원 건수', value: adminStats.totalApplications.toLocaleString(), sub: '월평균 5,499건', icon: TrendingUp, color: '#F59E0B' },
  ];

  const statusBadge = (status: string) => {
    const cfg: Record<string, string> = {
      활성: '#10B981',
      인증완료: '#1B6EF3',
      검토중: '#F59E0B',
      진행중: '#10B981',
      마감임박: '#F59E0B',
    };

    return (
      <span className="px-2.5 py-0.5 rounded-full text-xs font-medium text-white" style={{ backgroundColor: cfg[status] || '#64748B' }}>
        {status}
      </span>
    );
  };

  const filteredMembers = mockMembers.filter(member =>
    [member.name, member.email, member.role, member.status].some(value =>
      value.toLowerCase().includes(searchQuery.toLowerCase()),
    ),
  );

  const visibleMembers = activeTab === 'companies'
    ? filteredMembers.filter(member => member.role === '기업')
    : filteredMembers;

  return (
    <div className="min-h-screen py-8" style={{ backgroundColor: '#F5F8FF' }}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <div className="w-8 h-8 rounded-lg flex items-center justify-center text-white" style={{ backgroundColor: '#8B5CF6' }}>
                <AlertCircle size={14} />
              </div>
              <span className="text-sm text-muted-foreground">관리자 콘솔</span>
            </div>
            <h1 className="font-bold text-foreground" style={{ fontSize: '1.5rem' }}>JobBridgeAI 관리자</h1>
          </div>
          <div className="text-xs text-muted-foreground">마지막 업데이트: 2026-07-31 09:00</div>
        </div>

        <div className="flex gap-1 bg-white rounded-xl border border-border p-1 mb-8 overflow-x-auto">
          {tabs.map(tab => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-sm font-medium transition-all whitespace-nowrap ${
                  activeTab === tab.id ? 'bg-primary text-white shadow-sm' : 'text-muted-foreground hover:text-foreground'
                }`}
              >
                <Icon size={14} />
                {tab.label}
              </button>
            );
          })}
        </div>

        {activeTab === 'dashboard' && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {topStats.map(stat => {
                const Icon = stat.icon;
                return (
                  <div key={stat.label} className="bg-white rounded-xl border border-border p-5">
                    <div className="flex items-center justify-between mb-4">
                      <div className="w-10 h-10 rounded-lg flex items-center justify-center text-white" style={{ backgroundColor: stat.color }}>
                        <Icon size={18} />
                      </div>
                      <ChevronDown size={14} className="text-muted-foreground" />
                    </div>
                    <p className="text-sm text-muted-foreground mb-1">{stat.label}</p>
                    <p className="font-bold text-foreground" style={{ fontSize: '1.5rem' }}>{stat.value}</p>
                    <p className="text-xs text-muted-foreground mt-1">{stat.sub}</p>
                  </div>
                );
              })}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="bg-white rounded-xl border border-border p-5">
                <h3 className="font-medium text-foreground mb-4">월별 회원 증가</h3>
                <ResponsiveContainer width="100%" height={220}>
                  <AreaChart data={adminStats.monthlyData}>
                    <defs>
                      <linearGradient id="colorUsers" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#1B6EF3" stopOpacity={0.35} />
                        <stop offset="95%" stopColor="#1B6EF3" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
                    <XAxis dataKey="month" tick={{ fontSize: 11, fill: '#64748B' }} />
                    <YAxis tick={{ fontSize: 11, fill: '#64748B' }} />
                    <Tooltip />
                    <Area type="monotone" dataKey="users" stroke="#1B6EF3" fill="url(#colorUsers)" strokeWidth={2} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>

              <div className="bg-white rounded-xl border border-border p-5">
                <h3 className="font-medium text-foreground mb-4">채용공고 및 지원 수</h3>
                <ResponsiveContainer width="100%" height={220}>
                  <BarChart data={adminStats.monthlyData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
                    <XAxis dataKey="month" tick={{ fontSize: 11, fill: '#64748B' }} />
                    <YAxis tick={{ fontSize: 11, fill: '#64748B' }} />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="jobs" name="채용공고" fill="#8B5CF6" radius={[4, 4, 0, 0]} />
                    <Bar dataKey="apps" name="지원" fill="#F59E0B" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="bg-white rounded-xl border border-border p-5 lg:col-span-2">
                <h3 className="font-medium text-foreground mb-4">장애 유형 분포</h3>
                <div className="grid grid-cols-1 lg:grid-cols-[320px_1fr] gap-4 items-center">
                  <ResponsiveContainer width="100%" height={220}>
                    <PieChart>
                      <Pie data={adminStats.disabilityTypes} cx="50%" cy="50%" innerRadius={55} outerRadius={80} paddingAngle={3} dataKey="value">
                        {adminStats.disabilityTypes.map(entry => (
                          <Cell key={entry.name} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                    {adminStats.disabilityTypes.map(item => (
                      <div key={item.name} className="flex items-center gap-2 text-sm text-muted-foreground">
                        <span className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }} />
                        <span>{item.name}</span>
                        <span className="font-medium text-foreground">{item.value}%</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {(activeTab === 'members' || activeTab === 'companies') && (
          <div className="bg-white rounded-xl border border-border overflow-hidden">
            <div className="p-5 border-b border-border">
              <div className="relative max-w-sm">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                <input
                  value={searchQuery}
                  onChange={event => setSearchQuery(event.target.value)}
                  placeholder="회원 검색"
                  className="w-full pl-9 pr-3 py-2 rounded-lg border border-border bg-background text-sm outline-none focus:ring-2 focus:ring-primary/20"
                />
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-muted/40 text-muted-foreground">
                  <tr>
                    <th className="px-5 py-3 text-left font-medium">이름</th>
                    <th className="px-5 py-3 text-left font-medium">이메일</th>
                    <th className="px-5 py-3 text-left font-medium">유형</th>
                    <th className="px-5 py-3 text-left font-medium">가입일</th>
                    <th className="px-5 py-3 text-left font-medium">상태</th>
                    <th className="px-5 py-3 text-right font-medium">관리</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleMembers.map(member => (
                    <tr key={member.id} className="border-t border-border">
                      <td className="px-5 py-4 font-medium text-foreground">{member.name}</td>
                      <td className="px-5 py-4 text-muted-foreground">{member.email}</td>
                      <td className="px-5 py-4 text-muted-foreground">{member.role}</td>
                      <td className="px-5 py-4 text-muted-foreground">{member.joined}</td>
                      <td className="px-5 py-4">{statusBadge(member.status)}</td>
                      <td className="px-5 py-4">
                        <div className="flex justify-end gap-2">
                          <button aria-label="승인" className="w-8 h-8 rounded-lg border border-border flex items-center justify-center text-green-600 hover:bg-green-50">
                            <Check size={14} />
                          </button>
                          <button aria-label="거절" className="w-8 h-8 rounded-lg border border-border flex items-center justify-center text-red-600 hover:bg-red-50">
                            <X size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {activeTab === 'jobs' && (
          <div className="bg-white rounded-xl border border-border overflow-hidden">
            <div className="p-5 border-b border-border">
              <h2 className="font-semibold text-foreground">채용공고 관리</h2>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-muted/40 text-muted-foreground">
                  <tr>
                    <th className="px-5 py-3 text-left font-medium">기업</th>
                    <th className="px-5 py-3 text-left font-medium">공고명</th>
                    <th className="px-5 py-3 text-left font-medium">지원자</th>
                    <th className="px-5 py-3 text-left font-medium">등록일</th>
                    <th className="px-5 py-3 text-left font-medium">상태</th>
                  </tr>
                </thead>
                <tbody>
                  {mockJobs.map(job => (
                    <tr key={job.id} className="border-t border-border">
                      <td className="px-5 py-4 font-medium text-foreground">{job.company}</td>
                      <td className="px-5 py-4 text-muted-foreground">{job.title}</td>
                      <td className="px-5 py-4 text-muted-foreground">{job.applicants}</td>
                      <td className="px-5 py-4 text-muted-foreground">{job.posted}</td>
                      <td className="px-5 py-4">{statusBadge(job.status)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
