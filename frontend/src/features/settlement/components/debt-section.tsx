import { useDebts } from '@/features/settlement/api/settlement.query'
import { ConfirmDebtReceivedModal } from '@/features/settlement/components/confirm-debt-received-modal'
import { SettleDebtModal } from '@/features/settlement/components/settle-debt-modal'
import { SettlementHistory } from '@/features/settlement/components/settlement-history'
import { summarizePersonalDebts } from '@/features/settlement/utils/personal-debt'
import { Button } from '@/shared/components/button'
import { Card } from '@/shared/components/card'
import { ArrowRightIcon, PlusIcon } from '@/shared/components/icons'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { formatCurrency, displayUsername } from '@/shared/utils/format'
import type { DebtSummaryResponse } from '@/features/settlement/types/settlement.types'
import { useMemo, useState } from 'react'

interface DebtSectionProps {
  houseId: string
}

export function DebtSection({ houseId }: DebtSectionProps) {
  const currentUserId = getCurrentUserId()
  const { data: debts = [], isLoading, error, refetch } = useDebts(houseId)
  const [settleDebt, setSettleDebt] = useState<DebtSummaryResponse | null | 'manual'>(null)
  const [confirmReceived, setConfirmReceived] = useState<DebtSummaryResponse | null | 'manual'>(null)

  const personal = useMemo(
    () => summarizePersonalDebts(debts, currentUserId),
    [debts, currentUserId],
  )

  const settleModalOpen = settleDebt !== null
  const settlePrefill = settleDebt === 'manual' ? null : settleDebt
  const confirmModalOpen = confirmReceived !== null
  const confirmPrefill = confirmReceived === 'manual' ? null : confirmReceived

  return (
    <>
      <div className="space-y-6">
        <Card
          title="Công nợ hiện tại"
          description={debts.length === 0 ? 'Mọi người đã cân bằng' : `${debts.length} khoản nợ`}
          action={
            debts.length > 0 ? (
              <div className="flex flex-wrap gap-2">
                {personal.myDebts.length > 0 ? (
                  <Button size="sm" className="w-auto" onClick={() => setSettleDebt('manual')}>
                    <PlusIcon className="h-4 w-4" />
                    Ghi nhận thanh toán
                  </Button>
                ) : null}
                {personal.owedToMe.length > 0 ? (
                  <Button
                    size="sm"
                    variant="secondary"
                    className="w-auto"
                    onClick={() => setConfirmReceived('manual')}
                  >
                    Xác nhận đã nhận
                  </Button>
                ) : null}
              </div>
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
            {debts.map((debt, index) => {
              const isDebtor = debt.fromUserId === currentUserId
              const isCreditor = debt.toUserId === currentUserId

              return (
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
                  <div className="flex flex-wrap items-center gap-2">
                    <p className="text-lg font-bold text-red-600">{formatCurrency(debt.amount)}</p>
                    {isDebtor ? (
                      <Button
                        size="sm"
                        className="w-auto shrink-0"
                        onClick={() => setSettleDebt(debt)}
                      >
                        Thanh toán
                      </Button>
                    ) : null}
                    {isCreditor ? (
                      <Button
                        size="sm"
                        variant="secondary"
                        className="w-auto shrink-0"
                        onClick={() => setConfirmReceived(debt)}
                      >
                        Xác nhận đã nhận
                      </Button>
                    ) : null}
                  </div>
                </div>
              )
            })}
          </div>
        </Card>

        <SettlementHistory houseId={houseId} />
      </div>

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
