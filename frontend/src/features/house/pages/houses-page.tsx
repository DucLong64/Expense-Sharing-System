import { useState } from 'react'
import { useMyHouses } from '@/features/house/api/house.query'
import { CreateHouseModal } from '@/features/house/components/create-house-modal'
import { HouseCard } from '@/features/house/components/house-card'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { EmptyState } from '@/shared/components/empty-state'
import { ErrorMessage } from '@/shared/components/error-message'
import { HomeIcon, PlusIcon } from '@/shared/components/icons'
import { LoadingState } from '@/shared/components/loading-state'

export function HousesPage() {
  const [openCreateModal, setOpenCreateModal] = useState(false)
  const { data: houses = [], isLoading, error, refetch } = useMyHouses()

  return (
    <AppShell
      title="Nhóm của tôi"
      subtitle="Chọn nhóm để xem chi tiêu, công nợ và dashboard."
    >
      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="rounded-xl border border-slate-200/80 bg-white px-4 py-3 shadow-[var(--shadow-soft)]">
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Tổng nhóm</p>
          <p className="mt-0.5 text-2xl font-bold text-slate-900">{houses.length}</p>
        </div>
        <Button className="w-auto shrink-0" onClick={() => setOpenCreateModal(true)}>
          <PlusIcon className="h-4 w-4" />
          Tạo nhóm
        </Button>
      </div>

      {isLoading ? <LoadingState message="Đang tải danh sách nhóm..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải danh sách nhóm." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}

      {!isLoading && !error && houses.length === 0 ? (
        <EmptyState
          icon={<HomeIcon className="h-7 w-7" />}
          title="Chưa có nhóm nào"
          description="Tạo nhóm đầu tiên để bắt đầu quản lý chi tiêu chung cùng bạn bè và gia đình."
          action={
            <Button className="w-auto" onClick={() => setOpenCreateModal(true)}>
              <PlusIcon className="h-4 w-4" />
              Tạo nhóm
            </Button>
          }
        />
      ) : null}

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {houses.map((house) => (
          <HouseCard key={house.id} house={house} />
        ))}
      </div>

      <CreateHouseModal open={openCreateModal} onClose={() => setOpenCreateModal(false)} />
    </AppShell>
  )
}
