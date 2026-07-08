import { Route } from 'react-router-dom'
import { MyActivitiesPage } from '@/features/activity/pages/my-activities-page'

export const activityRoutes = (
  <Route path="/activities" element={<MyActivitiesPage />} />
)
