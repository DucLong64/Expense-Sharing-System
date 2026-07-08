interface TabItem<T extends string> {
  key: T
  label: string
}

interface TabsProps<T extends string> {
  tabs: TabItem<T>[]
  activeTab: T
  onChange: (tab: T) => void
}

export function Tabs<T extends string>({ tabs, activeTab, onChange }: TabsProps<T>) {
  return (
    <div className="inline-flex rounded-xl border border-slate-200 bg-slate-100/80 p-1">
      {tabs.map((tab) => {
        const isActive = activeTab === tab.key
        return (
          <button
            key={tab.key}
            type="button"
            onClick={() => onChange(tab.key)}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition-all duration-200 ${
              isActive
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            {tab.label}
          </button>
        )
      })}
    </div>
  )
}
