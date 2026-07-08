import { apiRequest } from '@/shared/api/axios-client'
import type {
  ChangeMemberRoleRequest,
  CreateHouseRequest,
  HouseMemberResponse,
  HouseResponse,
  InviteMemberRequest,
  UpdateHouseRequest,
} from '@/features/house/types/house.types'

export function getMyHouses(): Promise<HouseResponse[]> {
  return apiRequest<HouseResponse[]>({ url: '/api/v1/houses', method: 'GET' })
}

export function getHouse(houseId: string): Promise<HouseResponse> {
  return apiRequest<HouseResponse>({ url: `/api/v1/houses/${houseId}`, method: 'GET' })
}

export function createHouse(payload: CreateHouseRequest): Promise<HouseResponse> {
  return apiRequest<HouseResponse>({ url: '/api/v1/houses', method: 'POST', data: payload })
}

export function updateHouse(houseId: string, payload: UpdateHouseRequest): Promise<HouseResponse> {
  return apiRequest<HouseResponse>({
    url: `/api/v1/houses/${houseId}`,
    method: 'PUT',
    data: payload,
  })
}

export function deleteHouse(houseId: string): Promise<void> {
  return apiRequest<void>({ url: `/api/v1/houses/${houseId}`, method: 'DELETE' })
}

export function getHouseMembers(houseId: string): Promise<HouseMemberResponse[]> {
  return apiRequest<HouseMemberResponse[]>({
    url: `/api/v1/houses/${houseId}/members`,
    method: 'GET',
  })
}

export function inviteMember(
  houseId: string,
  payload: InviteMemberRequest,
): Promise<HouseMemberResponse> {
  return apiRequest<HouseMemberResponse>({
    url: `/api/v1/houses/${houseId}/members`,
    method: 'POST',
    data: payload,
  })
}

export function changeMemberRole(
  houseId: string,
  targetUserId: string,
  payload: ChangeMemberRoleRequest,
): Promise<HouseMemberResponse> {
  return apiRequest<HouseMemberResponse>({
    url: `/api/v1/houses/${houseId}/members/${targetUserId}/role`,
    method: 'PUT',
    data: payload,
  })
}

export function removeMember(houseId: string, targetUserId: string): Promise<void> {
  return apiRequest<void>({
    url: `/api/v1/houses/${houseId}/members/${targetUserId}`,
    method: 'DELETE',
  })
}

export function leaveHouse(houseId: string): Promise<void> {
  return apiRequest<void>({
    url: `/api/v1/houses/${houseId}/members/me`,
    method: 'DELETE',
  })
}
