import { apiRequest } from '@/shared/api/axios-client'
import type {
  NotificationFilter,
  NotificationResponse,
  UnreadNotificationCountResponse,
} from '@/features/notification/types/notification.types'

export function getMyNotifications(filter?: NotificationFilter): Promise<NotificationResponse[]> {
  return apiRequest<NotificationResponse[]>({
    url: '/api/v1/notifications',
    method: 'GET',
    params: filter,
  })
}

export function getUnreadNotificationCount(): Promise<UnreadNotificationCountResponse> {
  return apiRequest<UnreadNotificationCountResponse>({
    url: '/api/v1/notifications/unread-count',
    method: 'GET',
  })
}

export function markNotificationRead(notificationId: string): Promise<NotificationResponse> {
  return apiRequest<NotificationResponse>({
    url: `/api/v1/notifications/${notificationId}/read`,
    method: 'PATCH',
  })
}

export function markAllNotificationsRead(): Promise<void> {
  return apiRequest<void>({
    url: '/api/v1/notifications/read-all',
    method: 'PATCH',
  })
}
