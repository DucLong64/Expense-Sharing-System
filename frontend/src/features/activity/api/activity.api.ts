import { apiRequest } from '@/shared/api/axios-client'
import type {
  ActivityFilter,
  ActivityLogResponse,
  ActivityType,
} from '@/features/activity/types/activity.types'

export function getHouseActivities(
  houseId: string,
  activityType?: ActivityType,
): Promise<ActivityLogResponse[]> {
  return apiRequest<ActivityLogResponse[]>({
    url: `/api/v1/houses/${houseId}/activities`,
    method: 'GET',
    params: activityType ? { activityType } : undefined,
  })
}

export function getMyActivities(filter?: ActivityFilter): Promise<ActivityLogResponse[]> {
  return apiRequest<ActivityLogResponse[]>({
    url: '/api/v1/users/me/activities',
    method: 'GET',
    params: filter,
  })
}
