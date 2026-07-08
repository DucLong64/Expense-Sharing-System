import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { useInviteMember } from '@/features/house/api/house.query'
import {
  inviteMemberSchema,
  type InviteMemberFormValues,
} from '@/features/house/schemas/house.schema'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useInviteMemberForm(houseId: string, onSuccess?: () => void) {
  const { showToast } = useToast()
  const inviteMutation = useInviteMember(houseId, onSuccess)

  const form = useForm<InviteMemberFormValues>({
    resolver: zodResolver(inviteMemberSchema),
    defaultValues: { identifier: '', role: 'MEMBER' },
  })

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await inviteMutation.mutateAsync(values)
      form.reset({ identifier: '', role: 'MEMBER' })
      showToast('Mời thành viên thành công.', 'success')
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Không thể mời thành viên.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return { form, onSubmit, isSubmitting: inviteMutation.isPending }
}
