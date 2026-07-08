import { useState } from 'react'
import { useHouseActivities } from '@/features/activity/api/activity.query'
import type { ActivityType } from '@/features/activity/types/activity.types'
import { activityTypeLabels, activityTypeOptions } from '@/features/activity/utils/activity-labels'
import { Card } from '@/shared/components/card'
import { ErrorMessage } from '@/shared/components/error-message'
import { LoadingState } from '@/shared/components/loading-state'
import { formatDateTime, displayUsername } from '@/shared/utils/format'
import { Button } from '@/shared/components/button'

interface ActivityListProps {
  activities: Array<{
    id: string
    type: ActivityType
    message: string
    actorUserId: string
    actorUsername: string
    createdAt: string
  }>
}

export function ActivityList({ activities }: ActivityListProps) {
  if (activities.length === 0) {
    return <p className="text-sm text-slate-500">Chưa có hoạt động nào.</p>
  }

  return (
    <div className="divide-y divide-slate-100">
      {activities.map((activity) => (
        <div key={activity.id} className="flex gap-4 py-4 first:pt-0">
          <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-emerald-50 text-xs font-bold text-emerald-700">
            {activityTypeLabels[activity.type].slice(0, 2)}
          </span>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-medium text-slate-900">{activity.message}</p>
            <p className="mt-1 text-xs text-slate-500">
              {activityTypeLabels[activity.type]} · {displayUsername(activity.actorUsername, activity.actorUserId)} ·{' '}
              {formatDateTime(activity.createdAt)}
            </p>
          </div>
        </div>
      ))}
    </div>
  )
}

interface ActivitySectionProps {
  houseId: string
  embedded?: boolean
}

export function ActivitySection({ houseId, embedded = false }: ActivitySectionProps) {
  const [filter, setFilter] = useState<ActivityType | ''>('')
  const { data: activities = [], isLoading, error, refetch } = useHouseActivities(
    houseId,
    filter || undefined,
  )

  const content = (
    <>
      <div className="mb-4">
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

      {isLoading ? <LoadingState message="Đang tải nhật ký..." /> : null}
      {error ? (
        <div className="space-y-3">
          <ErrorMessage message="Không thể tải nhật ký hoạt động." />
          <Button variant="secondary" className="w-auto" onClick={() => void refetch()}>
            Thử lại
          </Button>
        </div>
      ) : null}
      {!isLoading && !error ? <ActivityList activities={activities} /> : null}
    </>
  )

  if (embedded) {
    return content
  }

  return (
    <Card title="Nhật ký hoạt động" description="Theo dõi mọi thay đổi trong nhóm">
      {content}
    </Card>
  )
}
