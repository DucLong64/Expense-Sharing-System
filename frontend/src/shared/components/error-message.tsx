interface ErrorMessageProps {
  message?: string | null
}

export function ErrorMessage({ message }: ErrorMessageProps) {
  if (!message) {
    return null
  }

  return (
    <div className="flex items-start gap-2 rounded-xl border border-red-200 bg-red-50 px-3.5 py-2.5 text-sm text-red-700">
      <span className="mt-0.5 shrink-0 text-red-500">!</span>
      <p>{message}</p>
    </div>
  )
}
