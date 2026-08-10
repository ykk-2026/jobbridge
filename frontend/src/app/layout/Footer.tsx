import { Briefcase, Mail, MapPin, Phone } from 'lucide-react';
import type { Page } from '@/app/types';

interface FooterProps {
  navigate: (page: Page) => void;
}

export function Footer({ navigate }: FooterProps) {
  return (
    <footer className="bg-white border-t border-border mt-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 grid grid-cols-1 md:grid-cols-3 gap-8">
        <div>
          <div className="flex items-center gap-2 font-bold mb-3">
            <span className="w-9 h-9 rounded-lg bg-primary text-white flex items-center justify-center">
              <Briefcase size={18} />
            </span>
            JobBridgeAI
          </div>
          <p className="text-sm text-muted-foreground">구직자와 기업을 위한 장애친화 채용 매칭 서비스입니다.</p>
        </div>
        <div>
          <h4 className="font-semibold mb-3">바로가기</h4>
          <div className="space-y-2 text-sm">
            <button onClick={() => navigate('jobs')} className="block text-muted-foreground hover:text-foreground">채용정보</button>
            <button onClick={() => navigate('ai-recommend')} className="block text-muted-foreground hover:text-foreground">맞춤 추천</button>
            <button onClick={() => navigate('applications')} className="block text-muted-foreground hover:text-foreground">지원 현황</button>
          </div>
        </div>
        <div>
          <h4 className="font-semibold mb-3">고객지원</h4>
          <div className="space-y-2 text-sm text-muted-foreground">
            <p className="flex items-center gap-2"><Phone size={14} /> 1588-0000</p>
            <p className="flex items-center gap-2"><Mail size={14} /> help@jobbridge.ai</p>
            <p className="flex items-center gap-2"><MapPin size={14} /> 서울, 대한민국</p>
          </div>
        </div>
      </div>
    </footer>
  );
}
