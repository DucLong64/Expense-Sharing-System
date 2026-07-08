import type { ReactNode } from 'react'

interface StatCardProps {
  label: string
  value: string
  icon?: ReactNode
  tone?: 'default' | 'success' | 'info'
}

const toneStyles = {
  default: 'from-slate-50 to-white border-slate-200/80 text-slate-600',
  success: 'from-emerald-50 to-white border-emerald-200/60 text-emerald-700',
  info: 'from-sky-50 to-white border-sky-200/60 text-sky-700',
}

export function StatCard({ label, value, icon, tone = 'default' }: StatCardProps) {
  return (
    <div
      className={`rounded-2xl border bg-gradient-to-br p-5 shadow-[var(--shadow-soft)] ${toneStyles[tone]}`}
    >
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-medium">{label}</p>
          <p className="mt-2 text-2xl font-bold tracking-tight text-slate-900">{value}</p>
        </div>
        {icon ? (
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white/80 shadow-sm">
            {icon}
          </div>
        ) : null}
      </div>
    </div>
  )
}
