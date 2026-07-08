import { getAccessToken } from '@/shared/auth/auth-storage'
import { getUserIdFromToken } from '@/shared/auth/jwt'

export function getCurrentUserId(): string | null {
  const token = getAccessToken()
  if (!token) {
    return null
  }
  return getUserIdFromToken(token)
}
