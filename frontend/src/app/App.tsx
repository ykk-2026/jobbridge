import { useEffect, useState } from 'react';
import type { CurrentUser, Page, RegisterFormData, UserRole } from '@/app/types';
import {
  getCurrentMember,
  loginMember,
  logoutMember,
  registerMember,
  type LoginMember,
} from '@/app/api/memberApi';
import { Navbar } from '@/app/layout/Navbar';
import { Footer } from '@/app/layout/Footer';
import { MainPage } from '@/app/pages/home/MainPage';
import { LoginPage } from '@/app/pages/auth/LoginPage';
import { RegisterPage } from '@/app/pages/auth/RegisterPage';
import { AdminPage } from '@/app/pages/admin/AdminPage';
import { ProfilePage } from '@/app/pages/profile/ProfilePage';

// MARKER-MAKE-KIT-INVOKED
const noNavPages: Page[] = ['login', 'register'];

const toCurrentUser = (member: LoginMember): CurrentUser => {
  const role: UserRole = member.role === 'ADMIN'
    ? 'admin'
    : member.role === 'CORPORATE' || member.role === 'EMPLOYER'
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

export default function App() {
  const [currentPage, setCurrentPage] = useState<Page>('main');
  const [pageHistory, setPageHistory] = useState<Page[]>([]);
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [registeredUser, setRegisteredUser] = useState<RegisterFormData | null>(null);
  const [bookmarks, setBookmarks] = useState<Set<string>>(new Set(['1', '2']));

  useEffect(() => {
    getCurrentMember()
      .then(member => setCurrentUser(member ? toCurrentUser(member) : null))
      .catch(() => setCurrentUser(null));
  }, []);

  const navigate = (page: Page) => {
    if (page !== currentPage) {
      setPageHistory(prev => [...prev, currentPage]);
    }
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleBack = () => {
    const previousPage = pageHistory[pageHistory.length - 1] || 'main';
    setPageHistory(prev => prev.slice(0, -1));
    setCurrentPage(previousPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleRegister = async (formData: RegisterFormData, passwordConfirm: string) => {
    await registerMember(formData, passwordConfirm);
    setRegisteredUser(formData);
  };

  const handleResetPassword = (newPassword: string) => {
    setRegisteredUser(prev => {
      if (!prev) return prev;

      const updatedUser = { ...prev, password: newPassword };
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
      setCurrentUser(null);
      setPageHistory([]);
      setCurrentPage('main');
    }
  };

  const handleBookmark = (id: string) => {
    setBookmarks(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const showNavFooter = !noNavPages.includes(currentPage);

  const renderPage = () => {
    switch (currentPage) {
      case 'main':
        return <MainPage navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} />;

      case 'login':
        return <LoginPage navigate={navigate} onLogin={handleLogin} registeredUser={registeredUser} onBack={handleBack} onResetPassword={handleResetPassword} />;

      case 'register':
        return <RegisterPage navigate={navigate} onRegister={handleRegister} onBack={handleBack} />;

      case 'admin':
        return <AdminPage />;

      case 'user-dashboard':
      case 'corporate':
        return <ProfilePage currentUser={currentUser} />;

      default:
        return <MainPage navigate={navigate} bookmarks={bookmarks} onBookmark={handleBookmark} />;
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
