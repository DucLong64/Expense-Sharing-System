import { apiRequest } from '@/shared/api/axios-client'
import type {
  CreateExpenseRequest,
  ExpenseResponse,
  UpdateExpenseRequest,
} from '@/features/expense/types/expense.types'

export function getExpenses(houseId: string): Promise<ExpenseResponse[]> {
  return apiRequest<ExpenseResponse[]>({
    url: `/api/v1/houses/${houseId}/expenses`,
    method: 'GET',
  })
}

export function getExpense(houseId: string, expenseId: string): Promise<ExpenseResponse> {
  return apiRequest<ExpenseResponse>({
    url: `/api/v1/houses/${houseId}/expenses/${expenseId}`,
    method: 'GET',
  })
}

export function createExpense(
  houseId: string,
  payload: CreateExpenseRequest,
): Promise<ExpenseResponse> {
  return apiRequest<ExpenseResponse>({
    url: `/api/v1/houses/${houseId}/expenses`,
    method: 'POST',
    data: payload,
  })
}

export function updateExpense(
  houseId: string,
  expenseId: string,
  payload: UpdateExpenseRequest,
): Promise<ExpenseResponse> {
  return apiRequest<ExpenseResponse>({
    url: `/api/v1/houses/${houseId}/expenses/${expenseId}`,
    method: 'PUT',
    data: payload,
  })
}

export function deleteExpense(houseId: string, expenseId: string): Promise<void> {
  return apiRequest<void>({
    url: `/api/v1/houses/${houseId}/expenses/${expenseId}`,
    method: 'DELETE',
  })
}
