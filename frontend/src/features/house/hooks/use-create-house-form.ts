import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { createHouseSchema, type CreateHouseFormValues } from '@/features/house/schemas/house.schema'
import { useCreateHouse } from '@/features/house/api/house.query'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useCreateHouseForm(onSuccess: () => void) {
  const { showToast } = useToast()
  const createHouseMutation = useCreateHouse(onSuccess)

  const form = useForm<CreateHouseFormValues>({
    resolver: zodResolver(createHouseSchema),
    defaultValues: { name: '', description: '' },
  })

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createHouseMutation.mutateAsync({
        name: values.name,
        description: values.description || undefined,
      })
      form.reset()
      showToast('Tạo nhóm thành công.', 'success')
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể tạo nhóm.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return {
    form,
    onSubmit,
    isSubmitting: createHouseMutation.isPending,
  }
}
