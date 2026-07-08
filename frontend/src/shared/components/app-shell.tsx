import { Link, useNavigate } from 'react-router-dom'
import type { ReactNode } from 'react'
import { NotificationBell } from '@/features/notification/components/notification-bell'
import { useCurrentUser } from '@/features/auth/api/auth.query'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { WalletIcon } from '@/shared/components/icons'
import { UserMenu } from '@/shared/components/dropdown-menu'

interface AppShellProps {
  title: string
  subtitle?: string
  backTo?: string
  children: ReactNode
}

export function AppShell({ title, subtitle, backTo, children }: AppShellProps) {
  const navigate = useNavigate()
  const { logout } = useAuth()
  const { data: currentUser } = useCurrentUser()
  const username = currentUser?.username ?? 'Tài khoản'

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-40 border-b border-slate-200/80 bg-white/80 backdrop-blur-xl">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
          <Link to="/" className="flex items-center gap-2.5">
            <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-600 text-white shadow-sm">
              <WalletIcon className="h-5 w-5" />
            </span>
            <span className="hidden text-sm font-bold text-slate-900 sm:block">Chia chi</span>
          </Link>
          <div className="flex items-center gap-2">
            <NotificationBell />
            <UserMenu
              username={username}
              items={[
                { label: 'Tài khoản', onClick: () => navigate('/profile') },
                { label: 'Hoạt động', onClick: () => navigate('/activities') },
                { label: 'Đăng xuất', tone: 'danger', onClick: () => void logout() },
              ]}
            />
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
          {subtitle ? (
            <p className="mt-2 max-w-2xl text-sm leading-relaxed text-slate-500">{subtitle}</p>
          ) : null}
        </div>
        {children}
      </main>
    </div>
  )
}
