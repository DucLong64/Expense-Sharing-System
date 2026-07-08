import { useSettlements } from '@/features/settlement/api/settlement.query'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { Button } from '@/shared/components/button'
import { formatCurrency, formatDateTime, displayUsername } from '@/shared/utils/format'

interface SettlementHistoryProps {
  houseId: string
}

export function SettlementHistory({ houseId }: SettlementHistoryProps) {
  const { data: settlements = [], isLoading, error, refetch } = useSettlements(houseId)

  return (
    <Card title="Lịch sử thanh toán" description="Các lần ghi nhận trả nợ">
      {isLoading ? <LoadingState message="Đang tải lịch sử..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải lịch sử thanh toán." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}
      {!isLoading && !error && settlements.length === 0 ? (
        <p className="text-sm text-slate-500">Chưa có giao dịch thanh toán nào.</p>
      ) : null}
      <div className="space-y-3">
        {settlements.map((settlement) => (
          <div
            key={settlement.id}
            className="rounded-xl border border-slate-200/80 bg-slate-50/50 p-4"
          >
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
              <div className="text-sm text-slate-700">
                <span className="font-medium">{displayUsername(settlement.fromUsername, settlement.fromUserId)}</span>
                {' → '}
                <span className="font-medium">{displayUsername(settlement.toUsername, settlement.toUserId)}</span>
              </div>
              <p className="font-bold text-emerald-700">{formatCurrency(settlement.amount)}</p>
            </div>
            {settlement.note ? (
              <p className="mt-2 text-sm text-slate-500">{settlement.note}</p>
            ) : null}
            <p className="mt-1 text-xs text-slate-400">{formatDateTime(settlement.settledAt)}</p>
          </div>
        ))}
      </div>
    </Card>
  )
}
