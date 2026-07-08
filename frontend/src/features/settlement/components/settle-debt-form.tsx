import { useSettleDebtForm } from '@/features/settlement/hooks/use-settle-debt-form'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Select } from '@/shared/components/select'
import { displayUsername } from '@/shared/utils/format'

interface SettleDebtFormProps {
  houseId: string
  prefillDebt?: DebtSummaryResponse | null
}

export function SettleDebtForm({ houseId, prefillDebt }: SettleDebtFormProps) {
  const { form, onSubmit, isSubmitting, creditorOptions } = useSettleDebtForm(houseId, prefillDebt)
  const { register, formState } = form

  return (
    <Card title="Ghi nhận thanh toán" description="Xác nhận khi bạn đã trả nợ cho ai đó">
      <form className="space-y-4" onSubmit={onSubmit}>
        <Select
          label="Người nhận"
          placeholder="Chọn người nhận"
          error={formState.errors.toUserId?.message}
          options={creditorOptions.map((creditor) => ({
            value: creditor.userId,
            label: displayUsername(creditor.username, creditor.userId),
          }))}
          {...register('toUserId')}
        />
        <Input
          label="Số tiền"
          type="number"
          min="1"
          error={formState.errors.amount?.message}
          {...register('amount', { valueAsNumber: true })}
        />
        <Input label="Ghi chú" error={formState.errors.note?.message} {...register('note')} />
        <ErrorMessage message={formState.errors.root?.message} />
        <Button type="submit" loading={isSubmitting}>
          Ghi nhận thanh toán
        </Button>
      </form>
    </Card>
  )
}
