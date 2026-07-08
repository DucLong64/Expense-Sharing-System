import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { loginSchema, type LoginFormValues } from '@/features/auth/schemas/auth.schema'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useLoginForm() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const { showToast } = useToast()

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { username: '', password: '' },
  })

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await login(values)
      navigate('/', { replace: true })
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Không thể đăng nhập. Vui lòng thử lại.'
      form.setError('root', { message })
      showToast(message)
    }
  })

  return { form, onSubmit, isSubmitting: form.formState.isSubmitting }
}
