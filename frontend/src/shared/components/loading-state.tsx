interface LoadingStateProps {
  message?: string
}

export function LoadingState({ message = 'Đang tải...' }: LoadingStateProps) {
  return (
    <div className="flex items-center gap-3 py-6">
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-emerald-600 border-t-transparent" />
      <p className="text-sm text-slate-600">{message}</p>
    </div>
  )
}
