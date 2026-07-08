import { useState } from 'react'
import { useParams } from 'react-router-dom'
import { ActivitySection } from '@/features/activity/components/activity-section'
import { useHouse } from '@/features/house/api/house.query'
import { DashboardSection } from '@/features/dashboard/components/dashboard-section'
import { EditHouseModal } from '@/features/house/components/edit-house-modal'
import { HouseSettingsSection } from '@/features/house/components/house-settings-section'
import { MembersSection } from '@/features/house/components/members-section'
import { ExpenseSection } from '@/features/expense/components/expense-section'
import { DebtSection } from '@/features/settlement/components/debt-section'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { Tabs } from '@/shared/components/tabs'

type TabKey = 'expenses' | 'debts' | 'dashboard' | 'members' | 'activities' | 'settings'

const tabs: Array<{ key: TabKey; label: string }> = [
  { key: 'expenses', label: 'Khoản chi' },
  { key: 'debts', label: 'Công nợ' },
  { key: 'dashboard', label: 'Dashboard' },
  { key: 'members', label: 'Thành viên' },
  { key: 'activities', label: 'Hoạt động' },
  { key: 'settings', label: 'Cài đặt' },
]

export function HouseDetailPage() {
  const { houseId = '' } = useParams()
  const [activeTab, setActiveTab] = useState<TabKey>('expenses')
  const [openEditModal, setOpenEditModal] = useState(false)
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
          <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

          {activeTab === 'expenses' ? <ExpenseSection houseId={houseId} /> : null}
          {activeTab === 'debts' ? <DebtSection houseId={houseId} /> : null}
          {activeTab === 'dashboard' ? <DashboardSection houseId={houseId} /> : null}
          {activeTab === 'members' ? <MembersSection houseId={houseId} /> : null}
          {activeTab === 'activities' ? <ActivitySection houseId={houseId} /> : null}
          {activeTab === 'settings' && house ? (
            <HouseSettingsSection houseId={houseId} onEdit={() => setOpenEditModal(true)} />
          ) : null}
        </div>
      ) : null}

      {house ? (
        <EditHouseModal
          open={openEditModal}
          house={house}
          onClose={() => setOpenEditModal(false)}
        />
      ) : null}
    </AppShell>
  )
}
