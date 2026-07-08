import type { ReactNode } from 'react'
import { AppLogo } from '@/shared/components/app-logo'
import { ChartIcon, ScaleIcon, UsersIcon } from '@/shared/components/icons'

interface AuthLayoutProps {
  title: string
  subtitle: string
  children: ReactNode
  footer?: ReactNode
}

const features = [
  { icon: UsersIcon, text: 'Quản lý nhóm và thành viên' },
  { icon: ScaleIcon, text: 'Chia chi phí công bằng, minh bạch' },
  { icon: ChartIcon, text: 'Dashboard và báo cáo chi tiêu' },
]

export function AuthLayout({ title, subtitle, children, footer }: AuthLayoutProps) {
  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <div className="relative hidden overflow-hidden bg-gradient-to-br from-emerald-700 via-emerald-600 to-teal-700 lg:flex lg:flex-col lg:justify-between lg:p-12">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute -left-20 top-20 h-72 w-72 rounded-full bg-white/30 blur-3xl" />
          <div className="absolute bottom-10 right-10 h-96 w-96 rounded-full bg-teal-300/20 blur-3xl" />
        </div>
        <div className="relative">
          <AppLogo variant="long" className="brightness-0 invert" />
          <h2 className="mt-16 max-w-md text-3xl font-bold leading-tight text-white">
            Quản lý chi tiêu chung — đơn giản và rõ ràng
          </h2>
          <p className="mt-4 max-w-sm text-sm leading-relaxed text-emerald-100">
            Theo dõi khoản chi, công nợ và dashboard cho nhóm bạn bè, gia đình hoặc đồng nghiệp.
          </p>
        </div>
        <ul className="relative space-y-4">
          {features.map(({ icon: Icon, text }) => (
            <li key={text} className="flex items-center gap-3 text-sm text-emerald-50">
              <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-white/10">
                <Icon className="h-5 w-5" />
              </span>
              {text}
            </li>
          ))}
        </ul>
      </div>

      <div className="flex items-center justify-center px-4 py-10 sm:px-8">
        <div className="w-full max-w-md">
          <div className="mb-8 lg:hidden">
            <AppLogo variant="long" />
          </div>

          <div className="mb-8">
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">{title}</h1>
            <p className="mt-2 text-sm leading-relaxed text-slate-500">{subtitle}</p>
          </div>

          <div className="rounded-2xl border border-slate-200/80 bg-white p-6 shadow-[var(--shadow-card)]">
            {children}
          </div>

          {footer ? <div className="mt-6 text-center text-sm text-slate-500">{footer}</div> : null}
        </div>
      </div>
    </div>
  )
}
