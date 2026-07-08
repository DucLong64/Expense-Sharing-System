import { useDeleteHouse, useLeaveHouse } from '@/features/house/api/house.query'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useHouseActions(houseId: string) {
  const { showToast } = useToast()
  const deleteMutation = useDeleteHouse()
  const leaveMutation = useLeaveHouse()

  async function deleteHouse() {
    try {
      await deleteMutation.mutateAsync(houseId)
      showToast('Đã xóa nhóm.', 'success')
    } catch (error) {
      showToast(error instanceof ApiError ? error.message : 'Không thể xóa nhóm.')
    }
  }

  async function leaveHouse() {
    try {
      await leaveMutation.mutateAsync(houseId)
      showToast('Đã rời nhóm.', 'success')
    } catch (error) {
      showToast(error instanceof ApiError ? error.message : 'Không thể rời nhóm.')
    }
  }

  return {
    deleteHouse,
    leaveHouse,
    isDeleting: deleteMutation.isPending,
    isLeaving: leaveMutation.isPending,
  }
}
