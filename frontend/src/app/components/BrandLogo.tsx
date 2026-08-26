import ileeumSymbol from '@/app/assets/ileeum-symbol.png';

interface BrandLogoProps {
  compact?: boolean;
}

export function BrandLogo({ compact = false }: BrandLogoProps) {
  const iconClass = compact ? 'h-9 w-9' : 'h-12 w-12';
  const imageClass = compact ? 'h-7 w-10' : 'h-9 w-13';
  const taglineClass = compact ? 'text-[10px] leading-[11px]' : 'text-[14px] leading-4';
  const wordmarkClass = compact ? 'text-[34px] leading-[32px]' : 'text-[52px] leading-[48px]';

  return (
    <span className="inline-flex items-center gap-2.5 text-left">
      <span
        className={`flex shrink-0 items-center justify-center overflow-hidden rounded-[8px] bg-[#1677F2] shadow-sm ${iconClass}`}
      >
        <img
          src={ileeumSymbol}
          alt=""
          aria-hidden="true"
          className={`${imageClass} max-w-none object-contain`}
        />
      </span>
      <span className="grid content-center">
        <span className={`${taglineClass} whitespace-nowrap font-black text-[#12346B]`}>
          장애인 맞춤 일자리 플랫폼
        </span>
        <span className={`${wordmarkClass} whitespace-nowrap font-black tracking-normal`}>
          <span className="text-[#0B2154]">일</span>
          <span className="text-[#1266F1]">이음</span>
        </span>
      </span>
    </span>
  );
}
