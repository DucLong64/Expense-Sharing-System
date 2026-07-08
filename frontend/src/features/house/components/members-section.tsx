import { useState } from 'react'
import {
  useChangeMemberRole,
  useHouseMembers,
  useRemoveMember,
} from '@/features/house/api/house.query'
import { InviteMemberModal } from '@/features/house/components/invite-member-modal'
import {
  assignableRoles,
  canChangeMemberRole,
  canInviteMember,
  canRemoveMember,
  findCurrentMember,
  roleLabels,
} from '@/features/house/utils/house-permissions'
import type { HouseMemberResponse, HouseRole } from '@/features/house/types/house.types'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { ApiError } from '@/shared/api/api-error'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { useToast } from '@/shared/hooks/use-toast'
import { formatDateTime, displayUsername } from '@/shared/utils/format'

interface MemberRowProps {
  member: HouseMemberResponse
  houseId: string
  currentUserId: string | null
  currentUserRole?: HouseRole
}

function MemberRow({ member, houseId, currentUserId, currentUserRole }: MemberRowProps) {
  const { showToast } = useToast()
  const changeRoleMutation = useChangeMemberRole(houseId)
  const removeMutation = useRemoveMember(houseId)
  const isSelf = member.userId === currentUserId
  const canChange = currentUserRole && canChangeMemberRole(currentUserRole) && member.role !== 'OWNER'
  const canRemove =
    currentUserRole &&
    canRemoveMember(currentUserRole) &&
    member.role !== 'OWNER' &&
    !isSelf

  async function handleRoleChange(role: HouseRole) {
    try {
      await changeRoleMutation.mutateAsync({ targetUserId: member.userId, role })
      showToast('Đã cập nhật vai trò.', 'success')
    } catch (error) {
      showToast(error instanceof ApiError ? error.message : 'Không thể đổi vai trò.')
    }
  }

  async function handleRemove() {
    if (!window.confirm('Xóa thành viên khỏi nhóm?')) {
      return
    }
    try {
      await removeMutation.mutateAsync(member.userId)
      showToast('Đã xóa thành viên.', 'success')
    } catch (error) {
      showToast(error instanceof ApiError ? error.message : 'Không thể xóa thành viên.')
    }
  }

  return (
    <div className="flex flex-col gap-3 rounded-xl border border-slate-200/80 bg-slate-50/50 p-4 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p className="font-medium text-slate-900">
          {displayUsername(member.username, member.userId)}
          {isSelf ? <span className="ml-2 text-xs text-emerald-600">(Bạn)</span> : null}
        </p>
        <p className="mt-1 text-xs text-slate-500">Tham gia {formatDateTime(member.joinedAt)}</p>
      </div>
      <div className="flex flex-wrap items-center gap-2">
        {canChange ? (
          <select
            className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-sm"
            value={member.role}
            disabled={changeRoleMutation.isPending}
            onChange={(event) => void handleRoleChange(event.target.value as HouseRole)}
          >
            {assignableRoles.map((role) => (
              <option key={role} value={role}>
                {roleLabels[role]}
              </option>
            ))}
          </select>
        ) : (
          <span className="rounded-lg bg-white px-3 py-1.5 text-sm font-medium text-slate-700 shadow-sm">
            {roleLabels[member.role]}
          </span>
        )}
        {canRemove ? (
          <Button variant="danger" size="sm" className="w-auto" onClick={() => void handleRemove()}>
            Xóa
          </Button>
        ) : null}
      </div>
    </div>
  )
}

interface MembersSectionProps {
  houseId: string
}

export function MembersSection({ houseId }: MembersSectionProps) {
  const [openInvite, setOpenInvite] = useState(false)
  const currentUserId = getCurrentUserId()
  const { data: members = [], isLoading, error, refetch } = useHouseMembers(houseId)
  const currentMember = findCurrentMember(members, currentUserId)
  const canInvite = currentMember ? canInviteMember(currentMember.role) : false

  return (
    <>
      <Card
        title="Thành viên"
        description={`${members.length} thành viên trong nhóm`}
        action={
          canInvite ? (
            <Button size="sm" className="w-auto" onClick={() => setOpenInvite(true)}>
              Mời thành viên
            </Button>
          ) : null
        }
      >
        {isLoading ? <LoadingState message="Đang tải thành viên..." /> : null}
        {error ? (
          <div className="space-y-3">
            <ErrorMessage message="Không thể tải danh sách thành viên." />
            <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
              Thử lại
            </Button>
          </div>
        ) : null}
        {!isLoading && !error ? (
          <div className="space-y-3">
            {members.map((member) => (
              <MemberRow
                key={member.id}
                member={member}
                houseId={houseId}
                currentUserId={currentUserId}
                currentUserRole={currentMember?.role}
              />
            ))}
          </div>
        ) : null}
      </Card>

      <InviteMemberModal open={openInvite} houseId={houseId} onClose={() => setOpenInvite(false)} />
    </>
  )
}
