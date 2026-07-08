import axios, { type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import * as authApi from '@/features/auth/api/auth.api'
import { ApiError } from '@/shared/api/api-error'
import type { ApiErrorBody, ApiResponse } from '@/shared/api/api-response.types'
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  setTokens,
} from '@/shared/auth/auth-storage'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const accessToken = getAccessToken()
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

type RetryableConfig = InternalAxiosRequestConfig & { _retry?: boolean }

let refreshPromise: Promise<string> | null = null

async function refreshAccessToken(): Promise<string> {
  const refreshTokenValue = getRefreshToken()
  if (!refreshTokenValue) {
    throw new ApiError('UNAUTHORIZED', 'Phiên đăng nhập đã hết hạn.')
  }

  if (!refreshPromise) {
    refreshPromise = authApi
      .refreshToken(refreshTokenValue)
      .then((tokens) => {
        setTokens(tokens.accessToken, tokens.refreshToken)
        return tokens.accessToken
      })
      .finally(() => {
        refreshPromise = null
      })
  }

  return refreshPromise
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (!axios.isAxiosError(error) || !error.config) {
      throw error
    }

    const originalRequest = error.config as RetryableConfig
    const isUnauthorized = error.response?.status === 401
    const isAuthEndpoint = originalRequest.url?.includes('/api/v1/auth/')

    if (isUnauthorized && !originalRequest._retry && !isAuthEndpoint) {
      originalRequest._retry = true

      try {
        const accessToken = await refreshAccessToken()
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return apiClient.request(originalRequest)
      } catch {
        clearTokens()
        window.location.href = '/login'
        throw error
      }
    }

    throw error
  },
)

export async function apiRequest<T>(config: AxiosRequestConfig): Promise<T> {
  try {
    const response = await apiClient.request<ApiResponse<T>>(config)
    const body = response.data

    if (!body.success) {
      throw new ApiError(body.code ?? 'REQUEST_FAILED', body.message ?? 'Request failed.')
    }

    return body.data
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.data) {
      const body = error.response.data as ApiErrorBody
      throw new ApiError(body.code ?? 'REQUEST_FAILED', body.message ?? 'Request failed.')
    }

    throw error
  }
}
