import { useChangePasswordForm } from '@/features/auth/hooks/use-change-password-form'
import { useCurrentUser } from '@/features/auth/api/auth.query'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'
import { Input } from '@/shared/components/input'
import { LoadingState } from '@/shared/components/loading-state'
import { formatDateTime } from '@/shared/utils/format'

export function ProfilePage() {
  const { data: user, isLoading, error, refetch } = useCurrentUser()
  const { form, onSubmit, isSubmitting } = useChangePasswordForm()
  const { register, formState } = form

  if (isLoading) {
    return (
      <AppShell title="Tài khoản" subtitle="Quản lý thông tin cá nhân và bảo mật.">
        <LoadingState message="Đang tải thông tin tài khoản..." />
      </AppShell>
    )
  }

  if (error || !user) {
    return (
      <AppShell title="Tài khoản" subtitle="Quản lý thông tin cá nhân và bảo mật.">
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải thông tin tài khoản." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      </AppShell>
    )
  }

  return (
    <AppShell title="Tài khoản" subtitle="Quản lý thông tin cá nhân và bảo mật.">
      <div className="grid gap-6 lg:grid-cols-2">
        <Card title="Thông tin cá nhân" description="Username không thể thay đổi sau khi đăng ký.">
          <dl className="space-y-4 text-sm">
            <div className="rounded-xl bg-slate-50 px-4 py-3">
              <dt className="text-slate-500">Username</dt>
              <dd className="mt-1 font-semibold text-slate-900">{user.username}</dd>
            </div>
            <div className="rounded-xl bg-slate-50 px-4 py-3">
              <dt className="text-slate-500">Họ và tên</dt>
              <dd className="mt-1 font-semibold text-slate-900">{user.fullName}</dd>
            </div>
            <div className="rounded-xl bg-slate-50 px-4 py-3">
              <dt className="text-slate-500">Email</dt>
              <dd className="mt-1 font-semibold text-slate-900">{user.email}</dd>
            </div>
            <div className="rounded-xl bg-slate-50 px-4 py-3">
              <dt className="text-slate-500">Ngày tạo</dt>
              <dd className="mt-1 font-semibold text-slate-900">{formatDateTime(user.createdAt)}</dd>
            </div>
          </dl>
        </Card>

        <Card title="Đổi mật khẩu" description="Nhập mật khẩu hiện tại và mật khẩu mới.">
          <form className="space-y-4" onSubmit={onSubmit}>
            <Input
              label="Mật khẩu hiện tại"
              type="password"
              autoComplete="current-password"
              error={formState.errors.currentPassword?.message}
              {...register('currentPassword')}
            />
            <Input
              label="Mật khẩu mới"
              type="password"
              autoComplete="new-password"
              error={formState.errors.newPassword?.message}
              {...register('newPassword')}
            />
            <Input
              label="Xác nhận mật khẩu mới"
              type="password"
              autoComplete="new-password"
              error={formState.errors.confirmPassword?.message}
              {...register('confirmPassword')}
            />
            <ErrorMessage message={formState.errors.root?.message} />
            <Button type="submit" loading={isSubmitting}>
              Cập nhật mật khẩu
            </Button>
          </form>
        </Card>
      </div>
    </AppShell>
  )
}
