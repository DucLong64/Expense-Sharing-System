import { HouseSettingsSection } from '@/features/house/components/house-settings-section'
import { Modal } from '@/shared/components/modal'

interface HouseSettingsModalProps {
  open: boolean
  houseId: string
  houseName?: string
  onEdit: () => void
  onClose: () => void
}

export function HouseSettingsModal({
  open,
  houseId,
  houseName,
  onEdit,
  onClose,
}: HouseSettingsModalProps) {
  return (
    <Modal open={open} onClose={onClose} title="Cài đặt nhóm" description={houseName}>
      <HouseSettingsSection
        houseId={houseId}
        houseName={houseName}
        onEdit={() => {
          onClose()
          onEdit()
        }}
      />
    </Modal>
  )
}
