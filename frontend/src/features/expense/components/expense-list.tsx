import { useState } from 'react'
import { useHouseMembers } from '@/features/house/api/house.query'
import { useDeleteExpense, useExpenses } from '@/features/expense/api/expense.query'
import { EditExpenseModal } from '@/features/expense/components/edit-expense-modal'
import type { ExpenseResponse } from '@/features/expense/types/expense.types'
import { splitTypeLabels } from '@/features/expense/schemas/expense.schema'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ReceiptIcon } from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { useToast } from '@/shared/hooks/use-toast'
import { formatCurrency, formatDate, displayUsername } from '@/shared/utils/format'
import { ApiError } from '@/shared/api/api-error'

interface ExpenseListProps {
  houseId: string
}

function ExpenseListItem({
  expense,
  houseId,
  members,
  onDelete,
  isDeleting,
}: {
  expense: ExpenseResponse
  houseId: string
  members: ReturnType<typeof useHouseMembers>['data']
  onDelete: (expenseId: string) => void
  isDeleting: boolean
}) {
  const [expanded, setExpanded] = useState(false)
  const [editing, setEditing] = useState(false)

  return (
    <>
      <div className="rounded-xl border border-slate-200/80 bg-slate-50/50 p-4">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex gap-4">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white text-emerald-600 shadow-sm">
              <ReceiptIcon className="h-5 w-5" />
            </span>
            <div>
              <p className="font-semibold text-slate-900">{expense.title}</p>
              <p className="mt-1 text-sm text-slate-500">
                {formatDate(expense.expenseDate)} · {splitTypeLabels[expense.splitType]}
              </p>
              <p className="mt-0.5 text-xs text-slate-400">
                Trả bởi {displayUsername(expense.paidByUsername, expense.paidBy)}
              </p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-2 sm:flex-col sm:items-end">
            <p className="text-lg font-bold text-emerald-700">{formatCurrency(expense.amount)}</p>
            <div className="flex gap-2">
              <Button variant="ghost" size="sm" className="w-auto" onClick={() => setExpanded(!expanded)}>
                {expanded ? 'Thu gọn' : 'Chi tiết'}
              </Button>
              <Button variant="secondary" size="sm" className="w-auto" onClick={() => setEditing(true)}>
                Sửa
              </Button>
              <Button
                variant="danger"
                size="sm"
                className="w-auto"
                loading={isDeleting}
                onClick={() => onDelete(expense.id)}
              >
                Xóa
              </Button>
            </div>
          </div>
        </div>
        {expanded ? (
          <div className="mt-4 space-y-2 border-t border-slate-200 pt-4 text-sm">
            {expense.description ? (
              <p className="text-slate-600">
                <span className="font-medium text-slate-700">Mô tả:</span> {expense.description}
              </p>
            ) : null}
            {expense.note ? (
              <p className="text-slate-600">
                <span className="font-medium text-slate-700">Ghi chú:</span> {expense.note}
              </p>
            ) : null}
            <div>
              <p className="font-medium text-slate-700">Phân chia:</p>
              <ul className="mt-2 space-y-1">
                {expense.participants.map((participant) => (
                  <li key={participant.userId} className="flex justify-between text-slate-600">
                    <span>{displayUsername(participant.username, participant.userId)}</span>
                    <span>
                      {formatCurrency(participant.shareAmount)}
                      {participant.sharePercentage
                        ? ` (${participant.sharePercentage}%)`
                        : null}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        ) : null}
      </div>

      {editing && members ? (
        <EditExpenseModal
          open={editing}
          houseId={houseId}
          expense={expense}
          members={members}
          onClose={() => setEditing(false)}
        />
      ) : null}
    </>
  )
}

export function ExpenseList({ houseId }: ExpenseListProps) {
  const { showToast } = useToast()
  const { data: members } = useHouseMembers(houseId)
  const { data: expenses = [], isLoading, error, refetch } = useExpenses(houseId)
  const deleteMutation = useDeleteExpense(houseId)

  async function handleDelete(expenseId: string) {
    if (!window.confirm('Xóa khoản chi này?')) {
      return
    }
    try {
      await deleteMutation.mutateAsync(expenseId)
      showToast('Đã xóa khoản chi.', 'success')
    } catch (err) {
      showToast(err instanceof ApiError ? err.message : 'Không thể xóa khoản chi.')
    }
  }

  return (
    <Card title="Danh sách khoản chi" description={`${expenses.length} khoản chi`}>
      {isLoading ? <LoadingState /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải danh sách khoản chi." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}
      {!isLoading && !error && expenses.length === 0 ? (
        <p className="text-sm text-slate-500">Chưa có khoản chi nào.</p>
      ) : null}
      <div className="space-y-3">
        {expenses.map((expense) => (
          <ExpenseListItem
            key={expense.id}
            expense={expense}
            houseId={houseId}
            members={members}
            onDelete={(expenseId) => void handleDelete(expenseId)}
            isDeleting={deleteMutation.isPending}
          />
        ))}
      </div>
    </Card>
  )
}
