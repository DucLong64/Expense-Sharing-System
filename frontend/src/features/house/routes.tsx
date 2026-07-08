import { Route } from 'react-router-dom'
import { activityRoutes } from '@/features/activity/routes'
import { HouseDetailPage } from '@/features/house/pages/house-detail-page'
import { HousesPage } from '@/features/house/pages/houses-page'

export const houseRoutes = (
  <>
    <Route path="/" element={<HousesPage />} />
    <Route path="/houses/:houseId" element={<HouseDetailPage />} />
    {activityRoutes}
  </>
)
