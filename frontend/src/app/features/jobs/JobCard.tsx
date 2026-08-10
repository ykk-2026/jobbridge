import { Bookmark, BookmarkCheck, Building2, ChevronRight, MapPin, Wifi } from 'lucide-react';
import type { Job, Page } from '@/app/types';

interface JobCardProps {
  job: Job;
  navigate: (page: Page, jobId?: string) => void;
  bookmarked: boolean;
  onBookmark: (id: string) => void;
}

export function JobCard({ job, navigate, bookmarked, onBookmark }: JobCardProps) {
  return (
    <article className="bg-white border border-border rounded-lg p-5 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <button className="flex items-center gap-3 text-left" onClick={() => navigate('job-detail', job.id)}>
          <div className="w-12 h-12 rounded-lg flex items-center justify-center text-white font-bold" style={{ backgroundColor: job.companyColor }}>
            {job.companyInitials}
          </div>
          <div>
            <h3 className="font-semibold text-foreground">{job.title}</h3>
            <p className="text-sm text-muted-foreground flex items-center gap-1 mt-1">
              <Building2 size={14} />
              {job.company}
            </p>
          </div>
        </button>
        <button
          aria-label={bookmarked ? '북마크 제거' : '북마크 추가'}
          onClick={() => onBookmark(job.id)}
          className="w-9 h-9 rounded-lg border border-border flex items-center justify-center text-primary hover:bg-primary/5"
        >
          {bookmarked ? <BookmarkCheck size={18} /> : <Bookmark size={18} />}
        </button>
      </div>

      <div className="flex flex-wrap gap-2 mt-4 text-xs text-muted-foreground">
        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-muted">
          <MapPin size={13} />
          {job.location}
        </span>
        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-muted">
          <Wifi size={13} />
          {job.workType}
        </span>
        <span className="px-2.5 py-1 rounded-full bg-muted">{job.salary}</span>
      </div>

      <p className="text-sm text-muted-foreground mt-4 line-clamp-2">{job.description}</p>

      <div className="flex items-center justify-between mt-5">
        <span className="text-sm font-semibold text-primary">AI 매칭 {job.aiScore}%</span>
        <button onClick={() => navigate('job-detail', job.id)} className="inline-flex items-center gap-1 text-sm font-medium text-primary">
          상세 보기 <ChevronRight size={14} />
        </button>
      </div>
    </article>
  );
}
