import { useInviteMemberForm } from '@/features/house/hooks/use-invite-member-form'
import { assignableRoles, roleLabels } from '@/features/house/utils/house-permissions'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { Modal, ModalActions } from '@/shared/components/modal'
import { Select } from '@/shared/components/select'

interface InviteMemberModalProps {
  open: boolean
  houseId: string
  onClose: () => void
}

export function InviteMemberModal({ open, houseId, onClose }: InviteMemberModalProps) {
  const { form, onSubmit, isSubmitting } = useInviteMemberForm(houseId, onClose)
  const { register, formState } = form

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Mời thành viên"
      description="Nhập username hoặc email chính xác của người cần mời."
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <Input
          label="Username hoặc email"
          placeholder="vd: long_dev hoặc long@email.com"
          error={formState.errors.identifier?.message}
          {...register('identifier')}
        />
        <Select
          label="Vai trò"
          error={formState.errors.role?.message}
          options={assignableRoles.map((role) => ({
            value: role,
            label: roleLabels[role],
          }))}
          {...register('role')}
        />
        <ErrorMessage message={formState.errors.root?.message} />
        <ModalActions onCancel={onClose} submitLabel="Mời" loading={isSubmitting} />
      </form>
    </Modal>
  )
}
