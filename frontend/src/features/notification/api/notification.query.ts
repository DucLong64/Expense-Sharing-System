import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as notificationApi from '@/features/notification/api/notification.api'
import type { NotificationFilter } from '@/features/notification/types/notification.types'

export const notificationKeys = {
  all: (filter?: NotificationFilter) => ['notifications', filter ?? {}] as const,
  unreadCount: ['notifications', 'unread-count'] as const,
}

export function useMyNotifications(filter?: NotificationFilter) {
  return useQuery({
    queryKey: notificationKeys.all(filter),
    queryFn: () => notificationApi.getMyNotifications(filter),
  })
}

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: notificationKeys.unreadCount,
    queryFn: notificationApi.getUnreadNotificationCount,
    refetchInterval: 30_000,
  })
}

export function useMarkNotificationRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: notificationApi.markNotificationRead,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}

export function useMarkAllNotificationsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: notificationApi.markAllNotificationsRead,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}
