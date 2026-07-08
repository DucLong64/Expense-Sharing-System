import { Link } from 'react-router-dom'
import { useDebts } from '@/features/settlement/api/settlement.query'
import { summarizePersonalDebts } from '@/features/settlement/utils/personal-debt'
import type { HouseResponse } from '@/features/house/types/house.types'
import { ArrowRightIcon, HomeIcon } from '@/shared/components/icons'
import { getCurrentUserId } from '@/shared/auth/current-user'
import { formatCurrency, formatDate } from '@/shared/utils/format'

interface HouseCardProps {
  house: HouseResponse
}

export function HouseCard({ house }: HouseCardProps) {
  const currentUserId = getCurrentUserId()
  const { data: debts = [], isLoading } = useDebts(house.id)
  const personal = summarizePersonalDebts(debts, currentUserId)

  return (
    <Link to={`/houses/${house.id}`} className="group block">
      <article className="relative h-full rounded-2xl border border-slate-200/80 bg-white p-5 shadow-[var(--shadow-soft)] transition duration-200 hover:-translate-y-0.5 hover:border-emerald-300/80 hover:shadow-[var(--shadow-card)]">
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

        <div className="mt-4 flex flex-wrap items-center gap-2">
          {!isLoading && personal.totalOwed > 0 ? (
            <span className="rounded-full bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-700">
              Bạn nợ {formatCurrency(personal.totalOwed)}
            </span>
          ) : null}
          {!isLoading && personal.totalOwingToMe > 0 ? (
            <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-semibold text-emerald-700">
              Được nhận {formatCurrency(personal.totalOwingToMe)}
            </span>
          ) : null}
          {!isLoading && personal.isBalanced ? (
            <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-600">
              Đã cân bằng
            </span>
          ) : null}
        </div>

        <p className="mt-4 text-xs font-medium text-slate-400">Tạo ngày {formatDate(house.createdAt)}</p>
      </article>
    </Link>
  )
}
