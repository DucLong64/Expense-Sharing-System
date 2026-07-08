import { useState } from 'react'
import { useHouseMembers } from '@/features/house/api/house.query'
import { CreateExpenseModal } from '@/features/expense/components/create-expense-modal'
import { ExpenseList } from '@/features/expense/components/expense-list'
import { Button } from '@/shared/components/button'
import { PlusIcon } from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'

interface ExpenseSectionProps {
  houseId: string
}

export function ExpenseSection({ houseId }: ExpenseSectionProps) {
  const [openCreateModal, setOpenCreateModal] = useState(false)
  const { data: members = [], isLoading, error } = useHouseMembers(houseId)

  if (isLoading) {
    return <LoadingState message="Đang tải thành viên..." />
  }

  if (error) {
    return <ErrorMessage message="Không thể tải danh sách thành viên." />
  }

  return (
    <>
      <ExpenseList
        houseId={houseId}
        action={
          <Button className="w-auto" size="sm" onClick={() => setOpenCreateModal(true)}>
            <PlusIcon className="h-4 w-4" />
            Thêm khoản chi
          </Button>
        }
      />
      <CreateExpenseModal
        open={openCreateModal}
        houseId={houseId}
        members={members}
        onClose={() => setOpenCreateModal(false)}
      />
    </>
  )
}
