import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as settlementApi from '@/features/settlement/api/settlement.api'
import { activityKeys } from '@/features/activity/api/activity.query'
import { dashboardKeys } from '@/features/dashboard/api/dashboard.query'
import { notificationKeys } from '@/features/notification/api/notification.query'

export const settlementKeys = {
  debts: (houseId: string) => ['debts', houseId] as const,
  history: (houseId: string) => ['settlements', houseId] as const,
}

export function useDebts(houseId: string) {
  return useQuery({
    queryKey: settlementKeys.debts(houseId),
    queryFn: () => settlementApi.getDebts(houseId),
    enabled: Boolean(houseId),
  })
}

export function useSettlements(houseId: string) {
  return useQuery({
    queryKey: settlementKeys.history(houseId),
    queryFn: () => settlementApi.getSettlements(houseId),
    enabled: Boolean(houseId),
  })
}

export function useSettleDebt(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof settlementApi.settleDebt>[1]) =>
      settlementApi.settleDebt(houseId, payload),
    onSuccess: async () => invalidateSettlementQueries(queryClient, houseId),
  })
}

export function useConfirmDebtReceived(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof settlementApi.confirmDebtReceived>[1]) =>
      settlementApi.confirmDebtReceived(houseId, payload),
    onSuccess: async () => invalidateSettlementQueries(queryClient, houseId),
  })
}

async function invalidateSettlementQueries(
  queryClient: ReturnType<typeof useQueryClient>,
  houseId: string,
) {
  await Promise.all([
    queryClient.invalidateQueries({ queryKey: settlementKeys.debts(houseId) }),
    queryClient.invalidateQueries({ queryKey: settlementKeys.history(houseId) }),
    queryClient.invalidateQueries({ queryKey: dashboardKeys.detail(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.house(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.my() }),
    queryClient.invalidateQueries({ queryKey: notificationKeys.unreadCount }),
    queryClient.invalidateQueries({ queryKey: ['notifications'] }),
  ])
}
