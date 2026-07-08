import { Link } from 'react-router-dom'
import { useRegisterForm } from '@/features/auth/hooks/use-register-form'
import { AuthLayout } from '@/shared/components/auth-layout'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'

export function RegisterPage() {
  const { form, onSubmit, isSubmitting } = useRegisterForm()
  const { register, formState } = form

  return (
    <AuthLayout
      title="Đăng ký"
      subtitle="Tạo tài khoản để bắt đầu quản lý chi tiêu nhóm."
      footer={
        <>
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-semibold text-emerald-600 hover:text-emerald-700">
            Đăng nhập
          </Link>
        </>
      }
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <Input
          label="Họ và tên"
          autoComplete="name"
          error={formState.errors.fullName?.message}
          {...register('fullName')}
        />
        <Input
          label="Username"
          autoComplete="username"
          hint="3-30 ký tự, chỉ gồm chữ, số, _ và ."
          error={formState.errors.username?.message}
          {...register('username')}
        />
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          error={formState.errors.email?.message}
          {...register('email')}
        />
        <Input
          label="Mật khẩu"
          type="password"
          autoComplete="new-password"
          hint="Mật khẩu tối thiểu 8 ký tự."
          error={formState.errors.password?.message}
          {...register('password')}
        />
        <ErrorMessage message={formState.errors.root?.message} />
        <Button type="submit" loading={isSubmitting}>
          Tạo tài khoản
        </Button>
      </form>
    </AuthLayout>
  )
}
