import { apiRequest } from '@/shared/api/axios-client'
import type {
  ConfirmDebtReceivedRequest,
  DebtSummaryResponse,
  SettleDebtRequest,
  SettlementResponse,
} from '@/features/settlement/types/settlement.types'

export function getDebts(houseId: string): Promise<DebtSummaryResponse[]> {
  return apiRequest<DebtSummaryResponse[]>({
    url: `/api/v1/houses/${houseId}/debts`,
    method: 'GET',
  })
}

export function getSettlements(houseId: string): Promise<SettlementResponse[]> {
  return apiRequest<SettlementResponse[]>({
    url: `/api/v1/houses/${houseId}/settlements`,
    method: 'GET',
  })
}

export function settleDebt(houseId: string, payload: SettleDebtRequest): Promise<SettlementResponse> {
  return apiRequest<SettlementResponse>({
    url: `/api/v1/houses/${houseId}/settlements`,
    method: 'POST',
    data: payload,
  })
}

export function confirmDebtReceived(
  houseId: string,
  payload: ConfirmDebtReceivedRequest,
): Promise<SettlementResponse> {
  return apiRequest<SettlementResponse>({
    url: `/api/v1/houses/${houseId}/settlements/confirm-received`,
    method: 'POST',
    data: payload,
  })
}
