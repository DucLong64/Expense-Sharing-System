import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'

export interface HouseDashboardResponse {
  totalSpending: number
  totalSettled: number
  spendingByMonth: Array<{ year: number; month: number; amount: number }>
  spendingByMember: Array<{ userId: string; username: string; amount: number }>
  currentDebts: DebtSummaryResponse[]
}
