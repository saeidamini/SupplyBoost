import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/shipment">
        <Translate contentKey="global.menu.entities.distributeShipment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/inventory">
        <Translate contentKey="global.menu.entities.retailInventory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/invoice">
        <Translate contentKey="global.menu.entities.distributeInvoice" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notificationNotification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product-order">
        <Translate contentKey="global.menu.entities.retailProductOrder" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/customer">
        <Translate contentKey="global.menu.entities.retailCustomer" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item">
        <Translate contentKey="global.menu.entities.retailOrderItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product-category">
        <Translate contentKey="global.menu.entities.retailProductCategory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/company">
        <Translate contentKey="global.menu.entities.company" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product">
        <Translate contentKey="global.menu.entities.retailProduct" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
