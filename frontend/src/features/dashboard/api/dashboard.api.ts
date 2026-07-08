import { apiRequest } from '@/shared/api/axios-client'
import type { HouseDashboardResponse } from '@/features/dashboard/types/dashboard.types'

export function getHouseDashboard(houseId: string): Promise<HouseDashboardResponse> {
  return apiRequest<HouseDashboardResponse>({
    url: `/api/v1/houses/${houseId}/dashboard`,
    method: 'GET',
  })
}
