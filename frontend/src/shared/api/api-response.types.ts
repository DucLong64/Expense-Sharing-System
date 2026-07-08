export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  code?: string
  timestamp?: string
}

export interface ApiErrorBody {
  success: false
  message?: string
  code?: string
}
