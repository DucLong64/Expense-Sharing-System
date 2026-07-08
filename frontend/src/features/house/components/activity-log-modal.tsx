import { ActivitySection } from '@/features/activity/components/activity-section'

interface ActivityLogModalProps {
  open: boolean
  houseId: string
  onClose: () => void
}

export function ActivityLogModal({ open, houseId, onClose }: ActivityLogModalProps) {
  if (!open) {
    return null
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <button
        type="button"
        aria-label="Đóng"
        className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
        onClick={onClose}
      />
      <div className="relative max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl border border-slate-200 bg-white p-6 shadow-[var(--shadow-card)]">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 className="text-lg font-semibold text-slate-900">Nhật ký hoạt động</h2>
            <p className="mt-1 text-sm text-slate-500">Theo dõi mọi thay đổi trong nhóm</p>
          </div>
          <button
            type="button"
            className="rounded-lg px-3 py-1.5 text-sm font-medium text-slate-600 hover:bg-slate-100"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
        <ActivitySection houseId={houseId} embedded />
      </div>
    </div>
  )
}
