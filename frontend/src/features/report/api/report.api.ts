import axios from 'axios'
import { apiClient } from '@/shared/api/axios-client'
import { ApiError } from '@/shared/api/api-error'
import type { ApiErrorBody } from '@/shared/api/api-response.types'
import { downloadBlob } from '@/shared/utils/download-file'

export type ReportFormat = 'excel' | 'pdf'

function parseFilename(contentDisposition: string | undefined, fallback: string): string {
  if (!contentDisposition) {
    return fallback
  }

  const utfMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utfMatch?.[1]) {
    return decodeURIComponent(utfMatch[1])
  }

  const plainMatch = contentDisposition.match(/filename="?([^";]+)"?/i)
  return plainMatch?.[1] ?? fallback
}

async function toApiError(error: unknown): Promise<ApiError> {
  if (!axios.isAxiosError(error) || !error.response?.data) {
    return new ApiError('REPORT_EXPORT_FAILED', 'Không thể tải báo cáo.')
  }

  const { data } = error.response
  if (data instanceof Blob) {
    try {
      const text = await data.text()
      const body = JSON.parse(text) as ApiErrorBody
      return new ApiError(body.code ?? 'REPORT_EXPORT_FAILED', body.message ?? 'Không thể tải báo cáo.')
    } catch {
      return new ApiError('REPORT_EXPORT_FAILED', 'Không thể tải báo cáo.')
    }
  }

  const body = data as ApiErrorBody
  return new ApiError(body.code ?? 'REPORT_EXPORT_FAILED', body.message ?? 'Không thể tải báo cáo.')
}

export async function downloadHouseReport(houseId: string, format: ReportFormat): Promise<void> {
  try {
    const response = await apiClient.get<Blob>(`/api/v1/houses/${houseId}/reports/${format}`, {
      responseType: 'blob',
    })

    const fallback = `bao-cao-${houseId}.${format === 'excel' ? 'xlsx' : 'pdf'}`
    const filename = parseFilename(response.headers['content-disposition'], fallback)
    downloadBlob(response.data, filename)
  } catch (error) {
    if (error instanceof ApiError) {
      throw error
    }
    throw await toApiError(error)
  }
}
