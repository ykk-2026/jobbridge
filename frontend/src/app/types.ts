export type Page =
  | 'main'
  | 'login'
  | 'register'
  | 'user-dashboard'
  | 'ai-recommend'
  | 'jobs'
  | 'job-detail'
  | 'saved'
  | 'applications'
  | 'corporate'
  | 'admin';

export type UserRole = 'personal' | 'corporate' | 'admin';

export interface CurrentUser {
  role: UserRole;
  name: string;
  id: string;
  disability?: string;
  avatar?: string;
  loginId?: string;
  email?: string;
  phone?: string;
  birthDate?: string;
  gender?: string;
  preferredRole?: string;
}

export interface RegisterFormData {
  loginId: string;
  password: string;
  birthDate: string;
  name: string;
  email: string;
  phone: string;
  gender: string;
  preferredRole: string;
}

export interface Job {
  id: string;
  company: string;
  companyInitials: string;
  companyColor: string;
  title: string;
  location: string;
  salary: string;
  workType: string;
  isRemote: boolean;
  category: string;
  requirements: string[];
  deadline: string;
  posted: string;
  aiScore: number;
  aiReasons: string[];
  description: string;
  benefits: string[];
  companyDesc: string;
  headcount: number;
  accessibility: {
    elevator: boolean;
    parking: boolean;
    wheelchair: boolean;
    restroom: boolean;
    guideDog: boolean;
    hearingLoop: boolean;
  };
  scores: {
    skill: number;
    workCondition: number;
    accessibility: number;
    location: number;
  };
}

export interface NotificationItem {
  id: string;
  text: string;
  time: string;
  isNew: boolean;
  type: 'deadline' | 'apply' | 'ai' | 'status';
}

export interface ApplicationTimelineStep {
  step: string;
  date: string;
  done: boolean;
}

export interface Application {
  id: string;
  job: Job;
  status: string;
  statusColor: string;
  appliedAt: string;
  updatedAt: string;
  timeline: ApplicationTimelineStep[];
}

