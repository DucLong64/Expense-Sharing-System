import { Link } from 'react-router-dom'
import type { NotificationResponse } from '@/features/notification/types/notification.types'
import { notificationTypeLabels } from '@/features/notification/utils/notification-labels'
import { displayUsername, formatDateTime } from '@/shared/utils/format'

interface NotificationListItemProps {
  notification: NotificationResponse
  compact?: boolean
  onNavigate?: () => void
  onMarkRead: (notificationId: string) => void
}

function getNotificationLink(notification: NotificationResponse): string {
  return `/houses/${notification.houseId}`
}

export function NotificationListItem({
  notification,
  compact = false,
  onNavigate,
  onMarkRead,
}: NotificationListItemProps) {
  function handleClick() {
    if (!notification.read) {
      onMarkRead(notification.id)
    }
    onNavigate?.()
  }

  if (compact) {
    return (
      <Link
        to={getNotificationLink(notification)}
        onClick={handleClick}
        className={`flex gap-3 px-4 py-3 transition hover:bg-slate-50 ${
          notification.read ? 'bg-white' : 'bg-emerald-50/50'
        }`}
      >
        <span
          className={`mt-1.5 h-2.5 w-2.5 shrink-0 rounded-full ${
            notification.read ? 'bg-transparent' : 'bg-emerald-500'
          }`}
        />
        <div className="min-w-0 flex-1">
          <p className="text-sm leading-snug text-slate-900">{notification.message}</p>
          <p className="mt-1 text-xs text-slate-500">
            {notificationTypeLabels[notification.type]} ·{' '}
            {displayUsername(notification.actorUsername, notification.actorUserId)} ·{' '}
            {formatDateTime(notification.createdAt)}
          </p>
        </div>
      </Link>
    )
  }

  return (
    <Link
      to={getNotificationLink(notification)}
      onClick={handleClick}
      className={`block rounded-xl border p-4 transition hover:border-emerald-300 hover:bg-emerald-50/40 ${
        notification.read
          ? 'border-slate-200/80 bg-white'
          : 'border-emerald-200 bg-emerald-50/60'
      }`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 flex-1">
          <p className="text-sm font-medium text-slate-900">{notification.message}</p>
          <p className="mt-1 text-xs text-slate-500">
            {notificationTypeLabels[notification.type]} ·{' '}
            {displayUsername(notification.actorUsername, notification.actorUserId)} ·{' '}
            {formatDateTime(notification.createdAt)}
          </p>
        </div>
        {!notification.read ? (
          <span className="mt-1 h-2.5 w-2.5 shrink-0 rounded-full bg-emerald-500" />
        ) : null}
      </div>
    </Link>
  )
}
