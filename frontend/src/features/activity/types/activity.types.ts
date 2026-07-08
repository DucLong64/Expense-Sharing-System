export type ActivityType =
  | 'HOUSE_CREATED'
  | 'HOUSE_UPDATED'
  | 'HOUSE_DELETED'
  | 'MEMBER_INVITED'
  | 'MEMBER_REMOVED'
  | 'MEMBER_LEFT'
  | 'EXPENSE_CREATED'
  | 'EXPENSE_UPDATED'
  | 'EXPENSE_DELETED'
  | 'DEBT_SETTLED'

export type ActivityTargetType = 'HOUSE' | 'USER' | 'EXPENSE' | 'SETTLEMENT'

export interface ActivityLogResponse {
  id: string
  houseId: string
  actorUserId: string
  actorUsername: string
  type: ActivityType
  targetType: ActivityTargetType
  targetId: string
  message: string
  createdAt: string
}

export interface ActivityFilter {
  activityType?: ActivityType
  houseId?: string
}
