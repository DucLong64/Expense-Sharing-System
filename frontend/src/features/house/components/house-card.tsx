import { Link } from 'react-router-dom'
import type { HouseResponse } from '@/features/house/types/house.types'
import { ArrowRightIcon, HomeIcon } from '@/shared/components/icons'
import { formatDate } from '@/shared/utils/format'

interface HouseCardProps {
  house: HouseResponse
}

export function HouseCard({ house }: HouseCardProps) {
  return (
    <Link to={`/houses/${house.id}`} className="group block">
      <article className="h-full rounded-2xl border border-slate-200/80 bg-white p-5 shadow-[var(--shadow-soft)] transition duration-200 hover:-translate-y-0.5 hover:border-emerald-300/80 hover:shadow-[var(--shadow-card)]">
        <div className="flex items-start justify-between gap-3">
          <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-emerald-50 text-emerald-600 transition group-hover:bg-emerald-100">
            <HomeIcon className="h-5 w-5" />
          </span>
          <ArrowRightIcon className="h-5 w-5 shrink-0 text-slate-300 transition group-hover:translate-x-0.5 group-hover:text-emerald-600" />
        </div>
        <h3 className="mt-4 text-lg font-semibold text-slate-900">{house.name}</h3>
        <p className="mt-2 line-clamp-2 text-sm leading-relaxed text-slate-500">
          {house.description || 'Chưa có mô tả'}
        </p>
        <p className="mt-4 text-xs font-medium text-slate-400">Tạo ngày {formatDate(house.createdAt)}</p>
      </article>
    </Link>
  )
}
