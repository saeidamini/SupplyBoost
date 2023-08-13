import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import shipment from 'app/entities/distribute/shipment/shipment.reducer';
// prettier-ignore
import inventory from 'app/entities/retail/inventory/inventory.reducer';
// prettier-ignore
import invoice from 'app/entities/distribute/invoice/invoice.reducer';
// prettier-ignore
import notification from 'app/entities/notification/notification/notification.reducer';
// prettier-ignore
import productOrder from 'app/entities/retail/product-order/product-order.reducer';
// prettier-ignore
import customer from 'app/entities/retail/customer/customer.reducer';
// prettier-ignore
import orderItem from 'app/entities/retail/order-item/order-item.reducer';
// prettier-ignore
import productCategory from 'app/entities/retail/product-category/product-category.reducer';
// prettier-ignore
import company from 'app/entities/company/company.reducer';
// prettier-ignore
import product from 'app/entities/retail/product/product.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  shipment,
  inventory,
  invoice,
  notification,
  productOrder,
  customer,
  orderItem,
  productCategory,
  company,
  product,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
