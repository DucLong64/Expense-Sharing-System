import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect, useMemo } from 'react'
import { useForm } from 'react-hook-form'
import { useConfirmDebtReceived, useDebts } from '@/features/settlement/api/settlement.query'
import {
  createConfirmDebtReceivedSchema,
  type ConfirmDebtReceivedFormValues,
} from '@/features/settlement/schemas/settlement.schema'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { ApiError } from '@/shared/api/api-error'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { useToast } from '@/shared/hooks/use-toast'

export function useConfirmDebtReceivedForm(
  houseId: string,
  prefillDebt?: DebtSummaryResponse | null,
  onSuccess?: () => void,
) {
  const { showToast } = useToast()
  const currentUserId = getCurrentUserId()
  const { data: debts = [] } = useDebts(houseId)
  const confirmMutation = useConfirmDebtReceived(houseId)

  const owedToMe = useMemo(
    () => debts.filter((debt) => debt.toUserId === currentUserId),
    [debts, currentUserId],
  )

  const schema = useMemo(() => createConfirmDebtReceivedSchema(owedToMe), [owedToMe])

  const form = useForm<ConfirmDebtReceivedFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { fromUserId: '', amount: 0, note: '' },
  })

  useEffect(() => {
    if (!prefillDebt) {
      form.reset({ fromUserId: '', amount: 0, note: '' })
      return
    }
    form.reset({
      fromUserId: prefillDebt.fromUserId,
      amount: prefillDebt.amount,
      note: '',
    })
  }, [prefillDebt, form])

  const selectedFromUserId = form.watch('fromUserId')
  const maxReceivableAmount = owedToMe.find((debt) => debt.fromUserId === selectedFromUserId)?.amount

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await confirmMutation.mutateAsync({
        fromUserId: values.fromUserId,
        amount: Math.round(values.amount * 100) / 100,
        note: values.note || undefined,
      })
      form.reset({ fromUserId: '', amount: 0, note: '' })
      showToast('Xác nhận đã nhận thành công.', 'success')
      onSuccess?.()
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể xác nhận đã nhận.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  const debtorOptions = owedToMe.map((debt) => ({
    userId: debt.fromUserId,
    username: debt.fromUsername,
    maxAmount: debt.amount,
  }))

  return {
    form,
    onSubmit,
    isSubmitting: confirmMutation.isPending,
    debtorOptions,
    maxReceivableAmount,
    hasReceivableDebts: owedToMe.length > 0,
  }
}
