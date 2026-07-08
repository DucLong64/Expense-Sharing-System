import { useDebts } from '@/features/settlement/api/settlement.query'
import { SettleDebtModal } from '@/features/settlement/components/settle-debt-modal'
import { SettlementHistory } from '@/features/settlement/components/settlement-history'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ArrowRightIcon, PlusIcon } from '@/shared/components/icons'
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
  const [settleDebt, setSettleDebt] = useState<DebtSummaryResponse | null | 'manual'>(null)

  const modalOpen = settleDebt !== null
  const prefillDebt = settleDebt === 'manual' ? null : settleDebt

  return (
    <>
      <div className="space-y-6">
        <Card
          title="Công nợ hiện tại"
          description={debts.length === 0 ? 'Mọi người đã cân bằng' : `${debts.length} khoản nợ`}
          action={
            debts.length > 0 ? (
              <Button
                size="sm"
                className="w-auto"
                onClick={() => setSettleDebt('manual')}
              >
                <PlusIcon className="h-4 w-4" />
                Ghi nhận thanh toán
              </Button>
            ) : null
          }
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
          <div className="divide-y divide-slate-100">
            {debts.map((debt, index) => (
              <div
                key={`${debt.fromUserId}-${debt.toUserId}-${index}`}
                className="flex flex-col gap-3 py-4 first:pt-0 sm:flex-row sm:items-center sm:justify-between"
              >
                <div className="flex flex-wrap items-center gap-2 text-sm">
                  <span className="font-medium text-slate-800">
                    {displayUsername(debt.fromUsername, debt.fromUserId)}
                  </span>
                  <ArrowRightIcon className="h-4 w-4 text-slate-400" />
                  <span className="font-medium text-slate-800">
                    {displayUsername(debt.toUsername, debt.toUserId)}
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  <p className="text-lg font-bold text-red-600">{formatCurrency(debt.amount)}</p>
                  <Button
                    size="sm"
                    className="w-auto shrink-0"
                    onClick={() => setSettleDebt(debt)}
                  >
                    Thanh toán
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </Card>

        <SettlementHistory houseId={houseId} />
      </div>

      <SettleDebtModal
        open={modalOpen}
        houseId={houseId}
        prefillDebt={prefillDebt}
        onClose={() => setSettleDebt(null)}
      />
    </>
  )
}
