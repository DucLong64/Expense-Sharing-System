export interface AuthResponse {
  accessToken: string
  refreshToken: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  fullName: string
}

export interface UserResponse {
  id: string
  username: string
  email: string
  fullName: string
  createdAt: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}
