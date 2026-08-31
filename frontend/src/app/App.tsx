import { useEffect, useState } from 'react';
import type { ApplicationFormData, CurrentUser, Page, RegisterFormData, UserRole } from '@/app/types';
import { mockAdminUser, mockCorporateUser, mockJobs, mockUser } from '@/app/data/mockData';
import { Navbar } from '@/app/layout/Navbar';
import { Footer } from '@/app/layout/Footer';
import { MainPage } from '@/app/pages/home/MainPage';
import { LoginPage } from '@/app/pages/auth/LoginPage';
import { RegisterPage } from '@/app/pages/auth/RegisterPage';
import { ProfilePage } from '@/app/pages/profile/ProfilePage';
import { JobDetailPage } from '@/app/pages/jobs/JobDetailPage';
import { JobsPage } from '@/app/pages/jobs/JobsPage';
import { SupportPage } from '@/app/pages/support/SupportPage';
import { AdminPage } from '@/app/pages/admin/AdminPage';
import { CommunityPage } from '@/app/pages/community/CommunityPage';
import {
  getCurrentMember,
  loginMember,
  logoutMember,
  registerMember,
  type LoginMember,
} from '@/app/api/memberApi';
import { saveJobApplication } from '@/app/api/jobApplicationApi';
import { deleteInterestJob, getInterestJobs, saveInterestJob } from '@/app/api/interestJobApi';

// MARKER-MAKE-KIT-INVOKED
const noNavPages: Page[] = ['login', 'register'];
const registeredUserStorageKey = 'jobBridgeRegisteredUser';
const autoLoginStorageKey = 'jobBridgeAutoLogin';
const currentPageStorageKey = 'jobBridgeCurrentPage';
const currentJobStorageKey = 'jobBridgeCurrentJob';
const appliedJobsStorageKey = 'jobBridgeAppliedJobs';
const applicationFormsStorageKey = 'jobBridgeApplicationForms';
const pendingApplicationStorageKey = 'jobBridgePendingApplicationJob';
const pageValues: Page[] = [
  'main',
  'login',
  'register',
  'user-dashboard',
  'jobs',
  'job-detail',
  'saved',
  'applications',
  'ai-recommend',
  'support',
  'community',
  'corporate',
  'admin',
];

interface AutoLoginSession {
  role: UserRole;
  loginId?: string;
}

const toCurrentUser = (member: LoginMember): CurrentUser => {
  const role: UserRole = member.role === 'ADMIN'
    ? 'admin'
    : member.role === 'COMPANY' || member.role === 'CORPORATE' || member.role === 'EMPLOYER'
      ? 'corporate'
      : 'personal';

  return {
    role,
    name: member.name,
    id: String(member.id),
    avatar: member.name.slice(0, 1),
    loginId: member.loginId,
  };
};

const loadRegisteredUser = (): RegisterFormData | null => {
  if (typeof window === 'undefined') return null;

  const savedUser = localStorage.getItem(registeredUserStorageKey);
  if (!savedUser) return null;

  try {
    return JSON.parse(savedUser) as RegisterFormData;
  } catch {
    localStorage.removeItem(registeredUserStorageKey);
    return null;
  }
};

const loadAutoLoginSession = (): AutoLoginSession | null => {
  if (typeof window === 'undefined') return null;

  const savedSession = localStorage.getItem(autoLoginStorageKey);
  if (!savedSession) return null;

  try {
    return JSON.parse(savedSession) as AutoLoginSession;
  } catch {
    localStorage.removeItem(autoLoginStorageKey);
    return null;
  }
};

const saveAutoLoginSession = (session: AutoLoginSession) => {
  localStorage.setItem(autoLoginStorageKey, JSON.stringify(session));
};

const clearAutoLoginSession = () => {
  localStorage.removeItem(autoLoginStorageKey);
  localStorage.removeItem('savedLoginId');
};

const loadCurrentPage = (): Page | null => {
  if (typeof window === 'undefined') return null;

  const savedPage = localStorage.getItem(currentPageStorageKey);
  if (!savedPage) return null;

  return pageValues.includes(savedPage as Page) ? (savedPage as Page) : null;
};

const saveCurrentPage = (page: Page) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem(currentPageStorageKey, page);
};

const loadCurrentJobId = () => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(currentJobStorageKey);
};

const saveCurrentJobId = (jobId?: string) => {
  if (typeof window === 'undefined') return;
  if (jobId) localStorage.setItem(currentJobStorageKey, jobId);
};

const normalizeJobId = (id: string) => id.replace(/^job-/, '');

const loadAppliedJobIds = () => {
  if (typeof window === 'undefined') return new Set<string>();

  const savedJobs = localStorage.getItem(appliedJobsStorageKey);
  if (!savedJobs) return new Set<string>();

  try {
    const parsedJobs = JSON.parse(savedJobs) as string[];
    return new Set(parsedJobs.map(normalizeJobId));
  } catch {
    localStorage.removeItem(appliedJobsStorageKey);
    return new Set<string>();
  }
};

const saveAppliedJobIds = (jobIds: Set<string>) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem(appliedJobsStorageKey, JSON.stringify(Array.from(jobIds).map(normalizeJobId)));
};

const loadApplicationForms = () => {
  if (typeof window === 'undefined') return {} as Record<string, ApplicationFormData>;

  const savedForms = localStorage.getItem(applicationFormsStorageKey);
  if (!savedForms) return {} as Record<string, ApplicationFormData>;

  try {
    const parsedForms = JSON.parse(savedForms) as Record<string, ApplicationFormData>;
    return Object.fromEntries(
      Object.entries(parsedForms).map(([jobId, form]) => [normalizeJobId(jobId), form]),
    ) as Record<string, ApplicationFormData>;
  } catch {
    localStorage.removeItem(applicationFormsStorageKey);
    return {} as Record<string, ApplicationFormData>;
  }
};

const saveApplicationForms = (forms: Record<string, ApplicationFormData>) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem(applicationFormsStorageKey, JSON.stringify(forms));
};

const loadPendingApplicationJobId = () => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(pendingApplicationStorageKey);
};

const savePendingApplicationJobId = (jobId: string) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem(pendingApplicationStorageKey, jobId);
};

const clearPendingApplicationJobId = () => {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(pendingApplicationStorageKey);
};

const createUserFromForm = (role: UserRole, formData: RegisterFormData): CurrentUser => ({
  role,
  name: formData.name.trim() || '구직자',
  id: formData.loginId.trim() || 'new_user',
  avatar: (formData.name.trim() || '회').slice(0, 1),
  loginId: formData.loginId,
  email: formData.email,
  phone: formData.phone,
  currentRegion: formData.currentRegion,
  birthDate: formData.birthDate,
  gender: formData.gender,
  preferredRole: formData.preferredRole,
});

const getMockUserByRole = (role: UserRole): CurrentUser => {
  if (role === 'corporate') return mockCorporateUser;
  if (role === 'admin') return mockAdminUser;
  return mockUser;
};

const getAutoLoggedInUser = (registeredUser: RegisterFormData | null): CurrentUser | null => {
  const session = loadAutoLoginSession();
  if (!session) return null;

  if (session.role === 'personal' && session.loginId && registeredUser?.loginId.trim() === session.loginId) {
    return createUserFromForm('personal', registeredUser);
  }

  if (session.role === 'personal' && session.loginId === 'demo') {
    return mockUser;
  }

  if (session.role === 'corporate' || session.role === 'admin') {
    return getMockUserByRole(session.role);
  }

  clearAutoLoginSession();
  return null;
};

export default function App() {
  const [registeredUser, setRegisteredUser] = useState<RegisterFormData | null>(() => loadRegisteredUser());
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [currentPage, setCurrentPage] = useState<Page>(() => loadCurrentPage() || 'main');
  const [currentJobId, setCurrentJobId] = useState<string | null>(() => loadCurrentJobId());
  const [pageHistory, setPageHistory] = useState<Page[]>([]);
  const [bookmarks, setBookmarks] = useState<Set<string>>(new Set());
  const [appliedJobIds, setAppliedJobIds] = useState<Set<string>>(() => loadAppliedJobIds());
  const [applicationForms, setApplicationForms] = useState<Record<string, ApplicationFormData>>(() => loadApplicationForms());
  const [pendingApplicationJobId, setPendingApplicationJobId] = useState<string | null>(() => loadPendingApplicationJobId());
  const [headerSearchQuery, setHeaderSearchQuery] = useState('');

  useEffect(() => {
    getCurrentMember()
      .then(member => setCurrentUser(member ? toCurrentUser(member) : null))
      .catch(() => setCurrentUser(null));
  }, []);

  useEffect(() => {
    if (!currentUser) {
      setBookmarks(new Set());
      return;
    }

    let cancelled = false;
    getInterestJobs()
      .then(savedJobs => {
        if (cancelled) return;

        const savedBookmarkIds = savedJobs.reduce<Set<string>>((ids, savedJob) => {
          const matchedJob = mockJobs.find(job => (
            job.company === savedJob.companyName && job.title === savedJob.title
          ));
          if (matchedJob) ids.add(`job-${matchedJob.id}`);
          return ids;
        }, new Set());
        setBookmarks(savedBookmarkIds);
      })
      .catch(() => {
        if (!cancelled) setBookmarks(new Set());
      });

    return () => {
      cancelled = true;
    };
  }, [currentUser]);

  const navigate = (page: Page, jobId?: string) => {
    if (page !== currentPage) {
      setPageHistory(prev => [...prev, currentPage]);
    }
    if (jobId) {
      setCurrentJobId(jobId);
      saveCurrentJobId(jobId);
    }
    saveCurrentPage(page);
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleBack = () => {
    const previousPage = pageHistory[pageHistory.length - 1] || 'main';
    setPageHistory(prev => prev.slice(0, -1));
    saveCurrentPage(previousPage);
    setCurrentPage(previousPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleRegister = async (formData: RegisterFormData, passwordConfirm: string) => {
    await registerMember(formData, passwordConfirm);
    setRegisteredUser(formData);
    localStorage.setItem(registeredUserStorageKey, JSON.stringify(formData));
  };

  const handleResetPassword = (newPassword: string) => {
    setRegisteredUser(prev => {
      if (!prev) return prev;

      const updatedUser = { ...prev, password: newPassword };
      localStorage.setItem(registeredUserStorageKey, JSON.stringify(updatedUser));
      return updatedUser;
    });
  };

  const handleLogin = async (loginId: string, password: string) => {
    const member = await loginMember(loginId, password);
    setCurrentUser(toCurrentUser(member));
  };

  const handleLogout = async () => {
    try {
      await logoutMember();
    } finally {
      clearAutoLoginSession();
      setCurrentUser(null);
      setBookmarks(new Set());
      setPageHistory([]);
      saveCurrentPage('main');
      setCurrentPage('main');
    }
  };

  const handleBookmark = async (id: string) => {
    if (!currentUser) {
      alert('관심 공고를 저장하려면 로그인해 주세요.');
      navigate('login');
      return;
    }

    const normalizedJobId = normalizeJobId(id);
    const bookmarkId = `job-${normalizedJobId}`;
    const job = mockJobs.find(item => item.id === normalizedJobId);
    if (!job) {
      alert('저장할 공고를 찾을 수 없습니다.');
      return;
    }

    try {
      if (bookmarks.has(bookmarkId)) {
        await deleteInterestJob(job.company, job.title);
      } else {
        await saveInterestJob(job);
      }

      setBookmarks(prev => {
        const next = new Set(prev);
        if (next.has(bookmarkId)) next.delete(bookmarkId);
        else next.add(bookmarkId);
        return next;
      });
    } catch (error) {
      alert(error instanceof Error ? error.message : '관심 공고 저장 중 오류가 발생했습니다.');
    }
  };

  const handleApplyJob = async (id: string, formData: ApplicationFormData) => {
    const normalizedJobId = normalizeJobId(id);
    const job = mockJobs.find(item => item.id === normalizedJobId);
    if (!job) throw new Error('지원할 공고를 찾을 수 없습니다.');
    await saveJobApplication(job, {
      applicantName: formData.name,
      phone: formData.phone,
      email: formData.email,
      employmentType: formData.employmentType,
    });
    setAppliedJobIds(prev => {
      const next = new Set(prev);
      next.add(normalizedJobId);
      saveAppliedJobIds(next);
      return next;
    });
    setApplicationForms(prev => {
      const next = {
        ...prev,
        [normalizedJobId]: {
          ...formData,
          submittedAt: formData.submittedAt || new Date().toISOString(),
          updatedAt: formData.updatedAt || new Date().toISOString(),
        },
      };
      saveApplicationForms(next);
      return next;
    });
  };

  const handleUpdateApplication = (id: string, formData: ApplicationFormData) => {
    const normalizedJobId = normalizeJobId(id);
    setApplicationForms(prev => {
      const next = {
        ...prev,
        [normalizedJobId]: {
          ...formData,
          submittedAt: prev[normalizedJobId]?.submittedAt || formData.submittedAt || new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        },
      };
      saveApplicationForms(next);
      return next;
    });
  };

  const handleDeleteApplication = (id: string) => {
    const normalizedJobId = normalizeJobId(id);
    setAppliedJobIds(prev => {
      const next = new Set(prev);
      next.delete(normalizedJobId);
      saveAppliedJobIds(next);
      return next;
    });
    setApplicationForms(prev => {
      const next = { ...prev };
      delete next[normalizedJobId];
      saveApplicationForms(next);
      return next;
    });
  };

  const handleRequireLoginForApply = (id: string) => {
    const normalizedJobId = id.replace(/^job-/, '');
    setPendingApplicationJobId(normalizedJobId);
    savePendingApplicationJobId(normalizedJobId);
    navigate('login', `job-${normalizedJobId}`);
  };

  const handleLoginSuccess = () => {
    if (pendingApplicationJobId) {
      const normalizedJobId = pendingApplicationJobId.replace(/^job-/, '');
      setPendingApplicationJobId(null);
      clearPendingApplicationJobId();
      navigate('job-detail', `job-${normalizedJobId}`);
      return;
    }

    navigate('main');
  };

  const handleHeaderSearch = (query: string) => {
    setHeaderSearchQuery(query.trim());
    navigate('jobs');
  };

  const showNavFooter = !noNavPages.includes(currentPage);

  const renderPage = () => {
    switch (currentPage) {
      case 'main':
        return <MainPage navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} onSearch={handleHeaderSearch} />;

      case 'login':
        return <LoginPage navigate={navigate} onLogin={handleLogin} onLoginSuccess={handleLoginSuccess} registeredUser={registeredUser} onBack={handleBack} onResetPassword={handleResetPassword} />;

      case 'register':
        return <RegisterPage navigate={navigate} onRegister={handleRegister} onBack={handleBack} />;

      case 'job-detail': {
        const normalizedJobId = normalizeJobId(currentJobId || '1');
        return (
          <JobDetailPage
            jobId={currentJobId}
            currentUser={currentUser}
            navigate={navigate}
            bookmarked={bookmarks.has(`job-${normalizedJobId}`)}
            applied={appliedJobIds.has(normalizedJobId)}
            onBookmark={handleBookmark}
            onApply={handleApplyJob}
            onRequireLoginForApply={handleRequireLoginForApply}
          />
        );
      }

      case 'user-dashboard':
      case 'corporate':
        return (
          <ProfilePage
            currentUser={currentUser}
            navigate={navigate}
            bookmarks={bookmarks}
            appliedJobIds={appliedJobIds}
            applicationForms={applicationForms}
            onBookmark={handleBookmark}
            onUpdateApplication={handleUpdateApplication}
            onDeleteApplication={handleDeleteApplication}
          />
        );

      case 'jobs':
        return <JobsPage navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} initialQuery={headerSearchQuery} />;

      case 'ai-recommend':
        return <JobsPage mode="recommended" navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} initialQuery={headerSearchQuery} />;

      case 'saved':
        return <JobsPage mode="saved" navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} initialQuery={headerSearchQuery} />;

      case 'support':
        return <SupportPage />;

      case 'community':
        return <CommunityPage currentUser={currentUser} navigate={navigate} />;

      case 'admin':
        return currentUser?.role === 'admin' ? <AdminPage /> : <LoginPage navigate={navigate} onLogin={handleLogin} onLoginSuccess={handleLoginSuccess} registeredUser={registeredUser} onBack={handleBack} onResetPassword={handleResetPassword} />;

      default:
        return <MainPage navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} onSearch={handleHeaderSearch} />;
    }
  };

  return (
    <div className="flex flex-col min-h-screen">
      {showNavFooter && (
        <Navbar
          currentPage={currentPage}
          navigate={navigate}
          currentUser={currentUser}
          onLogout={handleLogout}
          onSearch={handleHeaderSearch}
        />
      )}

      <main className="flex-1">
        {renderPage()}
      </main>

      {showNavFooter && currentPage !== 'user-dashboard' && currentPage !== 'corporate' && currentPage !== 'admin' && (
        <Footer navigate={navigate} />
      )}
    </div>
  );
}
