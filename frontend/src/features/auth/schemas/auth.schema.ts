import { z } from 'zod'

const usernameSchema = z
  .string()
  .trim()
  .min(3, 'Username tối thiểu 3 ký tự')
  .max(30, 'Username tối đa 30 ký tự')
  .regex(/^[a-zA-Z0-9_.]+$/, 'Username chỉ gồm chữ, số, _ và .')
  .transform((value) => value.toLowerCase())

export const loginSchema = z.object({
  username: usernameSchema,
  password: z.string().min(1, 'Mật khẩu là bắt buộc'),
})

export type LoginFormValues = z.infer<typeof loginSchema>

export const registerSchema = z.object({
  username: usernameSchema,
  fullName: z.string().trim().min(1, 'Họ và tên là bắt buộc').max(100, 'Họ và tên tối đa 100 ký tự'),
  email: z.string().trim().email('Email không hợp lệ'),
  password: z.string().min(8, 'Mật khẩu tối thiểu 8 ký tự'),
})

export type RegisterFormValues = z.infer<typeof registerSchema>

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Mật khẩu hiện tại là bắt buộc'),
    newPassword: z.string().min(8, 'Mật khẩu mới tối thiểu 8 ký tự'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((values) => values.newPassword === values.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  })

export type ChangePasswordFormValues = z.infer<typeof changePasswordSchema>
