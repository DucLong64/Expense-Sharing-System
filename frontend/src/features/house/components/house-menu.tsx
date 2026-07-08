import { useHouseMembers } from '@/features/house/api/house.query'
import { canManageHouse, findCurrentMember } from '@/features/house/utils/house-permissions'
import { DropdownMenu } from '@/shared/components/dropdown-menu'
import { getCurrentUserId } from '@/shared/auth/current-user'

interface HouseMenuProps {
  houseId: string
  onEdit: () => void
  onOpenSettings: () => void
  onOpenActivity: () => void
}

export function HouseMenu({ houseId, onEdit, onOpenSettings, onOpenActivity }: HouseMenuProps) {
  const currentUserId = getCurrentUserId()
  const { data: members = [] } = useHouseMembers(houseId)
  const currentMember = findCurrentMember(members, currentUserId)
  const canEdit = currentMember ? canManageHouse(currentMember.role) : false

  const items = [
    ...(canEdit
      ? [
          {
            label: 'Chỉnh sửa nhóm',
            onClick: onEdit,
          },
        ]
      : []),
    {
      label: 'Nhật ký hoạt động',
      onClick: onOpenActivity,
    },
    {
      label: 'Cài đặt nhóm',
      onClick: onOpenSettings,
    },
  ]

  return <DropdownMenu ariaLabel="Tùy chọn nhóm" items={items} align="right" />
}
