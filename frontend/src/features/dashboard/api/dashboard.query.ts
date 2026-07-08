import { useQuery } from '@tanstack/react-query'
import * as dashboardApi from '@/features/dashboard/api/dashboard.api'

export const dashboardKeys = {
  detail: (houseId: string) => ['dashboard', houseId] as const,
}

export function useHouseDashboard(houseId: string) {
  return useQuery({
    queryKey: dashboardKeys.detail(houseId),
    queryFn: () => dashboardApi.getHouseDashboard(houseId),
    enabled: Boolean(houseId),
  })
}
