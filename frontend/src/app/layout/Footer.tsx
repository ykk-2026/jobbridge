import { Mail, MapPin, Phone } from 'lucide-react';
import { BrandLogo } from '@/app/components/BrandLogo';
import type { Page } from '@/app/types';

interface FooterProps {
  navigate: (page: Page) => void;
}

export function Footer({ navigate }: FooterProps) {
  return (
    <footer className="mt-12 border-t border-border bg-white">
      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-8 px-4 py-10 sm:px-6 md:grid-cols-3 lg:px-8">
        <div>
          <div className="mb-3 flex items-center gap-2 font-bold">
            <BrandLogo compact />
          </div>
          <p className="text-sm text-muted-foreground">구직자와 기업을 위한 장애친화 채용 매칭 서비스입니다.</p>
        </div>
        <div>
          <h4 className="mb-3 font-semibold">바로가기</h4>
          <div className="space-y-2 text-sm">
            <button type="button" onClick={() => navigate('jobs')} className="block text-muted-foreground hover:text-foreground">채용정보</button>
            <button type="button" onClick={() => navigate('saved')} className="block text-muted-foreground hover:text-foreground">저장한 공고</button>
            <button type="button" onClick={() => navigate('user-dashboard')} className="block text-muted-foreground hover:text-foreground">지원관리</button>
          </div>
        </div>
        <div>
          <h4 className="mb-3 font-semibold">연락처</h4>
          <div className="space-y-2 text-sm text-muted-foreground">
            <p className="flex items-center gap-2"><Phone size={14} /> 1588-0000</p>
            <p className="flex items-center gap-2"><Mail size={14} /> help@ileeum.ai</p>
            <p className="flex items-center gap-2"><MapPin size={14} /> 서울, 대한민국</p>
          </div>
        </div>
      </div>
    </footer>
  );
}
