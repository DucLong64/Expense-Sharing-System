import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react'
import { Button } from '@/shared/components/button'

export interface ConfirmOptions {
  title: string
  description?: string
  confirmLabel?: string
  cancelLabel?: string
  tone?: 'default' | 'danger'
}

interface ConfirmState extends ConfirmOptions {
  open: boolean
}

interface ConfirmContextValue {
  confirm: (options: ConfirmOptions) => Promise<boolean>
}

const ConfirmContext = createContext<ConfirmContextValue | null>(null)

const defaultState: ConfirmState = {
  open: false,
  title: '',
  tone: 'default',
}

export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<ConfirmState>(defaultState)
  const resolveRef = useRef<((value: boolean) => void) | null>(null)

  const confirm = useCallback((options: ConfirmOptions) => {
    return new Promise<boolean>((resolve) => {
      resolveRef.current = resolve
      setState({ ...options, open: true, tone: options.tone ?? 'default' })
    })
  }, [])

  function close(result: boolean) {
    resolveRef.current?.(result)
    resolveRef.current = null
    setState(defaultState)
  }

  const value = useMemo(() => ({ confirm }), [confirm])

  return (
    <ConfirmContext.Provider value={value}>
      {children}
      {state.open ? (
        <div className="fixed inset-0 z-[60] flex items-center justify-center p-4">
          <button
            type="button"
            aria-label="Đóng"
            className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
            onClick={() => close(false)}
          />
          <div
            role="alertdialog"
            aria-modal="true"
            aria-labelledby="confirm-dialog-title"
            aria-describedby={state.description ? 'confirm-dialog-description' : undefined}
            className="relative w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-[var(--shadow-card)]"
          >
            <h2 id="confirm-dialog-title" className="text-lg font-semibold text-slate-900">
              {state.title}
            </h2>
            {state.description ? (
              <p id="confirm-dialog-description" className="mt-2 text-sm leading-relaxed text-slate-600">
                {state.description}
              </p>
            ) : null}
            <div className="mt-6 flex gap-3">
              <Button
                type="button"
                variant="secondary"
                className="flex-1"
                onClick={() => close(false)}
              >
                {state.cancelLabel ?? 'Hủy'}
              </Button>
              <Button
                type="button"
                variant={state.tone === 'danger' ? 'danger' : 'primary'}
                className="flex-1"
                onClick={() => close(true)}
              >
                {state.confirmLabel ?? 'Xác nhận'}
              </Button>
            </div>
          </div>
        </div>
      ) : null}
    </ConfirmContext.Provider>
  )
}

export function useConfirm(): ConfirmContextValue {
  const context = useContext(ConfirmContext)
  if (!context) {
    throw new Error('useConfirm must be used within ConfirmProvider')
  }
  return context
}
