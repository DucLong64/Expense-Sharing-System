import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { Button } from '@/shared/components/button'
import { WalletIcon } from '@/shared/components/icons'
import { useAuth } from '@/features/auth/hooks/use-auth'

interface AppShellProps {
  title: string
  subtitle?: string
  backTo?: string
  children: ReactNode
}

export function AppShell({ title, subtitle, backTo, children }: AppShellProps) {
  const { logout } = useAuth()

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-40 border-b border-slate-200/80 bg-white/80 backdrop-blur-xl">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
          <Link to="/" className="flex items-center gap-2.5">
            <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-600 text-white shadow-sm">
              <WalletIcon className="h-5 w-5" />
            </span>
            <span className="hidden text-sm font-bold text-slate-900 sm:block">Expense Sharing</span>
          </Link>
          <div className="flex items-center gap-2">
            <Link
              to="/activities"
              className="rounded-lg px-3 py-1.5 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
            >
              Hoạt động
            </Link>
            <Button variant="ghost" size="sm" className="w-auto" onClick={() => void logout()}>
              Đăng xuất
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6 sm:py-10">
        <div className="mb-8">
          {backTo ? (
            <Link
              to={backTo}
              className="mb-3 inline-flex items-center gap-1 text-sm font-medium text-slate-500 transition hover:text-emerald-700"
            >
              ← Quay lại
            </Link>
          ) : null}
          <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">{title}</h1>
          {subtitle ? <p className="mt-2 max-w-2xl text-sm leading-relaxed text-slate-500">{subtitle}</p> : null}
        </div>
        {children}
      </main>
    </div>
  )
}
