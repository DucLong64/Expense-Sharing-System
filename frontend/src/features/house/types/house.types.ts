export type HouseRole = 'OWNER' | 'ADMIN' | 'MEMBER' | 'VIEWER'

export interface HouseResponse {
  id: string
  name: string
  description: string | null
  createdBy: string
  createdAt: string
}

export interface HouseMemberResponse {
  id: string
  houseId: string
  userId: string
  username: string
  role: HouseRole
  joinedAt: string
}

export interface CreateHouseRequest {
  name: string
  description?: string
}

export interface UpdateHouseRequest {
  name: string
  description?: string
}

export interface InviteMemberRequest {
  identifier: string
  role: HouseRole
}

export interface ChangeMemberRoleRequest {
  role: HouseRole
}
