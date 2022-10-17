import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';

import entitiesReducers from './reducers';

import Shipment from './distribute/shipment';
import Inventory from './retail/inventory';
import Invoice from './distribute/invoice';
import Notification from './notification/notification';
import ProductOrder from './retail/product-order';
import Customer from './retail/customer';
import OrderItem from './retail/order-item';
import ProductCategory from './retail/product-category';
import Company from './company';
import Product from './retail/product';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const store = getStore();
  store.injectReducer('core', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="shipment/*" element={<Shipment />} />
        <Route path="inventory/*" element={<Inventory />} />
        <Route path="invoice/*" element={<Invoice />} />
        <Route path="notification/*" element={<Notification />} />
        <Route path="product-order/*" element={<ProductOrder />} />
        <Route path="customer/*" element={<Customer />} />
        <Route path="order-item/*" element={<OrderItem />} />
        <Route path="product-category/*" element={<ProductCategory />} />
        <Route path="company/*" element={<Company />} />
        <Route path="product/*" element={<Product />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
