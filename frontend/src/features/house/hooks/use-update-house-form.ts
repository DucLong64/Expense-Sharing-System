import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { useUpdateHouse } from '@/features/house/api/house.query'
import {
  updateHouseSchema,
  type UpdateHouseFormValues,
} from '@/features/house/schemas/house.schema'
import type { HouseResponse } from '@/features/house/types/house.types'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useUpdateHouseForm(house: HouseResponse, onSuccess?: () => void) {
  const { showToast } = useToast()
  const updateMutation = useUpdateHouse(house.id, onSuccess)

  const form = useForm<UpdateHouseFormValues>({
    resolver: zodResolver(updateHouseSchema),
    defaultValues: {
      name: house.name,
      description: house.description ?? '',
    },
  })

  useEffect(() => {
    form.reset({
      name: house.name,
      description: house.description ?? '',
    })
  }, [house, form])

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await updateMutation.mutateAsync({
        name: values.name,
        description: values.description || undefined,
      })
      showToast('Cập nhật nhóm thành công.', 'success')
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Không thể cập nhật nhóm.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return { form, onSubmit, isSubmitting: updateMutation.isPending }
}
