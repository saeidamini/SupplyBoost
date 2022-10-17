import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Notification from './notification';
import NotificationDetail from './notification-detail';
import NotificationUpdate from './notification-update';
import NotificationDeleteDialog from './notification-delete-dialog';

const NotificationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Notification />} />
    <Route path="new" element={<NotificationUpdate />} />
    <Route path=":id">
      <Route index element={<NotificationDetail />} />
      <Route path="edit" element={<NotificationUpdate />} />
      <Route path="delete" element={<NotificationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NotificationRoutes;
