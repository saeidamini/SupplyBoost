import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Inventory from './inventory';
import InventoryDetail from './inventory-detail';
import InventoryUpdate from './inventory-update';
import InventoryDeleteDialog from './inventory-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={InventoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={InventoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={InventoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={Inventory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={InventoryDeleteDialog} />
  </>
);

export default Routes;
