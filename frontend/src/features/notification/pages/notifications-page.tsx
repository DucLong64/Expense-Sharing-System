import { Link } from 'react-router-dom'
import {
  useMarkAllNotificationsRead,
  useMarkNotificationRead,
  useMyNotifications,
} from '@/features/notification/api/notification.query'
import type { NotificationResponse } from '@/features/notification/types/notification.types'
import { notificationTypeLabels } from '@/features/notification/utils/notification-labels'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { displayUsername, formatDateTime } from '@/shared/utils/format'
import { useState } from 'react'

function getNotificationLink(notification: NotificationResponse): string {
  return `/houses/${notification.houseId}`
}

interface NotificationItemProps {
  notification: NotificationResponse
}

function NotificationItem({ notification }: NotificationItemProps) {
  const markReadMutation = useMarkNotificationRead()

  async function handleClick() {
    if (!notification.read) {
      await markReadMutation.mutateAsync(notification.id)
    }
  }

  return (
    <Link
      to={getNotificationLink(notification)}
      onClick={() => void handleClick()}
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

export function NotificationsPage() {
  const [unreadOnly, setUnreadOnly] = useState(false)
  const { data: notifications = [], isLoading, error, refetch } = useMyNotifications(
    unreadOnly ? { unreadOnly: true } : undefined,
  )
  const markAllMutation = useMarkAllNotificationsRead()

  return (
    <AppShell title="Thông báo" subtitle="Cập nhật mới nhất từ các nhóm bạn tham gia.">
      <div className="mb-6 flex flex-wrap items-center gap-3">
        <label className="flex items-center gap-2 text-sm text-slate-600">
          <input
            type="checkbox"
            checked={unreadOnly}
            onChange={(event) => setUnreadOnly(event.target.checked)}
            className="h-4 w-4 rounded border-slate-300 text-emerald-600"
          />
          Chỉ hiện chưa đọc
        </label>
        <Button
          variant="secondary"
          className="w-auto"
          loading={markAllMutation.isPending}
          onClick={() => void markAllMutation.mutateAsync()}
        >
          Đánh dấu tất cả đã đọc
        </Button>
      </div>

      {isLoading ? <LoadingState message="Đang tải thông báo..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải thông báo." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}

      {!isLoading && !error && notifications.length === 0 ? (
        <p className="text-sm text-slate-500">Không có thông báo nào.</p>
      ) : null}

      {!isLoading && !error ? (
        <div className="space-y-3">
          {notifications.map((notification) => (
            <NotificationItem key={notification.id} notification={notification} />
          ))}
        </div>
      ) : null}
    </AppShell>
  )
}
