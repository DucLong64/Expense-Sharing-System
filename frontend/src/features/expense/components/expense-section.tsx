import { useHouseMembers } from '@/features/house/api/house.query'
import { CreateExpenseForm } from '@/features/expense/components/create-expense-form'
import { ExpenseList } from '@/features/expense/components/expense-list'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'

interface ExpenseSectionProps {
  houseId: string
}

export function ExpenseSection({ houseId }: ExpenseSectionProps) {
  const { data: members = [], isLoading, error } = useHouseMembers(houseId)

  if (isLoading) {
    return <LoadingState message="Đang tải thành viên..." />
  }

  if (error) {
    return <ErrorMessage message="Không thể tải danh sách thành viên." />
  }

  return (
    <div className="space-y-6">
      <CreateExpenseForm houseId={houseId} members={members} />
      <ExpenseList houseId={houseId} />
    </div>
  )
}
