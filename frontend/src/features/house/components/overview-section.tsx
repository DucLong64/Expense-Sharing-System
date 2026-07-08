import { useState } from 'react'
import { ActivityList } from '@/features/activity/components/activity-section'
import { useHouseActivities } from '@/features/activity/api/activity.query'
import { useHouseDashboard } from '@/features/dashboard/api/dashboard.query'
import { useHouseMembers } from '@/features/house/api/house.query'
import { CreateExpenseModal } from '@/features/expense/components/create-expense-modal'
import { useExpenses } from '@/features/expense/api/expense.query'
import { splitTypeLabels } from '@/features/expense/schemas/expense.schema'
import { useExportHouseReport } from '@/features/report/hooks/use-export-house-report'
import { useDebts } from '@/features/settlement/api/settlement.query'
import { ConfirmDebtReceivedModal } from '@/features/settlement/components/confirm-debt-received-modal'
import { SettleDebtModal } from '@/features/settlement/components/settle-debt-modal'
import { summarizePersonalDebts } from '@/features/settlement/utils/personal-debt'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import {
  ChartIcon,
  PlusIcon,
  ReceiptIcon,
  WalletIcon,
} from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { StatCard } from '@/shared/components/stat-card'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { displayUsername, formatCurrency, formatDate } from '@/shared/utils/format'

type HouseTabKey = 'overview' | 'expenses' | 'debts' | 'members'

interface OverviewSectionProps {
  houseId: string
  onNavigate: (tab: HouseTabKey) => void
}

export function OverviewSection({ houseId, onNavigate }: OverviewSectionProps) {
  const currentUserId = getCurrentUserId()
  const [openCreateExpense, setOpenCreateExpense] = useState(false)
  const [settleDebt, setSettleDebt] = useState<DebtSummaryResponse | null | 'manual'>(null)
  const [confirmReceived, setConfirmReceived] = useState<DebtSummaryResponse | null | 'manual'>(null)

  const { data: members = [], isLoading: membersLoading } = useHouseMembers(houseId)
  const { data: dashboard, isLoading: dashboardLoading, error: dashboardError, refetch } =
    useHouseDashboard(houseId)
  const { data: debts = [] } = useDebts(houseId)
  const { data: expenses = [] } = useExpenses(houseId)
  const { data: activities = [] } = useHouseActivities(houseId)
  const { exportReport, isExportingExcel, isExportingPdf } = useExportHouseReport(houseId)

  const personal = summarizePersonalDebts(debts, currentUserId)
  const recentExpenses = expenses.slice(0, 5)
  const recentActivities = activities.slice(0, 5)
  const settleModalOpen = settleDebt !== null
  const settlePrefill = settleDebt === 'manual' ? null : settleDebt
  const confirmModalOpen = confirmReceived !== null
  const confirmPrefill = confirmReceived === 'manual' ? null : confirmReceived

  if (dashboardLoading || membersLoading) {
    return <LoadingState message="Đang tải tổng quan..." />
  }

  if (dashboardError || !dashboard) {
    return (
      <div className="space-y-3">
        <ErrorMessage message="Không thể tải tổng quan." />
        <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
          Thử lại
        </Button>
      </div>
    )
  }

  function openSettleModal() {
    if (personal.myDebts.length === 1) {
      setSettleDebt(personal.myDebts[0])
      return
    }
    setSettleDebt('manual')
  }

  function openConfirmReceivedModal() {
    if (personal.owedToMe.length === 1) {
      setConfirmReceived(personal.owedToMe[0])
      return
    }
    setConfirmReceived('manual')
  }

  return (
    <>
      <div className="space-y-6">
        <section className="rounded-2xl border border-slate-200/80 bg-gradient-to-br from-white to-emerald-50/40 p-6 shadow-[var(--shadow-soft)]">
          <div className="grid gap-6 sm:grid-cols-2">
            <div>
              <p className="text-sm font-medium text-slate-500">Bạn nợ</p>
              <p className="mt-1 text-3xl font-bold tracking-tight text-red-600">
                {formatCurrency(personal.totalOwed)}
              </p>
            </div>
            <div>
              <p className="text-sm font-medium text-slate-500">Được nhận</p>
              <p className="mt-1 text-3xl font-bold tracking-tight text-emerald-700">
                {formatCurrency(personal.totalOwingToMe)}
              </p>
            </div>
          </div>
          {personal.isBalanced ? (
            <p className="mt-4 text-sm font-medium text-emerald-700">Bạn đã cân bằng trong nhóm này.</p>
          ) : null}
          <div className="mt-6 flex flex-wrap gap-3">
            <Button className="w-auto" onClick={() => setOpenCreateExpense(true)}>
              <PlusIcon className="h-4 w-4" />
              Thêm khoản chi
            </Button>
            {personal.totalOwed > 0 ? (
              <Button variant="secondary" className="w-auto" onClick={openSettleModal}>
                Ghi nhận thanh toán
              </Button>
            ) : null}
            {personal.owedToMe.length > 0 ? (
              <Button variant="secondary" className="w-auto" onClick={openConfirmReceivedModal}>
                Xác nhận đã nhận
              </Button>
            ) : null}
          </div>
        </section>

        <div className="grid gap-4 sm:grid-cols-2">
          <StatCard
            label="Tổng chi tiêu"
            value={formatCurrency(dashboard.totalSpending)}
            tone="default"
            icon={<WalletIcon className="h-5 w-5 text-slate-600" />}
          />
          <StatCard
            label="Đã thanh toán"
            value={formatCurrency(dashboard.totalSettled)}
            tone="success"
            icon={<ChartIcon className="h-5 w-5 text-emerald-600" />}
          />
        </div>

        <div className="grid gap-6 lg:grid-cols-2">
          <Card
            title="Công nợ của bạn"
            description={
              personal.myDebts.length === 0
                ? 'Không có khoản nợ cần trả'
                : `${personal.myDebts.length} khoản cần xử lý`
            }
            action={
              personal.myDebts.length > 0 ? (
                <Button variant="ghost" size="sm" className="w-auto" onClick={() => onNavigate('debts')}>
                  Xem tất cả
                </Button>
              ) : null
            }
          >
            {personal.myDebts.length === 0 ? (
              <p className="text-sm text-slate-500">Bạn không nợ ai trong nhóm.</p>
            ) : (
              <div className="divide-y divide-slate-100">
                {personal.myDebts.slice(0, 3).map((debt, index) => (
                  <div
                    key={`${debt.toUserId}-${index}`}
                    className="flex items-center justify-between gap-3 py-3 first:pt-0"
                  >
                    <div className="min-w-0 text-sm">
                      <span className="text-slate-500">Trả cho </span>
                      <span className="font-medium text-slate-800">
                        {displayUsername(debt.toUsername, debt.toUserId)}
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-red-600">{formatCurrency(debt.amount)}</span>
                      <Button
                        size="sm"
                        className="w-auto shrink-0"
                        onClick={() => setSettleDebt(debt)}
                      >
                        Trả
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>

          <Card
            title="Được nhận từ"
            description={
              personal.owedToMe.length === 0
                ? 'Không có khoản cần xác nhận'
                : `${personal.owedToMe.length} khoản chờ xác nhận`
            }
            action={
              personal.owedToMe.length > 0 ? (
                <Button variant="ghost" size="sm" className="w-auto" onClick={() => onNavigate('debts')}>
                  Xem tất cả
                </Button>
              ) : null
            }
          >
            {personal.owedToMe.length === 0 ? (
              <p className="text-sm text-slate-500">Không ai nợ bạn trong nhóm.</p>
            ) : (
              <div className="divide-y divide-slate-100">
                {personal.owedToMe.slice(0, 3).map((debt, index) => (
                  <div
                    key={`${debt.fromUserId}-${index}`}
                    className="flex items-center justify-between gap-3 py-3 first:pt-0"
                  >
                    <div className="min-w-0 text-sm">
                      <span className="text-slate-500">Nhận từ </span>
                      <span className="font-medium text-slate-800">
                        {displayUsername(debt.fromUsername, debt.fromUserId)}
                      </span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-emerald-700">{formatCurrency(debt.amount)}</span>
                      <Button
                        size="sm"
                        variant="secondary"
                        className="w-auto shrink-0"
                        onClick={() => setConfirmReceived(debt)}
                      >
                        Xác nhận
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>

        <Card
          title="Khoản chi gần đây"
          description={`${expenses.length} khoản chi`}
          action={
            expenses.length > 0 ? (
              <Button variant="ghost" size="sm" className="w-auto" onClick={() => onNavigate('expenses')}>
                Xem tất cả
              </Button>
            ) : null
          }
        >
          {recentExpenses.length === 0 ? (
            <p className="text-sm text-slate-500">Chưa có khoản chi nào.</p>
          ) : (
            <div className="divide-y divide-slate-100">
              {recentExpenses.map((expense) => (
                <div key={expense.id} className="flex items-center gap-3 py-3 first:pt-0">
                  <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-emerald-50 text-emerald-600">
                    <ReceiptIcon className="h-4 w-4" />
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium text-slate-900">{expense.title}</p>
                    <p className="text-xs text-slate-500">
                      {formatDate(expense.expenseDate)} · {splitTypeLabels[expense.splitType]}
                    </p>
                  </div>
                  <p className="shrink-0 text-sm font-bold text-emerald-700">
                    {formatCurrency(expense.amount)}
                  </p>
                </div>
              ))}
            </div>
          )}
        </Card>

        <Card
          title="Hoạt động gần đây"
          description="Cập nhật mới nhất trong nhóm"
        >
          <ActivityList activities={recentActivities} />
          {activities.length > 5 ? (
            <p className="mt-3 text-xs text-slate-500">
              Mở menu nhóm (⋯) để xem toàn bộ nhật ký hoạt động.
            </p>
          ) : null}
        </Card>

        <div className="grid gap-6 lg:grid-cols-2">
          <Card title="Chi tiêu theo tháng" description="Tổng hợp theo từng tháng">
            <div className="space-y-2">
              {dashboard.spendingByMonth.length === 0 ? (
                <p className="text-sm text-slate-500">Chưa có dữ liệu.</p>
              ) : (
                dashboard.spendingByMonth.slice(0, 6).map((item) => (
                  <div
                    key={`${item.year}-${item.month}`}
                    className="flex items-center justify-between rounded-lg px-1 py-2 text-sm"
                  >
                    <span className="font-medium text-slate-700">
                      Tháng {item.month}/{item.year}
                    </span>
                    <span className="font-semibold text-slate-900">{formatCurrency(item.amount)}</span>
                  </div>
                ))
              )}
            </div>
          </Card>

          <Card title="Chi tiêu theo thành viên" description="Phân bổ theo người tham gia">
            <div className="space-y-2">
              {dashboard.spendingByMember.length === 0 ? (
                <p className="text-sm text-slate-500">Chưa có dữ liệu.</p>
              ) : (
                dashboard.spendingByMember.map((item) => (
                  <div
                    key={item.userId}
                    className="flex items-center justify-between rounded-lg px-1 py-2 text-sm"
                  >
                    <span className="font-medium text-slate-700">
                      {displayUsername(item.username, item.userId)}
                    </span>
                    <span className="font-semibold text-slate-900">{formatCurrency(item.amount)}</span>
                  </div>
                ))
              )}
            </div>
          </Card>
        </div>

        <div className="flex flex-wrap gap-3">
          <Button
            variant="secondary"
            className="w-auto"
            loading={isExportingExcel}
            onClick={() => void exportReport('excel')}
          >
            Xuất Excel
          </Button>
          <Button
            variant="secondary"
            className="w-auto"
            loading={isExportingPdf}
            onClick={() => void exportReport('pdf')}
          >
            Xuất PDF
          </Button>
        </div>
      </div>

      <CreateExpenseModal
        open={openCreateExpense}
        houseId={houseId}
        members={members}
        onClose={() => setOpenCreateExpense(false)}
      />
      <SettleDebtModal
        open={settleModalOpen}
        houseId={houseId}
        prefillDebt={settlePrefill}
        onClose={() => setSettleDebt(null)}
      />
      <ConfirmDebtReceivedModal
        open={confirmModalOpen}
        houseId={houseId}
        prefillDebt={confirmPrefill}
        onClose={() => setConfirmReceived(null)}
      />
    </>
  )
}
