import type { ReactNode } from 'react'
import { Button } from '@/shared/components/button'

interface ModalProps {
  open: boolean
  onClose: () => void
  title: string
  description?: string
  children: ReactNode
}

export function Modal({ open, onClose, title, description, children }: ModalProps) {
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
      <div className="relative w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-[var(--shadow-card)]">
        <div className="mb-5">
          <h2 className="text-lg font-semibold text-slate-900">{title}</h2>
          {description ? <p className="mt-1 text-sm text-slate-500">{description}</p> : null}
        </div>
        {children}
      </div>
    </div>
  )
}

interface ModalActionsProps {
  onCancel: () => void
  submitLabel: string
  loading?: boolean
  cancelLabel?: string
}

export function ModalActions({
  onCancel,
  submitLabel,
  loading,
  cancelLabel = 'Hủy',
}: ModalActionsProps) {
  return (
    <div className="flex gap-3 pt-2">
      <Button type="button" variant="secondary" className="flex-1" onClick={onCancel}>
        {cancelLabel}
      </Button>
      <Button type="submit" className="flex-1" loading={loading}>
        {submitLabel}
      </Button>
    </div>
  )
}
