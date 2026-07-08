import type { HouseMemberResponse, HouseRole } from '@/features/house/types/house.types'

const roleRank: Record<HouseRole, number> = {
  OWNER: 4,
  ADMIN: 3,
  MEMBER: 2,
  VIEWER: 1,
}

export function canManageHouse(role: HouseRole): boolean {
  return roleRank[role] >= roleRank.ADMIN
}

export function canDeleteHouse(role: HouseRole): boolean {
  return role === 'OWNER'
}

export function canChangeMemberRole(role: HouseRole): boolean {
  return role === 'OWNER'
}

export function canInviteMember(role: HouseRole): boolean {
  return roleRank[role] >= roleRank.ADMIN
}

export function canRemoveMember(role: HouseRole): boolean {
  return roleRank[role] >= roleRank.ADMIN
}

export function findCurrentMember(
  members: HouseMemberResponse[],
  currentUserId: string | null,
): HouseMemberResponse | undefined {
  if (!currentUserId) {
    return undefined
  }
  return members.find((member) => member.userId === currentUserId)
}

export const assignableRoles: HouseRole[] = ['ADMIN', 'MEMBER', 'VIEWER']

export const roleLabels: Record<HouseRole, string> = {
  OWNER: 'Chủ nhóm',
  ADMIN: 'Quản trị',
  MEMBER: 'Thành viên',
  VIEWER: 'Chỉ xem',
}
