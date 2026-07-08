import { Link } from 'react-router-dom'
import { useUnreadNotificationCount } from '@/features/notification/api/notification.query'

export function NotificationBell() {
  const { data } = useUnreadNotificationCount()
  const unreadCount = data?.count ?? 0

  return (
    <Link
      to="/notifications"
      className="relative rounded-lg px-3 py-1.5 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
    >
      Thông báo
      {unreadCount > 0 ? (
        <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
          {unreadCount > 99 ? '99+' : unreadCount}
        </span>
      ) : null}
    </Link>
  )
}
