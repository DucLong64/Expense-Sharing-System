import type { ParticipantShareRequest, SplitType } from '@/features/expense/types/expense.types'

interface ParticipantFormValue {
  userId: string
  selected: boolean
  shareAmount?: number
  sharePercentage?: number
}

export function buildParticipantPayload(
  participants: ParticipantFormValue[],
  splitType: SplitType,
): ParticipantShareRequest[] {
  return participants
    .filter((participant) => participant.selected)
    .map((participant) => {
      if (splitType === 'EQUAL') {
        return { userId: participant.userId }
      }
      if (splitType === 'FIXED') {
        return { userId: participant.userId, amount: participant.shareAmount }
      }
      return { userId: participant.userId, percentage: participant.sharePercentage }
    })
}

export function createDefaultParticipants(userIds: string[]) {
  return userIds.map((userId) => ({
    userId,
    selected: true,
    shareAmount: undefined,
    sharePercentage: undefined,
  }))
}
