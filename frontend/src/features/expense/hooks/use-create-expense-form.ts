import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { useCreateExpense } from '@/features/expense/api/expense.query'
import {
  createExpenseSchema,
  type CreateExpenseFormValues,
} from '@/features/expense/schemas/expense.schema'
import {
  buildParticipantPayload,
  createDefaultParticipants,
} from '@/features/expense/utils/expense-participants'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { ApiError } from '@/shared/api/api-error'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { useToast } from '@/shared/hooks/use-toast'

export function useCreateExpenseForm(
  houseId: string,
  members: HouseMemberResponse[],
  onSuccess?: () => void,
) {
  const { showToast } = useToast()
  const createExpenseMutation = useCreateExpense(houseId)
  const currentUserId = getCurrentUserId()
  const defaultPaidBy = currentUserId ?? members[0]?.userId ?? ''

  const form = useForm<CreateExpenseFormValues>({
    resolver: zodResolver(createExpenseSchema),
    defaultValues: {
      title: '',
      description: '',
      amount: 0,
      paidBy: defaultPaidBy,
      splitType: 'EQUAL',
      expenseDate: new Date().toISOString().slice(0, 10),
      note: '',
      participants: createDefaultParticipants(members.map((member) => member.userId)),
    },
  })

  useEffect(() => {
    if (members.length === 0) {
      return
    }

    form.setValue(
      'participants',
      createDefaultParticipants(members.map((member) => member.userId)),
    )

    const paidBy = form.getValues('paidBy')
    if (!paidBy) {
      form.setValue('paidBy', members[0].userId)
    }
  }, [members, form])

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createExpenseMutation.mutateAsync({
        title: values.title,
        description: values.description || undefined,
        amount: values.amount,
        paidBy: values.paidBy,
        splitType: values.splitType,
        expenseDate: values.expenseDate,
        note: values.note || undefined,
        participants: buildParticipantPayload(values.participants, values.splitType),
      })
      form.reset({
        title: '',
        description: '',
        amount: 0,
        paidBy: values.paidBy,
        splitType: 'EQUAL',
        expenseDate: new Date().toISOString().slice(0, 10),
        note: '',
        participants: createDefaultParticipants(members.map((member) => member.userId)),
      })
      showToast('Thêm khoản chi thành công.', 'success')
      onSuccess?.()
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể tạo khoản chi.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return {
    form,
    onSubmit,
    isSubmitting: createExpenseMutation.isPending,
  }
}
