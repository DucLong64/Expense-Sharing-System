import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { ActivityLogModal } from '@/features/house/components/activity-log-modal'
import { EditHouseModal } from '@/features/house/components/edit-house-modal'
import { HouseMenu } from '@/features/house/components/house-menu'
import { HouseSettingsModal } from '@/features/house/components/house-settings-modal'
import { MembersSection } from '@/features/house/components/members-section'
import { OverviewSection } from '@/features/house/components/overview-section'
import { useHouse } from '@/features/house/api/house.query'
import { ExpenseSection } from '@/features/expense/components/expense-section'
import { DebtSection } from '@/features/settlement/components/debt-section'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { Tabs } from '@/shared/components/tabs'

type TabKey = 'overview' | 'expenses' | 'debts' | 'members'

const tabs: Array<{ key: TabKey; label: string }> = [
  { key: 'overview', label: 'Tổng quan' },
  { key: 'expenses', label: 'Khoản chi' },
  { key: 'debts', label: 'Công nợ' },
  { key: 'members', label: 'Thành viên' },
]

export function HouseDetailPage() {
  const { houseId = '' } = useParams()
  const [activeTab, setActiveTab] = useState<TabKey>('overview')
  const [openEditModal, setOpenEditModal] = useState(false)
  const [openSettingsModal, setOpenSettingsModal] = useState(false)
  const [openActivityModal, setOpenActivityModal] = useState(false)
  const { data: house, isLoading, error, refetch } = useHouse(houseId)

  if (!houseId) {
    return null
  }

  return (
    <AppShell
      title={house?.name ?? 'Chi tiết nhóm'}
      subtitle={house?.description ?? undefined}
      backTo="/"
    >
      {isLoading ? <LoadingState message="Đang tải nhóm..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải thông tin nhóm." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}

      {!isLoading && !error ? (
        <div className="space-y-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
            <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />
            <HouseMenu
              houseId={houseId}
              onEdit={() => setOpenEditModal(true)}
              onOpenSettings={() => setOpenSettingsModal(true)}
              onOpenActivity={() => setOpenActivityModal(true)}
            />
          </div>

          {activeTab === 'overview' ? (
            <OverviewSection houseId={houseId} onNavigate={setActiveTab} />
          ) : null}
          {activeTab === 'expenses' ? <ExpenseSection houseId={houseId} /> : null}
          {activeTab === 'debts' ? <DebtSection houseId={houseId} /> : null}
          {activeTab === 'members' ? <MembersSection houseId={houseId} /> : null}
        </div>
      ) : null}

      {house ? (
        <>
          <EditHouseModal
            open={openEditModal}
            house={house}
            onClose={() => setOpenEditModal(false)}
          />
          <HouseSettingsModal
            open={openSettingsModal}
            houseId={houseId}
            houseName={house.name}
            onEdit={() => setOpenEditModal(true)}
            onClose={() => setOpenSettingsModal(false)}
          />
          <ActivityLogModal
            open={openActivityModal}
            houseId={houseId}
            onClose={() => setOpenActivityModal(false)}
          />
        </>
      ) : null}
    </AppShell>
  )
}
