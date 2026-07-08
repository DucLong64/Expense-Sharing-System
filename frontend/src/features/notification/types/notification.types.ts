export type NotificationType =
  | 'EXPENSE_CREATED'
  | 'EXPENSE_UPDATED'
  | 'EXPENSE_DELETED'
  | 'DEBT_SETTLED'
  | 'MEMBER_INVITED'
  | 'MEMBER_REMOVED'
  | 'MEMBER_LEFT'

export type NotificationTargetType = 'HOUSE' | 'USER' | 'EXPENSE' | 'SETTLEMENT'

export interface NotificationResponse {
  id: string
  houseId: string
  actorUserId: string
  actorUsername: string
  type: NotificationType
  message: string
  targetType: NotificationTargetType | null
  targetId: string | null
  read: boolean
  readAt: string | null
  createdAt: string
}

export interface UnreadNotificationCountResponse {
  count: number
}

export interface NotificationFilter {
  houseId?: string
  unreadOnly?: boolean
}
