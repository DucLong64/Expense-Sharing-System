import type { UseFormReturn } from 'react-hook-form'
import {
  splitTypeLabels,
  type CreateExpenseFormValues,
} from '@/features/expense/schemas/expense.schema'
import type { HouseMemberResponse } from '@/features/house/types/house.types'
import { Input } from '@/shared/components/input'
import { Select } from '@/shared/components/select'
import { displayUsername } from '@/shared/utils/format'

interface ExpenseFormFieldsProps {
  form: UseFormReturn<CreateExpenseFormValues>
  members: HouseMemberResponse[]
  showPaidBy?: boolean
}

export function ExpenseFormFields({ form, members, showPaidBy = true }: ExpenseFormFieldsProps) {
  const { register, formState, watch, setValue } = form
  const splitType = watch('splitType')
  const participants = watch('participants')

  function toggleParticipant(index: number) {
    const current = participants[index]
    setValue(`participants.${index}.selected`, !current.selected, { shouldValidate: true })
  }

  return (
    <div className="space-y-4">
      <Input label="Tiêu đề" error={formState.errors.title?.message} {...register('title')} />
      <Input
        label="Mô tả"
        error={formState.errors.description?.message}
        {...register('description')}
      />
      <div className="grid gap-4 sm:grid-cols-2">
        <Input
          label="Số tiền"
          type="number"
          min="1"
          error={formState.errors.amount?.message}
          {...register('amount', { valueAsNumber: true })}
        />
        <Input
          label="Ngày phát sinh"
          type="date"
          error={formState.errors.expenseDate?.message}
          {...register('expenseDate')}
        />
      </div>
      {showPaidBy ? (
        <Select
          label="Người trả"
          error={formState.errors.paidBy?.message}
          options={members.map((member) => ({
            value: member.userId,
            label: `${displayUsername(member.username, member.userId)} (${member.role})`,
          }))}
          {...register('paidBy')}
        />
      ) : null}
      <Select
        label="Cách chia"
        error={formState.errors.splitType?.message}
        options={Object.entries(splitTypeLabels).map(([value, label]) => ({
          value,
          label,
        }))}
        {...register('splitType')}
      />
      <Input label="Ghi chú" error={formState.errors.note?.message} {...register('note')} />
      <div>
        <p className="mb-3 text-sm font-medium text-slate-700">Thành viên tham gia</p>
        <div className="space-y-2">
          {participants.map((participant, index) => {
            const member = members.find((item) => item.userId === participant.userId)
            if (!member) {
              return null
            }
            return (
              <div
                key={participant.userId}
                className={`rounded-xl border px-3.5 py-3 ${
                  participant.selected
                    ? 'border-emerald-300 bg-emerald-50/50'
                    : 'border-slate-200 bg-white'
                }`}
              >
                <label className="flex cursor-pointer items-center gap-3 text-sm">
                  <input
                    type="checkbox"
                    className="h-4 w-4 rounded border-slate-300 text-emerald-600"
                    checked={participant.selected}
                    onChange={() => toggleParticipant(index)}
                  />
                  <span className="font-medium text-slate-800">
                    {displayUsername(member.username, member.userId)} ({member.role})
                  </span>
                </label>
                {participant.selected && splitType === 'FIXED' ? (
                  <Input
                    label="Số tiền phần chia"
                    type="number"
                    min="1"
                    className="mt-3"
                    {...register(`participants.${index}.shareAmount`, { valueAsNumber: true })}
                  />
                ) : null}
                {participant.selected && splitType === 'PERCENTAGE' ? (
                  <Input
                    label="Phần trăm (%)"
                    type="number"
                    min="1"
                    max="100"
                    className="mt-3"
                    {...register(`participants.${index}.sharePercentage`, { valueAsNumber: true })}
                  />
                ) : null}
              </div>
            )
          })}
        </div>
        {formState.errors.participants?.message ? (
          <p className="mt-2 text-sm text-red-600">{formState.errors.participants.message}</p>
        ) : null}
      </div>
    </div>
  )
}
