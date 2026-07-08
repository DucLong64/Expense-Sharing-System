import { useCreateHouseForm } from '@/features/house/hooks/use-create-house-form'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Modal, ModalActions } from '@/shared/components/modal'

interface CreateHouseModalProps {
  open: boolean
  onClose: () => void
}

export function CreateHouseModal({ open, onClose }: CreateHouseModalProps) {
  const { form, onSubmit, isSubmitting } = useCreateHouseForm(onClose)
  const { register, formState } = form

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Tạo nhóm mới"
      description="Tạo nhóm để bắt đầu theo dõi chi tiêu chung."
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <Input label="Tên nhóm" error={formState.errors.name?.message} {...register('name')} />
        <Input
          label="Mô tả"
          error={formState.errors.description?.message}
          {...register('description')}
        />
        <ErrorMessage message={formState.errors.root?.message} />
        <ModalActions onCancel={onClose} submitLabel="Tạo nhóm" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
