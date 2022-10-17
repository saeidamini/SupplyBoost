import shipment from 'app/entities/distribute/shipment/shipment.reducer';
import inventory from 'app/entities/retail/inventory/inventory.reducer';
import invoice from 'app/entities/distribute/invoice/invoice.reducer';
import notification from 'app/entities/notification/notification/notification.reducer';
import productOrder from 'app/entities/retail/product-order/product-order.reducer';
import customer from 'app/entities/retail/customer/customer.reducer';
import orderItem from 'app/entities/retail/order-item/order-item.reducer';
import productCategory from 'app/entities/retail/product-category/product-category.reducer';
import company from 'app/entities/company/company.reducer';
import product from 'app/entities/retail/product/product.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
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
};

export default entitiesReducers;
