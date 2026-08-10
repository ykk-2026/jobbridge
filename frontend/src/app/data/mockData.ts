import type { Application, CurrentUser, Job, NotificationItem } from '@/app/types';

export const mockJobs: Job[] = [
  {
    id: '1',
    company: 'Samsung SDS',
    companyInitials: 'S',
    companyColor: '#1B6EF3',
    title: 'Java 백엔드 개발자',
    location: '서울 송파구',
    salary: '4,000만 ~ 6,000만 원',
    workType: '재택근무 가능',
    isRemote: true,
    category: 'IT',
    requirements: ['Java', 'Spring Boot', 'SQL', 'AWS', 'Git'],
    deadline: '2026-08-31',
    posted: '2026-07-15',
    aiScore: 98,
    aiReasons: [
      'Java 역량이 공고 요구사항과 잘 맞습니다.',
      '재택근무가 가능해 근무 조건 적합도가 높습니다.',
      '근무지 접근성 시설이 잘 갖춰져 있습니다.',
      '희망 지역인 서울권과 일치합니다.',
    ],
    description:
      'Samsung SDS에서 클라우드 기반 엔터프라이즈 솔루션을 함께 개발할 Java 백엔드 개발자를 채용합니다. 접근성 시설과 유연한 협업 환경을 지원합니다.',
    benefits: ['4대 보험', '연차 15일', '복지포인트', '중식 지원', '재택근무', '유연근무', '건강검진', '가족 행사 지원'],
    companyDesc:
      'Samsung SDS는 클라우드, AI, 물류 IT, 기업 디지털 전환을 지원하는 IT 서비스 기업입니다.',
    headcount: 3,
    accessibility: { elevator: true, parking: true, wheelchair: true, restroom: true, guideDog: false, hearingLoop: false },
    scores: { skill: 98, workCondition: 96, accessibility: 95, location: 100 },
  },
  {
    id: '2',
    company: 'Kakao',
    companyInitials: 'K',
    companyColor: '#F59E0B',
    title: '프론트엔드 개발자 (React)',
    location: '경기 성남시',
    salary: '4,500만 ~ 7,000만 원',
    workType: '하이브리드',
    isRemote: true,
    category: 'IT',
    requirements: ['React', 'TypeScript', 'JavaScript', 'CSS', 'Git'],
    deadline: '2026-09-15',
    posted: '2026-07-18',
    aiScore: 91,
    aiReasons: [
      'React와 TypeScript 역량이 직무와 잘 맞습니다.',
      '하이브리드 근무로 일정 조정이 가능합니다.',
      '사무실 접근성 지원 범위가 넓습니다.',
      '통근 조건이 비교적 적합합니다.',
    ],
    description:
      'Kakao에서 여러 서비스의 사용자 경험을 개선할 프론트엔드 개발자를 채용합니다.',
    benefits: ['스톡옵션', '동호회 지원', '재택근무', '학습비 지원', '최신 장비', '유연근무', '의료비 지원'],
    companyDesc:
      'Kakao는 메신저, 지도, 커머스, 콘텐츠 등 다양한 모바일 및 플랫폼 서비스를 운영합니다.',
    headcount: 2,
    accessibility: { elevator: true, parking: true, wheelchair: true, restroom: true, guideDog: true, hearingLoop: true },
    scores: { skill: 88, workCondition: 95, accessibility: 98, location: 85 },
  },
  {
    id: '3',
    company: 'LG CNS',
    companyInitials: 'L',
    companyColor: '#8B5CF6',
    title: '데이터 분석가',
    location: '서울 강서구',
    salary: '3,500만 ~ 5,500만 원',
    workType: '사무실 근무',
    isRemote: false,
    category: 'IT',
    requirements: ['Python', 'SQL', 'R', 'Tableau', '통계'],
    deadline: '2026-08-20',
    posted: '2026-07-20',
    aiScore: 85,
    aiReasons: [
      'SQL과 Python 역량이 일부 일치합니다.',
      '근무지에 접근성 시설이 마련되어 있습니다.',
      '희망 지역인 서울권과 일치합니다.',
    ],
    description:
      'LG CNS에서 빅데이터 분석과 AI 솔루션 프로젝트를 담당할 데이터 분석가를 채용합니다.',
    benefits: ['4대 보험', '연차 20일', '의료비 지원', '가족수당', '주거 지원', '교육 지원'],
    companyDesc:
      'LG CNS는 기업 고객을 위한 IT 서비스와 디지털 전환 솔루션을 제공합니다.',
    headcount: 1,
    accessibility: { elevator: true, parking: true, wheelchair: true, restroom: true, guideDog: false, hearingLoop: false },
    scores: { skill: 78, workCondition: 80, accessibility: 90, location: 95 },
  },
  {
    id: '4',
    company: 'SK C&C',
    companyInitials: 'SK',
    companyColor: '#EF4444',
    title: 'UI/UX 디자이너',
    location: '서울 중구',
    salary: '3,200만 ~ 4,800만 원',
    workType: '유연근무',
    isRemote: false,
    category: 'Design',
    requirements: ['Figma', 'Adobe XD', 'Sketch', 'UI 디자인', '사용자 리서치'],
    deadline: '2026-08-25',
    posted: '2026-07-22',
    aiScore: 79,
    aiReasons: [
      '디자인 도구 경험이 요구됩니다.',
      '유연근무가 가능해 일정 조정에 도움이 됩니다.',
      '서울 중심 지역으로 이동 접근성이 좋습니다.',
    ],
    description:
      'SK C&C에서 사용자 중심 디지털 경험을 설계할 UI/UX 디자이너를 채용합니다.',
    benefits: ['4대 보험', '연차 15일', '학습비 지원', '디자인 도구 지원', '유연근무'],
    companyDesc:
      'SK C&C는 SK 그룹의 IT 서비스 기업으로 디지털 혁신을 지원합니다.',
    headcount: 2,
    accessibility: { elevator: true, parking: false, wheelchair: true, restroom: true, guideDog: false, hearingLoop: false },
    scores: { skill: 72, workCondition: 85, accessibility: 80, location: 90 },
  },
  {
    id: '5',
    company: 'Naver',
    companyInitials: 'N',
    companyColor: '#10B981',
    title: '서비스 기획자',
    location: '경기 성남시',
    salary: '4,000만 ~ 6,500만 원',
    workType: '재택근무 가능',
    isRemote: true,
    category: 'Planning',
    requirements: ['서비스 기획', '프레젠테이션', '데이터 분석', '커뮤니케이션', '프로젝트 관리'],
    deadline: '2026-09-01',
    posted: '2026-07-19',
    aiScore: 83,
    aiReasons: [
      '서비스 기획 경험이 일부 일치합니다.',
      '재택근무가 가능합니다.',
      '장애친화 근무환경을 지원합니다.',
    ],
    description:
      'Naver에서 수많은 사용자가 이용하는 제품의 서비스 기획자를 채용합니다.',
    benefits: ['스톡옵션', '재택근무', '학습 지원', '건강검진', '구내식당', '셔틀버스'],
    companyDesc:
      'Naver는 검색, 커머스, 콘텐츠, 플랫폼 서비스를 운영합니다.',
    headcount: 1,
    accessibility: { elevator: true, parking: true, wheelchair: true, restroom: true, guideDog: true, hearingLoop: true },
    scores: { skill: 80, workCondition: 90, accessibility: 95, location: 82 },
  },
  {
    id: '6',
    company: 'Hyundai IT&E',
    companyInitials: 'H',
    companyColor: '#0EA5E9',
    title: '시스템 운영 엔지니어',
    location: '서울 영등포구',
    salary: '3,000만 ~ 4,200만 원',
    workType: '교대근무 없음',
    isRemote: false,
    category: 'IT',
    requirements: ['Linux', 'Shell Script', '모니터링', 'ITSM', 'IT 인프라'],
    deadline: '2026-08-15',
    posted: '2026-07-10',
    aiScore: 76,
    aiReasons: [
      'IT 인프라 운영 역량이 일부 일치합니다.',
      '교대근무가 없어 안정적인 일정이 가능합니다.',
      '기본 접근성 시설이 제공됩니다.',
    ],
    description:
      'Hyundai IT&E에서 안정적인 IT 시스템 운영을 담당할 엔지니어를 채용합니다.',
    benefits: ['4대 보험', '연차 15일', '식대 지원', '통근버스', '가족 행사 지원'],
    companyDesc:
      'Hyundai IT&E는 현대백화점그룹의 IT 서비스를 담당합니다.',
    headcount: 3,
    accessibility: { elevator: true, parking: true, wheelchair: false, restroom: true, guideDog: false, hearingLoop: false },
    scores: { skill: 70, workCondition: 75, accessibility: 78, location: 85 },
  },
];

export const mockUser: CurrentUser = {
  role: 'personal',
  name: '김민준',
  id: 'minjun_kim',
  disability: '지체장애',
  avatar: '김',
};

export const mockCorporateUser: CurrentUser = {
  role: 'corporate',
  name: 'Samsung SDS 채용담당자',
  id: 'samsung_sds_hr',
};

export const mockAdminUser: CurrentUser = {
  role: 'admin',
  name: '관리자',
  id: 'admin',
};

export const categories = [
  { id: 'IT', label: 'IT / 개발', icon: 'Code', count: 342 },
  { id: 'Office', label: '사무 / 경영지원', icon: 'Office', count: 218 },
  { id: 'Design', label: '디자인', icon: 'Design', count: 95 },
  { id: 'Service', label: '서비스 / 지원', icon: 'Service', count: 173 },
  { id: 'Production', label: '생산 / 기술', icon: 'Factory', count: 127 },
];

export const notifications: NotificationItem[] = [
  { id: '1', text: 'Samsung SDS Java 백엔드 개발자 공고가 3일 후 마감됩니다.', time: '방금 전', isNew: true, type: 'deadline' },
  { id: '2', text: 'Kakao 프론트엔드 개발자 지원서가 제출되었습니다.', time: '2시간 전', isNew: true, type: 'apply' },
  { id: '3', text: '맞춤 추천 공고 5건이 새로 등록되었습니다.', time: '1일 전', isNew: false, type: 'ai' },
  { id: '4', text: 'LG CNS에서 서류를 검토 중입니다.', time: '2일 전', isNew: false, type: 'status' },
];

export const applications: Application[] = [
  {
    id: '1',
    job: mockJobs[1],
    status: '서류 검토',
    statusColor: '#F59E0B',
    appliedAt: '2026-07-20',
    updatedAt: '2026-07-22',
    timeline: [
      { step: '지원서 제출', date: '2026-07-20', done: true },
      { step: '서류 검토', date: '2026-07-22', done: true },
      { step: '면접 제안', date: '', done: false },
      { step: '최종 결과', date: '', done: false },
    ],
  },
  {
    id: '2',
    job: mockJobs[2],
    status: '면접 제안',
    statusColor: '#8B5CF6',
    appliedAt: '2026-07-15',
    updatedAt: '2026-07-25',
    timeline: [
      { step: '지원서 제출', date: '2026-07-15', done: true },
      { step: '서류 검토', date: '2026-07-18', done: true },
      { step: '면접 제안', date: '2026-07-25', done: true },
      { step: '최종 결과', date: '', done: false },
    ],
  },
  {
    id: '3',
    job: mockJobs[3],
    status: '지원 완료',
    statusColor: '#1B6EF3',
    appliedAt: '2026-07-28',
    updatedAt: '2026-07-28',
    timeline: [
      { step: '지원서 제출', date: '2026-07-28', done: true },
      { step: '서류 검토', date: '', done: false },
      { step: '면접 제안', date: '', done: false },
      { step: '최종 결과', date: '', done: false },
    ],
  },
];

export const adminStats = {
  totalUsers: 12847,
  totalCompanies: 1523,
  totalJobs: 4218,
  totalApplications: 38492,
  newUsersThisMonth: 423,
  newJobsThisMonth: 312,
  monthlyData: [
    { month: '1월', users: 8200, jobs: 2800, apps: 24000 },
    { month: '2월', users: 8900, jobs: 3100, apps: 27000 },
    { month: '3월', users: 9500, jobs: 3400, apps: 29500 },
    { month: '4월', users: 10200, jobs: 3600, apps: 31000 },
    { month: '5월', users: 11100, jobs: 3900, apps: 34000 },
    { month: '6월', users: 11800, jobs: 4000, apps: 36000 },
    { month: '7월', users: 12847, jobs: 4218, apps: 38492 },
  ],
  disabilityTypes: [
    { name: '지체장애', value: 38, color: '#1B6EF3' },
    { name: '시각장애', value: 12, color: '#10B981' },
    { name: '청각장애', value: 15, color: '#F59E0B' },
    { name: '뇌병변장애', value: 8, color: '#8B5CF6' },
    { name: '지적장애', value: 10, color: '#EF4444' },
    { name: '기타', value: 17, color: '#64748B' },
  ],
};
