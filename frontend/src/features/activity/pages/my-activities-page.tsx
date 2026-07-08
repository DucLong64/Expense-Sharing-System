import { useState } from 'react'
import { useMyActivities } from '@/features/activity/api/activity.query'
import { ActivityList } from '@/features/activity/components/activity-section'
import type { ActivityType } from '@/features/activity/types/activity.types'
import { activityTypeLabels, activityTypeOptions } from '@/features/activity/utils/activity-labels'
import { AppShell } from '@/shared/components/app-shell'
import { Button } from '@/shared/components/button'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'

export function MyActivitiesPage() {
  const [filter, setFilter] = useState<ActivityType | ''>('')
  const { data: activities = [], isLoading, error, refetch } = useMyActivities(
    filter ? { activityType: filter } : undefined,
  )

  return (
    <AppShell title="Hoạt động của tôi" subtitle="Nhật ký các thao tác bạn và nhóm đã thực hiện.">
      <div className="mb-6">
        <select
          className="rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-sm shadow-sm"
          value={filter}
          onChange={(event) => setFilter(event.target.value as ActivityType | '')}
        >
          <option value="">Tất cả loại</option>
          {activityTypeOptions.map((type) => (
            <option key={type} value={type}>
              {activityTypeLabels[type]}
            </option>
          ))}
        </select>
      </div>

      {isLoading ? <LoadingState message="Đang tải hoạt động..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải hoạt động." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}
      {!isLoading && !error ? <ActivityList activities={activities} /> : null}
    </AppShell>
  )
}
