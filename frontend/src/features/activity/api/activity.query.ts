import { useQuery } from '@tanstack/react-query'
import * as activityApi from '@/features/activity/api/activity.api'
import type { ActivityFilter, ActivityType } from '@/features/activity/types/activity.types'

export const activityKeys = {
  house: (houseId: string, activityType?: ActivityType) =>
    ['activities', 'house', houseId, activityType ?? 'all'] as const,
  my: (filter?: ActivityFilter) => ['activities', 'my', filter ?? {}] as const,
}

export function useHouseActivities(houseId: string, activityType?: ActivityType) {
  return useQuery({
    queryKey: activityKeys.house(houseId, activityType),
    queryFn: () => activityApi.getHouseActivities(houseId, activityType),
    enabled: Boolean(houseId),
  })
}

export function useMyActivities(filter?: ActivityFilter) {
  return useQuery({
    queryKey: activityKeys.my(filter),
    queryFn: () => activityApi.getMyActivities(filter),
  })
}
