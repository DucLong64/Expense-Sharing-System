import { useUpdateHouseForm } from '@/features/house/hooks/use-update-house-form'
import type { HouseResponse } from '@/features/house/types/house.types'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Modal, ModalActions } from '@/shared/components/modal'

interface EditHouseModalProps {
  open: boolean
  house: HouseResponse
  onClose: () => void
}

export function EditHouseModal({ open, house, onClose }: EditHouseModalProps) {
  const { form, onSubmit, isSubmitting } = useUpdateHouseForm(house, onClose)
  const { register, formState } = form

  return (
    <Modal open={open} onClose={onClose} title="Chỉnh sửa nhóm" description="Cập nhật tên và mô tả nhóm.">
      <form className="space-y-4" onSubmit={onSubmit}>
        <Input label="Tên nhóm" error={formState.errors.name?.message} {...register('name')} />
        <Input
          label="Mô tả"
          error={formState.errors.description?.message}
          {...register('description')}
        />
        <ErrorMessage message={formState.errors.root?.message} />
        <ModalActions onCancel={onClose} submitLabel="Lưu" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
