import type { ActivityType } from '@/features/activity/types/activity.types'

export const activityTypeLabels: Record<ActivityType, string> = {
  HOUSE_CREATED: 'Tạo nhóm',
  HOUSE_UPDATED: 'Cập nhật nhóm',
  HOUSE_DELETED: 'Xóa nhóm',
  MEMBER_INVITED: 'Mời thành viên',
  MEMBER_REMOVED: 'Xóa thành viên',
  MEMBER_LEFT: 'Rời nhóm',
  EXPENSE_CREATED: 'Thêm khoản chi',
  EXPENSE_UPDATED: 'Cập nhật khoản chi',
  EXPENSE_DELETED: 'Xóa khoản chi',
  DEBT_SETTLED: 'Thanh toán công nợ',
}

export const activityTypeOptions: ActivityType[] = [
  'EXPENSE_CREATED',
  'EXPENSE_UPDATED',
  'EXPENSE_DELETED',
  'DEBT_SETTLED',
  'MEMBER_INVITED',
  'MEMBER_REMOVED',
  'MEMBER_LEFT',
  'HOUSE_UPDATED',
]
