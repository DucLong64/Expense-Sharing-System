export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(amount)
}

export function formatDate(date: string): string {
  if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
    const [year, month, day] = date.split('-')
    return `${day}/${month}/${year}`
  }

  return new Intl.DateTimeFormat('vi-VN').format(new Date(date))
}

export function formatDateTime(date: string): string {
  return new Intl.DateTimeFormat('vi-VN', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(date))
}

export function shortenUserId(userId: string): string {
  return userId.slice(0, 8)
}

export function displayUsername(username: string | null | undefined, userId: string): string {
  const normalized = username?.trim()
  return normalized || shortenUserId(userId)
}
