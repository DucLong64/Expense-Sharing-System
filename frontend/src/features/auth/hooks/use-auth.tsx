import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import * as authApi from '@/features/auth/api/auth.api'
import type { LoginRequest, RegisterRequest } from '@/features/auth/types/auth.types'
import {
  clearTokens,
  getRefreshToken,
  isAuthenticated,
  setTokens,
} from '@/shared/auth/auth-storage'

interface AuthContextValue {
  isAuthenticated: boolean
  login: (payload: LoginRequest) => Promise<void>
  register: (payload: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [authenticated, setAuthenticated] = useState(isAuthenticated)

  const login = useCallback(async (payload: LoginRequest) => {
    const tokens = await authApi.login(payload)
    setTokens(tokens.accessToken, tokens.refreshToken)
    setAuthenticated(true)
  }, [])

  const register = useCallback(async (payload: RegisterRequest) => {
    const tokens = await authApi.register(payload)
    setTokens(tokens.accessToken, tokens.refreshToken)
    setAuthenticated(true)
  }, [])

  const logout = useCallback(async () => {
    const refreshToken = getRefreshToken()
    if (refreshToken) {
      try {
        await authApi.logout(refreshToken)
      } catch {
        // Clear local session even if logout API fails.
      }
    }
    clearTokens()
    setAuthenticated(false)
  }, [])

  const value = useMemo(
    () => ({ isAuthenticated: authenticated, login, register, logout }),
    [authenticated, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
