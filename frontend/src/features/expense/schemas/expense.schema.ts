import { z } from 'zod'

const participantFormSchema = z.object({
  userId: z.string().uuid(),
  selected: z.boolean(),
  shareAmount: z.number().positive().optional(),
  sharePercentage: z.number().positive().max(100).optional(),
})

const baseExpenseSchema = z.object({
  title: z.string().min(1, 'Tiêu đề là bắt buộc').max(200, 'Tiêu đề tối đa 200 ký tự'),
  description: z.string().max(500, 'Mô tả tối đa 500 ký tự').optional(),
  amount: z.number().positive('Số tiền phải lớn hơn 0'),
  splitType: z.enum(['EQUAL', 'FIXED', 'PERCENTAGE']),
  expenseDate: z.string().min(1, 'Ngày phát sinh là bắt buộc'),
  note: z.string().max(500, 'Ghi chú tối đa 500 ký tự').optional(),
  participants: z.array(participantFormSchema),
})

function validateParticipants(
  data: z.infer<typeof baseExpenseSchema>,
  ctx: z.RefinementCtx,
) {
  const selected = data.participants.filter((participant) => participant.selected)

  if (selected.length === 0) {
    ctx.addIssue({
      code: 'custom',
      message: 'Chọn ít nhất một thành viên',
      path: ['participants'],
    })
    return
  }

  if (data.splitType === 'FIXED') {
    for (const participant of selected) {
      if (!participant.shareAmount || participant.shareAmount <= 0) {
        ctx.addIssue({
          code: 'custom',
          message: 'Nhập số tiền cho từng thành viên',
          path: ['participants'],
        })
        return
      }
    }
    const total = selected.reduce((sum, participant) => sum + (participant.shareAmount ?? 0), 0)
    if (Math.abs(total - data.amount) > 0.01) {
      ctx.addIssue({
        code: 'custom',
        message: 'Tổng phần chia phải bằng số tiền khoản chi',
        path: ['participants'],
      })
    }
  }

  if (data.splitType === 'PERCENTAGE') {
    for (const participant of selected) {
      if (!participant.sharePercentage || participant.sharePercentage <= 0) {
        ctx.addIssue({
          code: 'custom',
          message: 'Nhập phần trăm cho từng thành viên',
          path: ['participants'],
        })
        return
      }
    }
    const total = selected.reduce(
      (sum, participant) => sum + (participant.sharePercentage ?? 0),
      0,
    )
    if (Math.abs(total - 100) > 0.01) {
      ctx.addIssue({
        code: 'custom',
        message: 'Tổng phần trăm phải bằng 100%',
        path: ['participants'],
      })
    }
  }
}

export const createExpenseSchema = baseExpenseSchema
  .extend({ paidBy: z.string().uuid('Người trả không hợp lệ') })
  .superRefine(validateParticipants)

export type CreateExpenseFormValues = z.infer<typeof createExpenseSchema>

export const updateExpenseSchema = baseExpenseSchema.superRefine(validateParticipants)

export type UpdateExpenseFormValues = z.infer<typeof updateExpenseSchema>

export const splitTypeLabels: Record<'EQUAL' | 'FIXED' | 'PERCENTAGE', string> = {
  EQUAL: 'Chia đều',
  FIXED: 'Số tiền cố định',
  PERCENTAGE: 'Theo phần trăm',
}
