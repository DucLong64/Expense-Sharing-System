import { useCreateExpenseForm } from '@/features/expense/hooks/use-create-expense-form'
import { ExpenseFormFields } from '@/features/expense/components/expense-form-fields'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { ErrorMessage } from '@/shared/components/error-message'
import { Modal, ModalActions } from '@/shared/components/modal'

interface CreateExpenseModalProps {
  open: boolean
  houseId: string
  members: HouseMemberResponse[]
  onClose: () => void
}

export function CreateExpenseModal({ open, houseId, members, onClose }: CreateExpenseModalProps) {
  const { form, onSubmit, isSubmitting } = useCreateExpenseForm(houseId, members, onClose)

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Thêm khoản chi"
      description="Ghi nhận chi phí mới và phân chia cho thành viên"
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <ExpenseFormFields form={form} members={members} />
        <ErrorMessage message={form.formState.errors.root?.message} />
        <ModalActions onCancel={onClose} submitLabel="Thêm khoản chi" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
