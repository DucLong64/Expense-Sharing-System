import { useDebts } from '@/features/settlement/api/settlement.query'
import { SettleDebtForm } from '@/features/settlement/components/settle-debt-form'
import { SettlementHistory } from '@/features/settlement/components/settlement-history'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ArrowRightIcon } from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { formatCurrency, displayUsername } from '@/shared/utils/format'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { useState } from 'react'

interface DebtSectionProps {
  houseId: string
}

export function DebtSection({ houseId }: DebtSectionProps) {
  const { data: debts = [], isLoading, error, refetch } = useDebts(houseId)
  const [prefillDebt, setPrefillDebt] = useState<DebtSummaryResponse | null>(null)

  return (
    <div className="space-y-6">
      <Card
        title="Công nợ hiện tại"
        description={debts.length === 0 ? 'Mọi người đã cân bằng' : `${debts.length} khoản nợ`}
      >
        {isLoading ? <LoadingState /> : null}
        {error ? (
          <div className="space-y-3">
            <ErrorMessage message="Không thể tải công nợ." />
            <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
              Thử lại
            </Button>
          </div>
        ) : null}
        {!isLoading && !error && debts.length === 0 ? (
          <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-6 text-center">
            <p className="text-sm font-medium text-emerald-800">Không còn công nợ nào — tuyệt vời!</p>
          </div>
        ) : null}
        <div className="space-y-3">
          {debts.map((debt, index) => (
            <button
              key={`${debt.fromUserId}-${debt.toUserId}-${index}`}
              type="button"
              className="flex w-full flex-col gap-3 rounded-xl border border-slate-200/80 bg-slate-50/50 p-4 text-left transition hover:border-emerald-300 hover:bg-emerald-50/30 sm:flex-row sm:items-center sm:justify-between"
              onClick={() => setPrefillDebt(debt)}
            >
              <div className="flex flex-wrap items-center gap-2 text-sm">
                <span className="rounded-lg bg-white px-3 py-1.5 font-medium text-slate-700 shadow-sm">
                  {displayUsername(debt.fromUsername, debt.fromUserId)}
                </span>
                <ArrowRightIcon className="h-4 w-4 text-slate-400" />
                <span className="rounded-lg bg-white px-3 py-1.5 font-medium text-slate-700 shadow-sm">
                  {displayUsername(debt.toUsername, debt.toUserId)}
                </span>
              </div>
              <p className="text-xl font-bold text-red-600">{formatCurrency(debt.amount)}</p>
            </button>
          ))}
        </div>
        {debts.length > 0 ? (
          <p className="mt-3 text-xs text-slate-500">Nhấn vào khoản nợ để điền form thanh toán.</p>
        ) : null}
      </Card>

      <SettleDebtForm houseId={houseId} prefillDebt={prefillDebt} />
      <SettlementHistory houseId={houseId} />
    </div>
  )
}
