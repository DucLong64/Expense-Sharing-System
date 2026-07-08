import { useHouseMembers } from '@/features/house/api/house.query'
import { useHouseActions } from '@/features/house/hooks/use-house-actions'
import {
  canDeleteHouse,
  canManageHouse,
  findCurrentMember,
} from '@/features/house/utils/house-permissions'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { getCurrentUserId } from '@/shared/auth/current-user'

interface HouseSettingsSectionProps {
  houseId: string
  onEdit: () => void
}

export function HouseSettingsSection({ houseId, onEdit }: HouseSettingsSectionProps) {
  const currentUserId = getCurrentUserId()
  const { data: members = [] } = useHouseMembers(houseId)
  const currentMember = findCurrentMember(members, currentUserId)
  const { deleteHouse, leaveHouse, isDeleting, isLeaving } = useHouseActions(houseId)

  const canEdit = currentMember ? canManageHouse(currentMember.role) : false
  const canDelete = currentMember ? canDeleteHouse(currentMember.role) : false

  async function handleDelete() {
    if (!window.confirm('Xóa nhóm này? Hành động không thể hoàn tác.')) {
      return
    }
    await deleteHouse()
  }

  async function handleLeave() {
    if (!window.confirm('Rời khỏi nhóm này?')) {
      return
    }
    await leaveHouse()
  }

  return (
    <Card title="Cài đặt nhóm" description="Quản lý thông tin và quyền truy cập">
      <div className="space-y-4">
        {canEdit ? (
          <Button variant="secondary" className="w-auto" onClick={onEdit}>
            Chỉnh sửa thông tin
          </Button>
        ) : null}

        <div className="rounded-xl border border-slate-200 bg-slate-50/50 p-4">
          <p className="text-sm font-medium text-slate-900">Rời nhóm</p>
          <p className="mt-1 text-sm text-slate-500">Bạn sẽ không còn truy cập nhóm này.</p>
          <Button
            variant="secondary"
            className="mt-3 w-auto"
            loading={isLeaving}
            onClick={() => void handleLeave()}
          >
            Rời nhóm
          </Button>
        </div>

        {canDelete ? (
          <div className="rounded-xl border border-red-200 bg-red-50/50 p-4">
            <p className="text-sm font-medium text-red-800">Xóa nhóm</p>
            <p className="mt-1 text-sm text-red-600">
              Xóa vĩnh viễn nhóm và toàn bộ dữ liệu liên quan.
            </p>
            <Button
              variant="danger"
              className="mt-3 w-auto"
              loading={isDeleting}
              onClick={() => void handleDelete()}
            >
              Xóa nhóm
            </Button>
          </div>
        ) : null}
      </div>
    </Card>
  )
}
