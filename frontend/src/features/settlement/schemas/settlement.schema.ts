import { z } from 'zod'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'

const amountSchema = z
  .number()
  .refine((value) => Number.isFinite(value), { message: 'Số tiền không hợp lệ' })
  .refine((value) => value > 0, { message: 'Số tiền phải lớn hơn 0' })

export const settleDebtSchema = z.object({
  toUserId: z.string().uuid('Người nhận không hợp lệ'),
  amount: amountSchema,
  note: z.string().max(500, 'Ghi chú tối đa 500 ký tự').optional(),
})

export type SettleDebtFormValues = z.infer<typeof settleDebtSchema>

function roundMoney(value: number): number {
  return Math.round(value * 100) / 100
}

export function createSettleDebtSchema(myDebts: DebtSummaryResponse[]) {
  return settleDebtSchema.superRefine((data, ctx) => {
    const debt = myDebts.find((item) => item.toUserId === data.toUserId)
    if (!debt) {
      ctx.addIssue({
        code: 'custom',
        message: 'Bạn không nợ người này trong nhóm.',
        path: ['toUserId'],
      })
      return
    }

    if (roundMoney(data.amount) > roundMoney(debt.amount)) {
      ctx.addIssue({
        code: 'custom',
        message: 'Số tiền không được vượt quá số nợ hiện tại.',
        path: ['amount'],
      })
    }
  })
}

export const confirmDebtReceivedSchema = z.object({
  fromUserId: z.string().uuid('Người trả không hợp lệ'),
  amount: amountSchema,
  note: z.string().max(500, 'Ghi chú tối đa 500 ký tự').optional(),
})

export type ConfirmDebtReceivedFormValues = z.infer<typeof confirmDebtReceivedSchema>

export function createConfirmDebtReceivedSchema(owedToMe: DebtSummaryResponse[]) {
  return confirmDebtReceivedSchema.superRefine((data, ctx) => {
    const debt = owedToMe.find((item) => item.fromUserId === data.fromUserId)
    if (!debt) {
      ctx.addIssue({
        code: 'custom',
        message: 'Người này không nợ bạn trong nhóm.',
        path: ['fromUserId'],
      })
      return
    }

    if (roundMoney(data.amount) > roundMoney(debt.amount)) {
      ctx.addIssue({
        code: 'custom',
        message: 'Số tiền không được vượt quá số nợ hiện tại.',
        path: ['amount'],
      })
    }
  })
}
