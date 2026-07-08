export interface DebtSummaryResponse {
  fromUserId: string
  fromUsername: string
  toUserId: string
  toUsername: string
  amount: number
}

export interface SettleDebtRequest {
  toUserId: string
  amount: number
  note?: string
}

export interface SettlementResponse {
  id: string
  houseId: string
  fromUserId: string
  fromUsername: string
  toUserId: string
  toUsername: string
  amount: number
  note: string | null
  settledAt: string
}
