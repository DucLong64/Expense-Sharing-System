import { useState } from 'react'
import { downloadHouseReport, type ReportFormat } from '@/features/report/api/report.api'
import { ApiError } from '@/shared/api/api-error'
import { useToast } from '@/shared/hooks/use-toast'

export function useExportHouseReport(houseId: string) {
  const { showToast } = useToast()
  const [exportingFormat, setExportingFormat] = useState<ReportFormat | null>(null)

  async function exportReport(format: ReportFormat) {
    setExportingFormat(format)
    try {
      await downloadHouseReport(houseId, format)
      showToast(format === 'excel' ? 'Đã tải file Excel.' : 'Đã tải file PDF.', 'success')
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Không thể xuất báo cáo.'
      showToast(message)
    } finally {
      setExportingFormat(null)
    }
  }

  return {
    exportReport,
    isExportingExcel: exportingFormat === 'excel',
    isExportingPdf: exportingFormat === 'pdf',
  }
}
