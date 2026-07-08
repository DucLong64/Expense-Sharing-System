import { useEditExpenseForm } from '@/features/expense/hooks/use-edit-expense-form'
import { ExpenseFormFields } from '@/features/expense/components/expense-form-fields'
import type { ExpenseResponse } from '@/features/expense/types/expense.types'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { ErrorMessage } from '@/shared/components/error-message'
import { Modal, ModalActions } from '@/shared/components/modal'

interface EditExpenseModalProps {
  open: boolean
  houseId: string
  expense: ExpenseResponse
  members: HouseMemberResponse[]
  onClose: () => void
}

export function EditExpenseModal({
  open,
  houseId,
  expense,
  members,
  onClose,
}: EditExpenseModalProps) {
  const { form, onSubmit, isSubmitting } = useEditExpenseForm(houseId, expense, members, onClose)

  return (
    <Modal open={open} onClose={onClose} title="Chỉnh sửa khoản chi" description={expense.title}>
      <form className="space-y-4" onSubmit={onSubmit}>
        <ExpenseFormFields form={form} members={members} showPaidBy={false} />
        <ErrorMessage message={form.formState.errors.root?.message} />
        <ModalActions onCancel={onClose} submitLabel="Lưu" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
