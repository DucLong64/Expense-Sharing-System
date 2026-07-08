import { z } from 'zod'

export const settleDebtSchema = z.object({
  toUserId: z.string().uuid('Người nhận không hợp lệ'),
  amount: z.number().positive('Số tiền phải lớn hơn 0'),
  note: z.string().max(500, 'Ghi chú tối đa 500 ký tự').optional(),
})

export type SettleDebtFormValues = z.infer<typeof settleDebtSchema>
