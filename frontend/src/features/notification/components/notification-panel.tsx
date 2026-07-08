import { Link } from 'react-router-dom'
import {
  useMarkAllNotificationsRead,
  useMarkNotificationRead,
  useMyNotifications,
} from '@/features/notification/api/notification.query'
import { NotificationListItem } from '@/features/notification/components/notification-list-item'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'

interface NotificationPanelProps {
  open: boolean
  onClose: () => void
}

export function NotificationPanel({ open, onClose }: NotificationPanelProps) {
  const { data: notifications = [], isLoading, error, refetch } = useMyNotifications(
    undefined,
    open,
  )
  const markReadMutation = useMarkNotificationRead()
  const markAllMutation = useMarkAllNotificationsRead()

  if (!open) {
    return null
  }

  function handleMarkRead(notificationId: string) {
    void markReadMutation.mutateAsync(notificationId)
  }

  return (
    <div
      role="dialog"
      aria-label="Thông báo"
      className="absolute right-0 top-full z-50 mt-2 w-[min(100vw-2rem,24rem)] overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-[var(--shadow-card)]"
    >
      <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3">
        <h2 className="text-base font-semibold text-slate-900">Thông báo</h2>
        <button
          type="button"
          className="text-xs font-medium text-emerald-700 transition hover:text-emerald-800 disabled:opacity-50"
          disabled={markAllMutation.isPending || notifications.every((item) => item.read)}
          onClick={() => void markAllMutation.mutateAsync()}
        >
          Đánh dấu tất cả đã đọc
        </button>
      </div>

      <div className="max-h-[min(28rem,70vh)] overflow-y-auto">
        {isLoading ? (
          <div className="p-4">
            <LoadingState message="Đang tải..." />
          </div>
        ) : null}

        {error ? (
          <div className="space-y-3 p-4">
            <ErrorMessage message="Không thể tải thông báo." />
            <Button variant="secondary" size="sm" className="w-auto" onClick={() => void refetch()}>
              Thử lại
            </Button>
          </div>
        ) : null}

        {!isLoading && !error && notifications.length === 0 ? (
          <p className="px-4 py-8 text-center text-sm text-slate-500">Không có thông báo nào.</p>
        ) : null}

        {!isLoading && !error ? (
          <div className="divide-y divide-slate-100">
            {notifications.map((notification) => (
              <NotificationListItem
                key={notification.id}
                notification={notification}
                compact
                onNavigate={onClose}
                onMarkRead={handleMarkRead}
              />
            ))}
          </div>
        ) : null}
      </div>

      <div className="border-t border-slate-100 bg-slate-50/80 px-4 py-2.5 text-center">
        <Link
          to="/notifications"
          onClick={onClose}
          className="text-xs font-medium text-emerald-700 transition hover:text-emerald-800"
        >
          Xem tất cả thông báo
        </Link>
      </div>
    </div>
  )
}
