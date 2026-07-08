import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect, useMemo } from 'react'
import { useForm } from 'react-hook-form'
import { useDebts, useSettleDebt } from '@/features/settlement/api/settlement.query'
import {
  createSettleDebtSchema,
  type SettleDebtFormValues,
} from '@/features/settlement/schemas/settlement.schema'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { ApiError } from '@/shared/api/api-error'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { useToast } from '@/shared/hooks/use-toast'

export function useSettleDebtForm(
  houseId: string,
  prefillDebt?: DebtSummaryResponse | null,
  onSuccess?: () => void,
) {
  const { showToast } = useToast()
  const currentUserId = getCurrentUserId()
  const { data: debts = [] } = useDebts(houseId)
  const settleMutation = useSettleDebt(houseId)

  const myDebts = useMemo(
    () => debts.filter((debt) => debt.fromUserId === currentUserId),
    [debts, currentUserId],
  )

  const schema = useMemo(() => createSettleDebtSchema(myDebts), [myDebts])

  const form = useForm<SettleDebtFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { toUserId: '', amount: 0, note: '' },
  })

  useEffect(() => {
    if (!prefillDebt) {
      form.reset({ toUserId: '', amount: 0, note: '' })
      return
    }
    form.reset({
      toUserId: prefillDebt.toUserId,
      amount: prefillDebt.amount,
      note: '',
    })
  }, [prefillDebt, form])

  const selectedToUserId = form.watch('toUserId')
  const maxPayableAmount = myDebts.find((debt) => debt.toUserId === selectedToUserId)?.amount

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await settleMutation.mutateAsync({
        toUserId: values.toUserId,
        amount: Math.round(values.amount * 100) / 100,
        note: values.note || undefined,
      })
      form.reset({ toUserId: '', amount: 0, note: '' })
      showToast('Ghi nhận thanh toán thành công.', 'success')
      onSuccess?.()
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể ghi nhận thanh toán.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  const creditorOptions = myDebts.map((debt) => ({
    userId: debt.toUserId,
    username: debt.toUsername,
    maxAmount: debt.amount,
  }))

  return {
    form,
    onSubmit,
    isSubmitting: settleMutation.isPending,
    creditorOptions,
    maxPayableAmount,
    hasPayableDebts: myDebts.length > 0,
  }
}
