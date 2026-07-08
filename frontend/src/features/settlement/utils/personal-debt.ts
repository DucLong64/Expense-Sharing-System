import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'

export interface PersonalDebtSummary {
  totalOwed: number
  totalOwingToMe: number
  myDebts: DebtSummaryResponse[]
  owedToMe: DebtSummaryResponse[]
  isBalanced: boolean
}

export function summarizePersonalDebts(
  debts: DebtSummaryResponse[],
  userId: string | null,
): PersonalDebtSummary {
  if (!userId) {
    return {
      totalOwed: 0,
      totalOwingToMe: 0,
      myDebts: [],
      owedToMe: [],
      isBalanced: true,
    }
  }

  const myDebts = debts.filter((debt) => debt.fromUserId === userId)
  const owedToMe = debts.filter((debt) => debt.toUserId === userId)
  const totalOwed = myDebts.reduce((sum, debt) => sum + debt.amount, 0)
  const totalOwingToMe = owedToMe.reduce((sum, debt) => sum + debt.amount, 0)

  return {
    totalOwed,
    totalOwingToMe,
    myDebts,
    owedToMe,
    isBalanced: totalOwed === 0 && totalOwingToMe === 0,
  }
}
