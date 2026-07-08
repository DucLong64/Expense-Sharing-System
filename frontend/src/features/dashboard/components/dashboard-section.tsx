import { useHouseDashboard } from '@/features/dashboard/api/dashboard.query'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ChartIcon, WalletIcon } from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { StatCard } from '@/shared/components/stat-card'
import { formatCurrency, displayUsername } from '@/shared/utils/format'

interface DashboardSectionProps {
  houseId: string
}

export function DashboardSection({ houseId }: DashboardSectionProps) {
  const { data, isLoading, error, refetch } = useHouseDashboard(houseId)

  if (isLoading) {
    return <LoadingState message="Đang tải dashboard..." />
  }

  if (error || !data) {
    return (
      <div className="space-y-3">
        <ErrorMessage message="Không thể tải dashboard." />
        <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
          Thử lại
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2">
        <StatCard
          label="Tổng chi tiêu"
          value={formatCurrency(data.totalSpending)}
          tone="default"
          icon={<WalletIcon className="h-5 w-5 text-slate-600" />}
        />
        <StatCard
          label="Đã thanh toán"
          value={formatCurrency(data.totalSettled)}
          tone="success"
          icon={<ChartIcon className="h-5 w-5 text-emerald-600" />}
        />
      </div>

      <Card title="Chi tiêu theo tháng" description="Tổng hợp theo từng tháng">
        <div className="space-y-2">
          {data.spendingByMonth.length === 0 ? (
            <p className="text-sm text-slate-500">Chưa có dữ liệu.</p>
          ) : (
            data.spendingByMonth.map((item) => (
              <div
                key={`${item.year}-${item.month}`}
                className="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3 text-sm"
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
          {data.spendingByMember.length === 0 ? (
            <p className="text-sm text-slate-500">Chưa có dữ liệu.</p>
          ) : (
            data.spendingByMember.map((item) => (
              <div
                key={item.userId}
                className="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3 text-sm"
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
  )
}
