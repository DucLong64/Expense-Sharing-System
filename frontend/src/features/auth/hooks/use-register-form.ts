import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { registerSchema, type RegisterFormValues } from '@/features/auth/schemas/auth.schema'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useRegisterForm() {
  const navigate = useNavigate()
  const { register: registerUser } = useAuth()
  const { showToast } = useToast()

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { username: '', fullName: '', email: '', password: '' },
  })

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await registerUser(values)
      navigate('/', { replace: true })
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể đăng ký. Vui lòng thử lại.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return { form, onSubmit, isSubmitting: form.formState.isSubmitting }
}
