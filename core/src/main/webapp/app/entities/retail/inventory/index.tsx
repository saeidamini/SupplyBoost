import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Inventory from './inventory';
import InventoryDetail from './inventory-detail';
import InventoryUpdate from './inventory-update';
import InventoryDeleteDialog from './inventory-delete-dialog';

const InventoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Inventory />} />
    <Route path="new" element={<InventoryUpdate />} />
    <Route path=":id">
      <Route index element={<InventoryDetail />} />
      <Route path="edit" element={<InventoryUpdate />} />
      <Route path="delete" element={<InventoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InventoryRoutes;
