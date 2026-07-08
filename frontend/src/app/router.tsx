import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute, PublicOnlyRoute } from '@/app/guards'
import { ProfilePage } from '@/features/auth/pages/profile-page'
import { authRoutes } from '@/features/auth/routes'
import { houseRoutes } from '@/features/house/routes'
import { NotificationsPage } from '@/features/notification/pages/notifications-page'

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<PublicOnlyRoute />}>{authRoutes}</Route>
        <Route element={<ProtectedRoute />}>
          {houseRoutes}
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
