import axios from 'axios'
import { apiRequest } from '@/shared/api/axios-client'
import type { ApiResponse } from '@/shared/api/api-response.types'
import { ApiError } from '@/shared/api/api-error'
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/features/auth/types/auth.types'

const authBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

export function login(payload: LoginRequest): Promise<AuthResponse> {
  return apiRequest<AuthResponse>({
    url: '/api/v1/auth/login',
    method: 'POST',
    data: payload,
  })
}

export function register(payload: RegisterRequest): Promise<AuthResponse> {
  return apiRequest<AuthResponse>({
    url: '/api/v1/auth/register',
    method: 'POST',
    data: payload,
  })
}

export async function refreshToken(refreshTokenValue: string): Promise<AuthResponse> {
  const response = await axios.post<ApiResponse<AuthResponse>>(
    `${authBaseUrl}/api/v1/auth/refresh`,
    { refreshToken: refreshTokenValue },
    { headers: { 'Content-Type': 'application/json' } },
  )
  const body = response.data

  if (!body.success || !body.data) {
    throw new ApiError(body.code ?? 'REFRESH_FAILED', body.message ?? 'Không thể làm mới phiên.')
  }

  return body.data
}

export function logout(refreshTokenValue: string): Promise<void> {
  return apiRequest<void>({
    url: '/api/v1/auth/logout',
    method: 'POST',
    data: { refreshToken: refreshTokenValue },
  })
}
