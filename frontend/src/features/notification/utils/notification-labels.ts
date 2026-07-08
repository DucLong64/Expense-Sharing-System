import type { NotificationType } from '@/features/notification/types/notification.types'

export const notificationTypeLabels: Record<NotificationType, string> = {
  EXPENSE_CREATED: 'Khoản chi mới',
  EXPENSE_UPDATED: 'Cập nhật chi phí',
  EXPENSE_DELETED: 'Xóa chi phí',
  DEBT_SETTLED: 'Thanh toán',
  MEMBER_INVITED: 'Thành viên mới',
  MEMBER_REMOVED: 'Xóa thành viên',
  MEMBER_LEFT: 'Rời nhóm',
}
