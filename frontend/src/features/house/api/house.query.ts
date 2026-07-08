import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import * as houseApi from '@/features/house/api/house.api'
import { activityKeys } from '@/features/activity/api/activity.query'
import { notificationKeys } from '@/features/notification/api/notification.query'

export const houseKeys = {
  all: ['houses'] as const,
  detail: (houseId: string) => ['houses', houseId] as const,
  members: (houseId: string) => ['houses', houseId, 'members'] as const,
}

function invalidateHouseQueries(
  queryClient: ReturnType<typeof useQueryClient>,
  houseId: string,
) {
  return Promise.all([
    queryClient.invalidateQueries({ queryKey: houseKeys.all }),
    queryClient.invalidateQueries({ queryKey: houseKeys.detail(houseId) }),
    queryClient.invalidateQueries({ queryKey: houseKeys.members(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.house(houseId) }),
    queryClient.invalidateQueries({ queryKey: activityKeys.my() }),
    queryClient.invalidateQueries({ queryKey: notificationKeys.unreadCount }),
    queryClient.invalidateQueries({ queryKey: ['notifications'] }),
  ])
}

export function useMyHouses() {
  return useQuery({
    queryKey: houseKeys.all,
    queryFn: houseApi.getMyHouses,
  })
}

export function useHouse(houseId: string) {
  return useQuery({
    queryKey: houseKeys.detail(houseId),
    queryFn: () => houseApi.getHouse(houseId),
    enabled: Boolean(houseId),
  })
}

export function useHouseMembers(houseId: string) {
  return useQuery({
    queryKey: houseKeys.members(houseId),
    queryFn: () => houseApi.getHouseMembers(houseId),
    enabled: Boolean(houseId),
  })
}

export function useCreateHouse(onSuccess?: () => void) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: houseApi.createHouse,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: houseKeys.all })
      onSuccess?.()
    },
  })
}

export function useUpdateHouse(houseId: string, onSuccess?: () => void) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof houseApi.updateHouse>[1]) =>
      houseApi.updateHouse(houseId, payload),
    onSuccess: async () => {
      await invalidateHouseQueries(queryClient, houseId)
      onSuccess?.()
    },
  })
}

export function useDeleteHouse() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: houseApi.deleteHouse,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: houseKeys.all })
      navigate('/')
    },
  })
}

export function useInviteMember(houseId: string, onSuccess?: () => void) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: Parameters<typeof houseApi.inviteMember>[1]) =>
      houseApi.inviteMember(houseId, payload),
    onSuccess: async () => {
      await invalidateHouseQueries(queryClient, houseId)
      onSuccess?.()
    },
  })
}

export function useChangeMemberRole(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      targetUserId,
      role,
    }: {
      targetUserId: string
      role: Parameters<typeof houseApi.changeMemberRole>[2]['role']
    }) => houseApi.changeMemberRole(houseId, targetUserId, { role }),
    onSuccess: async () => invalidateHouseQueries(queryClient, houseId),
  })
}

export function useRemoveMember(houseId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (targetUserId: string) => houseApi.removeMember(houseId, targetUserId),
    onSuccess: async () => invalidateHouseQueries(queryClient, houseId),
  })
}

export function useLeaveHouse() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: houseApi.leaveHouse,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: houseKeys.all })
      navigate('/')
    },
  })
}
