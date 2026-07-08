export type SplitType = 'EQUAL' | 'FIXED' | 'PERCENTAGE'

export interface ParticipantShareRequest {
  userId: string
  amount?: number
  percentage?: number
}

export interface ExpenseParticipantResponse {
  userId: string
  username: string
  shareAmount: number
  sharePercentage: number | null
}

export interface ExpenseResponse {
  id: string
  houseId: string
  title: string
  description: string | null
  amount: number
  paidBy: string
  paidByUsername: string
  splitType: SplitType
  expenseDate: string
  note: string | null
  createdBy: string
  createdByUsername: string
  createdAt: string
  participants: ExpenseParticipantResponse[]
}

export interface CreateExpenseRequest {
  title: string
  description?: string
  amount: number
  paidBy: string
  splitType: SplitType
  expenseDate: string
  note?: string
  participants: ParticipantShareRequest[]
}

export interface UpdateExpenseRequest {
  title: string
  description?: string
  amount: number
  splitType: SplitType
  expenseDate: string
  note?: string
  participants: ParticipantShareRequest[]
}
