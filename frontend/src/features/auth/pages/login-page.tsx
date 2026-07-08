import { Link } from 'react-router-dom'
import { useLoginForm } from '@/features/auth/hooks/use-login-form'
import { AuthLayout } from '@/shared/components/auth-layout'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'

export function LoginPage() {
  const { form, onSubmit, isSubmitting } = useLoginForm()
  const { register, formState } = form

  return (
    <AuthLayout
      title="Đăng nhập"
      subtitle="Quản lý chi tiêu chung cùng nhóm của bạn."
      footer={
        <>
          Chưa có tài khoản?{' '}
          <Link to="/register" className="font-semibold text-emerald-600 hover:text-emerald-700">
            Đăng ký ngay
          </Link>
        </>
      }
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <Input
          label="Username"
          autoComplete="username"
          error={formState.errors.username?.message}
          {...register('username')}
        />
        <Input
          label="Mật khẩu"
          type="password"
          autoComplete="current-password"
          error={formState.errors.password?.message}
          {...register('password')}
        />
        <ErrorMessage message={formState.errors.root?.message} />
        <Button type="submit" loading={isSubmitting}>
          Đăng nhập
        </Button>
      </form>
    </AuthLayout>
  )
}
