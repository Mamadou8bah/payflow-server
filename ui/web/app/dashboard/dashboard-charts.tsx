"use client";

import { useId } from "react";

export type ChartPoint = { label: string; value: number };

const CHART_COLORS = ["#123c91", "#f97316", "#10b981", "#8b5cf6", "#ef4444", "#0ea5e9"];
const PAYFLOW_LOGO = "https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png";

function ChartFrame({ height, children }: { height: number; children: React.ReactNode }) {
  return (
    <div className="w-full max-w-full overflow-hidden" style={{ height }}>
      {children}
    </div>
  );
}

export function LineAreaChart({
  data,
  height = 220,
  color = "#123c91",
  fillColor = "#dbeafe",
  valuePrefix = "",
  valueSuffix = "",
}: {
  data: ChartPoint[];
  height?: number;
  color?: string;
  fillColor?: string;
  valuePrefix?: string;
  valueSuffix?: string;
}) {
  const clipId = useId().replace(/:/g, "");
  const width = 560;
  const pad = { top: 20, right: 16, bottom: 36, left: 52 };
  const innerW = width - pad.left - pad.right;
  const innerH = height - pad.top - pad.bottom;
  const max = Math.max(...data.map((d) => d.value), 1);

  const points = data.map((d, i) => {
    const x = pad.left + (i / Math.max(data.length - 1, 1)) * innerW;
    const y = pad.top + innerH - (d.value / max) * innerH;
    return { x, y, ...d };
  });

  const linePath = points.map((p, i) => `${i === 0 ? "M" : "L"} ${p.x} ${p.y}`).join(" ");
  const areaPath = `${linePath} L ${points[points.length - 1]?.x ?? pad.left} ${pad.top + innerH} L ${points[0]?.x ?? pad.left} ${pad.top + innerH} Z`;

  const yTicks = [0, 0.5, 1].map((t) => ({
    y: pad.top + innerH - t * innerH,
    label: Math.round(t * max).toLocaleString(),
  }));

  return (
    <ChartFrame height={height}>
      <svg viewBox={`0 0 ${width} ${height}`} className="h-full w-full" preserveAspectRatio="xMidYMid meet">
        <clipPath id={clipId}>
          <rect x={pad.left} y={pad.top} width={innerW} height={innerH} />
        </clipPath>
        {yTicks.map((tick) => (
          <g key={tick.label}>
            <line x1={pad.left} y1={tick.y} x2={width - pad.right} y2={tick.y} stroke="#e2e8f0" strokeWidth="1" />
            <text x={pad.left - 8} y={tick.y + 4} textAnchor="end" fontSize="11" fill="#64748b">
              {valuePrefix}{tick.label}{valueSuffix}
            </text>
          </g>
        ))}
        <g clipPath={`url(#${clipId})`}>
          <path d={areaPath} fill={fillColor} />
          <path d={linePath} fill="none" stroke={color} strokeWidth="2.5" strokeLinejoin="round" strokeLinecap="round" />
          {points.map((p) => (
            <circle key={p.label} cx={p.x} cy={p.y} r="4" fill={color}>
              <title>{`${p.label}: ${valuePrefix}${p.value.toLocaleString()}${valueSuffix}`}</title>
            </circle>
          ))}
        </g>
        {points.map((p) => (
          <text key={`${p.label}-x`} x={p.x} y={height - 10} textAnchor="middle" fontSize="11" fill="#64748b">
            {p.label}
          </text>
        ))}
      </svg>
    </ChartFrame>
  );
}

export function BarChart({
  data,
  height = 200,
  valuePrefix = "",
}: {
  data: { label: string; value: number; color?: string }[];
  height?: number;
  valuePrefix?: string;
}) {
  const width = 560;
  const pad = { top: 28, right: 16, bottom: 36, left: 16 };
  const innerW = width - pad.left - pad.right;
  const innerH = height - pad.top - pad.bottom;
  const max = Math.max(...data.map((d) => d.value), 1);
  const barGap = 12;
  const barW = Math.max(8, (innerW - barGap * (data.length - 1)) / data.length);

  return (
    <ChartFrame height={height}>
      <svg viewBox={`0 0 ${width} ${height}`} className="h-full w-full" preserveAspectRatio="xMidYMid meet">
        {data.map((d, i) => {
          const barH = Math.max(4, (d.value / max) * innerH);
          const x = pad.left + i * (barW + barGap);
          const y = pad.top + innerH - barH;
          const color = d.color ?? CHART_COLORS[i % CHART_COLORS.length];
          const labelY = Math.max(pad.top + 12, y - 6);
          return (
            <g key={d.label}>
              <rect x={x} y={y} width={barW} height={barH} rx="6" fill={color} />
              <text x={x + barW / 2} y={labelY} textAnchor="middle" fontSize="10" fill="#334155" fontWeight="600">
                {valuePrefix}{d.value.toLocaleString()}
              </text>
              <text x={x + barW / 2} y={height - 10} textAnchor="middle" fontSize="11" fill="#64748b">
                {d.label}
              </text>
              <title>{`${d.label}: ${valuePrefix}${d.value.toLocaleString()}`}</title>
            </g>
          );
        })}
      </svg>
    </ChartFrame>
  );
}

export function DonutChart({
  data,
  size = 160,
}: {
  data: { label: string; value: number; color: string }[];
  size?: number;
}) {
  const total = data.reduce((s, d) => s + d.value, 0) || 1;
  const cx = size / 2;
  const cy = size / 2;
  const radius = size * 0.36;
  const stroke = size * 0.13;
  let angle = -90;

  const arcs = data.map((d) => {
    const sweep = (d.value / total) * 360;
    const start = angle;
    angle += sweep;
    const end = angle;
    const startRad = (start * Math.PI) / 180;
    const endRad = (end * Math.PI) / 180;
    const x1 = cx + radius * Math.cos(startRad);
    const y1 = cy + radius * Math.sin(startRad);
    const x2 = cx + radius * Math.cos(endRad);
    const y2 = cy + radius * Math.sin(endRad);
    const large = sweep > 180 ? 1 : 0;
    const path = `M ${x1} ${y1} A ${radius} ${radius} 0 ${large} 1 ${x2} ${y2}`;
    return { ...d, path, pct: Math.round((d.value / total) * 100) };
  });

  return (
    <div className="flex min-w-0 flex-col items-center gap-4 sm:flex-row sm:items-center sm:justify-between">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} className="mx-auto shrink-0 sm:mx-0">
        {arcs.map((a) => (
          <path key={a.label} d={a.path} fill="none" stroke={a.color} strokeWidth={stroke} strokeLinecap="butt">
            <title>{`${a.label}: ${a.pct}%`}</title>
          </path>
        ))}
        <text x={cx} y={cy - 4} textAnchor="middle" fontSize="14" fontWeight="700" fill="#0f172a">
          {total.toLocaleString()}
        </text>
        <text x={cx} y={cy + 12} textAnchor="middle" fontSize="9" fill="#64748b">
          Total
        </text>
      </svg>
      <ul className="w-full min-w-0 space-y-2 sm:max-w-[12rem]">
        {arcs.map((a) => (
          <li key={a.label} className="flex items-center justify-between gap-2 text-sm">
            <span className="flex min-w-0 items-center gap-2 text-slate-600">
              <span className="h-2.5 w-2.5 shrink-0 rounded-full" style={{ backgroundColor: a.color }} />
              <span className="truncate">{a.label}</span>
            </span>
            <span className="shrink-0 font-bold text-slate-900">{a.pct}%</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

export function Sparkline({ values, color = "#f97316" }: { values: number[]; color?: string }) {
  const max = Math.max(...values, 1);
  const w = 80;
  const h = 28;
  const coords = values
    .map((v, i) => {
      const x = (i / Math.max(values.length - 1, 1)) * w;
      const y = h - (v / max) * h;
      return `${x},${y}`;
    })
    .join(" ");
  return (
    <svg viewBox={`0 0 ${w} ${h}`} className="h-7 w-20 shrink-0 overflow-hidden">
      <polyline fill="none" stroke={color} strokeWidth="2" points={coords} strokeLinejoin="round" strokeLinecap="round" />
    </svg>
  );
}

export function DashboardHeroCard({
  label,
  value,
  hint,
  footerLeft,
  footerRight,
  progress = 88,
  progressLabel,
}: {
  label: string;
  value: string;
  hint?: string;
  footerLeft: string;
  footerRight?: string;
  progress?: number;
  progressLabel?: string;
}) {
  return (
    <section className="overflow-hidden rounded-[2rem] bg-gradient-to-br from-[#123c91] via-[#1649a8] to-[#1e5bb8] p-6 text-white shadow-lg">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-white">{label}</p>
          <p className="mt-2 text-4xl font-black tracking-tight">{value}</p>
          {hint ? <p className="mt-2 text-sm text-blue-200">{hint}</p> : null}
        </div>
        <img src={PAYFLOW_LOGO} alt="Payflow" className="h-14 w-14 shrink-0 object-contain" />
      </div>
      <div className="mt-6 flex flex-wrap items-center justify-between gap-2 text-sm">
        <span className="font-bold text-white">{footerLeft}</span>
        {footerRight ? <span className="text-blue-200">{footerRight}</span> : null}
      </div>
      <div className="mt-3 h-2 overflow-hidden rounded-full bg-blue-950">
        <div className="h-full rounded-full bg-orange-500 transition-all" style={{ width: `${Math.min(100, Math.max(0, progress))}%` }} />
      </div>
      {progressLabel ? <p className="mt-2 text-xs text-blue-200">{progressLabel}</p> : null}
    </section>
  );
}
