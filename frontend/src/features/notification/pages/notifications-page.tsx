import { useState } from 'react'
import {
  useMarkAllNotificationsRead,
  useMarkNotificationRead,
  useMyNotifications,
} from '@/features/notification/api/notification.query'
import { NotificationListItem } from '@/features/notification/components/notification-list-item'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'

export function NotificationsPage() {
  const [unreadOnly, setUnreadOnly] = useState(false)
  const { data: notifications = [], isLoading, error, refetch } = useMyNotifications(
    unreadOnly ? { unreadOnly: true } : undefined,
  )
  const markReadMutation = useMarkNotificationRead()
  const markAllMutation = useMarkAllNotificationsRead()

  function handleMarkRead(notificationId: string) {
    void markReadMutation.mutateAsync(notificationId)
  }

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
            <NotificationListItem
              key={notification.id}
              notification={notification}
              onMarkRead={handleMarkRead}
            />
          ))}
        </div>
      ) : null}
    </AppShell>
  )
}
