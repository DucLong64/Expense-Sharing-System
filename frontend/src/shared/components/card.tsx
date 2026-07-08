import type { ReactNode } from 'react'

interface CardProps {
  children: ReactNode
  className?: string
  title?: string
  description?: string
  action?: ReactNode
}

export function Card({ children, className = '', title, description, action }: CardProps) {
  return (
    <div
      className={`rounded-2xl border border-slate-200/80 bg-white p-6 shadow-[var(--shadow-soft)] ${className}`}
    >
      {title || description || action ? (
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            {title ? <h3 className="text-base font-semibold text-slate-900">{title}</h3> : null}
            {description ? <p className="mt-1 text-sm text-slate-500">{description}</p> : null}
          </div>
          {action}
        </div>
      ) : null}
      {children}
    </div>
  )
}
