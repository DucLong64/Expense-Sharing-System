import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { useUpdateExpense } from '@/features/expense/api/expense.query'
import {
  createExpenseSchema,
  type CreateExpenseFormValues,
} from '@/features/expense/schemas/expense.schema'
import { buildParticipantPayload } from '@/features/expense/utils/expense-participants'
import type { ExpenseResponse } from '@/features/expense/types/expense.types'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

function mapExpenseToFormValues(
  expense: ExpenseResponse,
  members: HouseMemberResponse[],
): CreateExpenseFormValues {
  const participantUserIds = new Set(expense.participants.map((item) => item.userId))

  return {
    title: expense.title,
    description: expense.description ?? '',
    amount: expense.amount,
    paidBy: expense.paidBy,
    splitType: expense.splitType,
    expenseDate: expense.expenseDate,
    note: expense.note ?? '',
    participants: members.map((member) => {
      const share = expense.participants.find((item) => item.userId === member.userId)
      return {
        userId: member.userId,
        selected: participantUserIds.has(member.userId),
        shareAmount: share?.shareAmount,
        sharePercentage: share?.sharePercentage ?? undefined,
      }
    }),
  }
}

export function useEditExpenseForm(
  houseId: string,
  expense: ExpenseResponse,
  members: HouseMemberResponse[],
  onSuccess?: () => void,
) {
  const { showToast } = useToast()
  const updateMutation = useUpdateExpense(houseId, expense.id, onSuccess)

  const form = useForm<CreateExpenseFormValues>({
    resolver: zodResolver(createExpenseSchema),
    defaultValues: mapExpenseToFormValues(expense, members),
  })

  useEffect(() => {
    form.reset(mapExpenseToFormValues(expense, members))
  }, [expense, members, form])

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await updateMutation.mutateAsync({
        title: values.title,
        description: values.description || undefined,
        amount: values.amount,
        splitType: values.splitType,
        expenseDate: values.expenseDate,
        note: values.note || undefined,
        participants: buildParticipantPayload(values.participants, values.splitType),
      })
      showToast('Cập nhật khoản chi thành công.', 'success')
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể cập nhật khoản chi.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return { form, onSubmit, isSubmitting: updateMutation.isPending }
}
