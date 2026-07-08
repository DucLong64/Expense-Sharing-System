import { useMutation, useQuery } from '@tanstack/react-query'
import * as authApi from '@/features/auth/api/auth.api'
import type { ChangePasswordRequest } from '@/features/auth/types/auth.types'

export const authKeys = {
  me: ['auth', 'me'] as const,
}

export function useCurrentUser() {
  return useQuery({
    queryKey: authKeys.me,
    queryFn: authApi.getMe,
  })
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (payload: ChangePasswordRequest) => authApi.changePassword(payload),
  })
}
