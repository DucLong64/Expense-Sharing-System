import { z } from 'zod'

export const createHouseSchema = z.object({
  name: z.string().min(1, 'Tên nhóm là bắt buộc').max(100, 'Tên nhóm tối đa 100 ký tự'),
  description: z.string().max(500, 'Mô tả tối đa 500 ký tự').optional(),
})

export type CreateHouseFormValues = z.infer<typeof createHouseSchema>

export const updateHouseSchema = createHouseSchema

export type UpdateHouseFormValues = z.infer<typeof updateHouseSchema>

export const inviteMemberSchema = z.object({
  identifier: z.string().trim().min(1, 'Nhập username hoặc email'),
  role: z.enum(['ADMIN', 'MEMBER', 'VIEWER']),
})

export type InviteMemberFormValues = z.infer<typeof inviteMemberSchema>

export const changeMemberRoleSchema = z.object({
  role: z.enum(['ADMIN', 'MEMBER', 'VIEWER']),
})

export type ChangeMemberRoleFormValues = z.infer<typeof changeMemberRoleSchema>
