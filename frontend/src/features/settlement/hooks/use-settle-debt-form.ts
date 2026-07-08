import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { useDebts, useSettleDebt } from '@/features/settlement/api/settlement.query'
import {
  settleDebtSchema,
  type SettleDebtFormValues,
} from '@/features/settlement/schemas/settlement.schema'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useSettleDebtForm(houseId: string, prefillDebt?: DebtSummaryResponse | null) {
  const { showToast } = useToast()
  const { data: debts = [] } = useDebts(houseId)
  const settleMutation = useSettleDebt(houseId)

  const form = useForm<SettleDebtFormValues>({
    resolver: zodResolver(settleDebtSchema),
    defaultValues: { toUserId: '', amount: 0, note: '' },
  })

  useEffect(() => {
    if (!prefillDebt) {
      return
    }
    form.setValue('toUserId', prefillDebt.toUserId)
    form.setValue('amount', prefillDebt.amount)
  }, [prefillDebt, form])

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await settleMutation.mutateAsync({
        toUserId: values.toUserId,
        amount: values.amount,
        note: values.note || undefined,
      })
      form.reset({ toUserId: '', amount: 0, note: '' })
      showToast('Ghi nhận thanh toán thành công.', 'success')
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể ghi nhận thanh toán.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  const creditorOptions = Array.from(
    debts.reduce((map, debt) => {
      if (!map.has(debt.toUserId)) {
        map.set(debt.toUserId, { userId: debt.toUserId, username: debt.toUsername })
      }
      return map
    }, new Map<string, { userId: string; username: string }>()),
  ).map(([, creditor]) => creditor)

  return { form, onSubmit, isSubmitting: settleMutation.isPending, creditorOptions }
}
