import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as expenseApi from '@/features/expense/api/expense.api'
import { activityKeys } from '@/features/activity/api/activity.query'
import { houseKeys } from '@/features/house/api/house.query'
import { dashboardKeys } from '@/features/dashboard/api/dashboard.query'
import { settlementKeys } from '@/features/settlement/api/settlement.query'

export const expenseKeys = {
  all: (houseId: string) => ['expenses', houseId] as const,
  detail: (houseId: string, expenseId: string) => ['expenses', houseId, expenseId] as const,
}

function invalidateExpenseRelatedQueries(
  queryClient: ReturnType<typeof useQueryClient>,
  houseId: string,
) {
  return Promise.all([
    queryClient.invalidateQueries({ queryKey: expenseKeys.all(houseId) }),
    queryClient.invalidateQueries({ queryKey: settlementKeys.debts(houseId) }),
    queryClient.invalidateQueries({ queryKey: settlementKeys.history(houseId) }),
    queryClient.invalidateQueries({ queryKey: dashboardKeys.detail(houseId) }),
    queryClient.invalidateQueries({ queryKey: houseKeys.members(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.house(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.my() }),
  ])
}

export function useExpenses(houseId: string) {
  return useQuery({
    queryKey: expenseKeys.all(houseId),
    queryFn: () => expenseApi.getExpenses(houseId),
    enabled: Boolean(houseId),
  })
}

export function useExpense(houseId: string, expenseId: string, enabled = true) {
  return useQuery({
    queryKey: expenseKeys.detail(houseId, expenseId),
    queryFn: () => expenseApi.getExpense(houseId, expenseId),
    enabled: Boolean(houseId && expenseId && enabled),
  })
}

export function useCreateExpense(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof expenseApi.createExpense>[1]) =>
      expenseApi.createExpense(houseId, payload),
    onSuccess: async () => invalidateExpenseRelatedQueries(queryClient, houseId),
  })
}

export function useUpdateExpense(houseId: string, expenseId: string, onSuccess?: () => void) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof expenseApi.updateExpense>[2]) =>
      expenseApi.updateExpense(houseId, expenseId, payload),
    onSuccess: async () => {
      await invalidateExpenseRelatedQueries(queryClient, houseId)
      await queryClient.invalidateQueries({ queryKey: expenseKeys.detail(houseId, expenseId) })
      onSuccess?.()
    },
  })
}

export function useDeleteExpense(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (expenseId: string) => expenseApi.deleteExpense(houseId, expenseId),
    onSuccess: async () => invalidateExpenseRelatedQueries(queryClient, houseId),
  })
}
