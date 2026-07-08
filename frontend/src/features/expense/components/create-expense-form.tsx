import { useCreateExpenseForm } from '@/features/expense/hooks/use-create-expense-form'
import { ExpenseFormFields } from '@/features/expense/components/expense-form-fields'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'

interface CreateExpenseFormProps {
  houseId: string
  members: HouseMemberResponse[]
}

export function CreateExpenseForm({ houseId, members }: CreateExpenseFormProps) {
  const { form, onSubmit, isSubmitting } = useCreateExpenseForm(houseId, members)

  return (
    <Card title="Thêm khoản chi" description="Ghi nhận chi phí mới và phân chia cho thành viên">
      <form className="space-y-4" onSubmit={onSubmit}>
        <ExpenseFormFields form={form} members={members} />
        <ErrorMessage message={form.formState.errors.root?.message} />
        <Button type="submit" loading={isSubmitting}>
          Thêm khoản chi
        </Button>
      </form>
    </Card>
  )
}
