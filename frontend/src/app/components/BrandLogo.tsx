import ileeumSymbol from '@/app/assets/ileeum-symbol.png';

interface BrandLogoProps {
  compact?: boolean;
}

export function BrandLogo({ compact = false }: BrandLogoProps) {
  const iconBoxClass = compact ? 'h-12 w-12 rounded-xl' : 'h-14 w-14 rounded-xl';
  const symbolSizeClass = compact ? 'h-9 w-14' : 'h-10 w-16';
  const taglineClass = compact
    ? 'text-[10px] font-bold leading-[12px] tracking-[-0.02em]'
    : 'text-[14px] font-bold leading-[18px] tracking-[-0.02em]';
  const wordmarkClass = compact
    ? 'text-[30px] font-extrabold leading-[30px] tracking-[-0.05em]'
    : 'text-[46px] font-extrabold leading-[48px] tracking-[-0.05em]';

  return (
    <span className="inline-flex items-center gap-3 text-left">
      <span className={`${iconBoxClass} flex shrink-0 items-center justify-center overflow-hidden bg-[#1677F2] shadow-sm`}>
        <img src={ileeumSymbol} alt="" aria-hidden="true" className={`${symbolSizeClass} max-w-none object-contain`} />
      </span>
      <span className="grid">
        <span className={`${taglineClass} text-[#12346B]`}>장애인 맞춤 일자리 플랫폼</span>
        <span className={wordmarkClass}>
          <span className="text-[#12346B]">일</span>
          <span className="text-[#1677F2]">이음</span>
        </span>
      </span>
    </span>
  );
}
