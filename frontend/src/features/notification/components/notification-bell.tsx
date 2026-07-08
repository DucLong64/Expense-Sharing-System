import { useEffect, useRef, useState } from 'react'
import { useUnreadNotificationCount } from '@/features/notification/api/notification.query'
import { NotificationPanel } from '@/features/notification/components/notification-panel'

export function NotificationBell() {
  const [open, setOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)
  const { data } = useUnreadNotificationCount()
  const unreadCount = data?.count ?? 0

  useEffect(() => {
    if (!open) {
      return
    }

    function handleClickOutside(event: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }

    function handleEscape(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        setOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    document.addEventListener('keydown', handleEscape)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
      document.removeEventListener('keydown', handleEscape)
    }
  }, [open])

  function togglePanel() {
    setOpen((current) => !current)
  }

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        aria-expanded={open}
        aria-haspopup="dialog"
        onClick={togglePanel}
        className={`relative rounded-lg px-3 py-1.5 text-sm font-medium transition ${
          open
            ? 'bg-slate-100 text-slate-900'
            : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
        }`}
      >
        Thông báo
        {unreadCount > 0 ? (
          <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        ) : null}
      </button>
      <NotificationPanel open={open} onClose={() => setOpen(false)} />
    </div>
  )
}
