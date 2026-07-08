import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute, PublicOnlyRoute } from '@/app/guards'
import { authRoutes } from '@/features/auth/routes'
import { houseRoutes } from '@/features/house/routes'

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<PublicOnlyRoute />}>{authRoutes}</Route>
        <Route element={<ProtectedRoute />}>{houseRoutes}</Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
