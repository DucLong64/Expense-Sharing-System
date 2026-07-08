import { useConfirmDebtReceivedForm } from '@/features/settlement/hooks/use-confirm-debt-received-form'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Modal, ModalActions } from '@/shared/components/modal'
import { Select } from '@/shared/components/select'
import { displayUsername, formatCurrency } from '@/shared/utils/format'

interface ConfirmDebtReceivedModalProps {
  open: boolean
  houseId: string
  prefillDebt?: DebtSummaryResponse | null
  onClose: () => void
}

export function ConfirmDebtReceivedModal({
  open,
  houseId,
  prefillDebt,
  onClose,
}: ConfirmDebtReceivedModalProps) {
  const { form, onSubmit, isSubmitting, debtorOptions, maxReceivableAmount, hasReceivableDebts } =
    useConfirmDebtReceivedForm(houseId, open ? prefillDebt : null, onClose)
  const { register, formState } = form

  const description = prefillDebt
    ? `${displayUsername(prefillDebt.fromUsername, prefillDebt.fromUserId)} trả bạn · ${formatCurrency(prefillDebt.amount)}`
    : 'Xác nhận khi bạn đã nhận tiền từ ai đó'

  return (
    <Modal open={open} onClose={onClose} title="Xác nhận đã nhận" description={description}>
      {!hasReceivableDebts ? (
        <p className="text-sm text-slate-500">Hiện không có ai nợ bạn trong nhóm.</p>
      ) : (
        <form className="space-y-4" onSubmit={onSubmit}>
          <Select
            label="Người trả"
            placeholder="Chọn người trả"
            error={formState.errors.fromUserId?.message}
            options={debtorOptions.map((debtor) => ({
              value: debtor.userId,
              label: `${displayUsername(debtor.username, debtor.userId)} · ${formatCurrency(debtor.maxAmount)}`,
            }))}
            {...register('fromUserId')}
          />
          <Input
            label="Số tiền"
            type="number"
            min="0.01"
            max={maxReceivableAmount}
            step="0.01"
            inputMode="decimal"
            hint={
              maxReceivableAmount !== undefined
                ? `Tối đa ${formatCurrency(maxReceivableAmount)}`
                : undefined
            }
            error={formState.errors.amount?.message}
            {...register('amount', { valueAsNumber: true })}
          />
          <Input label="Ghi chú" error={formState.errors.note?.message} {...register('note')} />
          <ErrorMessage message={formState.errors.root?.message} />
          <ModalActions onCancel={onClose} submitLabel="Xác nhận đã nhận" loading={isSubmitting} />
        </form>
      )}
    </Modal>
  )
}
