import { useSettleDebtForm } from '@/features/settlement/hooks/use-settle-debt-form'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Modal, ModalActions } from '@/shared/components/modal'
import { Select } from '@/shared/components/select'
import { displayUsername, formatCurrency } from '@/shared/utils/format'

interface SettleDebtModalProps {
  open: boolean
  houseId: string
  prefillDebt?: DebtSummaryResponse | null
  onClose: () => void
}

export function SettleDebtModal({ open, houseId, prefillDebt, onClose }: SettleDebtModalProps) {
  const { form, onSubmit, isSubmitting, creditorOptions } = useSettleDebtForm(
    houseId,
    open ? prefillDebt : null,
    onClose,
  )
  const { register, formState } = form

  const description = prefillDebt
    ? `Thanh toán cho ${displayUsername(prefillDebt.toUsername, prefillDebt.toUserId)} · ${formatCurrency(prefillDebt.amount)}`
    : 'Xác nhận khi bạn đã trả nợ cho ai đó'

  return (
    <Modal open={open} onClose={onClose} title="Ghi nhận thanh toán" description={description}>
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
        <ModalActions onCancel={onClose} submitLabel="Ghi nhận thanh toán" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
