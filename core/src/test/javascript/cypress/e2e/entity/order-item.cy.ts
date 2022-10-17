import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('OrderItem e2e test', () => {
  const orderItemPageUrl = '/order-item';
  const orderItemPageUrlPattern = new RegExp('/order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const orderItemSample = {"quantity":30777,"totalPrice":60954,"status":"OUT_OF_STOCK"};

  let orderItem;
  // let product;
  // let productOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/retail/api/products',
      body: {"name":"infrastructures","description":"background Baby","price":50830,"itemSize":"XXL","image":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","imageContentType":"unknown"},
    }).then(({ body }) => {
      product = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/retail/api/product-orders',
      body: {"placedDate":"2022-10-17T03:57:20.469Z","status":"PENDING","code":"salmon Junctions","invoiceId":55261,"customer":"protocol Re-contextualized"},
    }).then(({ body }) => {
      productOrder = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/services/retail/api/order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/retail/api/order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/services/retail/api/order-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/services/retail/api/products', {
      statusCode: 200,
      body: [product],
    });

    cy.intercept('GET', '/services/retail/api/product-orders', {
      statusCode: 200,
      body: [productOrder],
    });

  });
   */

  afterEach(() => {
    if (orderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/retail/api/order-items/${orderItem.id}`,
      }).then(() => {
        orderItem = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/retail/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
    if (productOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/retail/api/product-orders/${productOrder.id}`,
      }).then(() => {
        productOrder = undefined;
      });
    }
  });
   */

  it('OrderItems menu should load OrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderItem').should('exist');
    cy.url().should('match', orderItemPageUrlPattern);
  });

  describe('OrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-item/new$'));
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/retail/api/order-items',
          body: {
            ...orderItemSample,
            product: product,
            order: productOrder,
          },
        }).then(({ body }) => {
          orderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/retail/api/order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/retail/api/order-items?page=0&size=20>; rel="last",<http://localhost/services/retail/api/order-items?page=0&size=20>; rel="first"',
              },
              body: [orderItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(orderItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details OrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('edit button click should load edit OrderItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('edit button click should load edit OrderItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of OrderItem', () => {
        cy.intercept('GET', '/services/retail/api/order-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('orderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);

        orderItem = undefined;
      });
    });
  });

  describe('new OrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${orderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderItem');
    });

    it.skip('should create an instance of OrderItem', () => {
      cy.get(`[data-cy="quantity"]`).type('25344').should('have.value', '25344');

      cy.get(`[data-cy="totalPrice"]`).type('85865').should('have.value', '85865');

      cy.get(`[data-cy="status"]`).select('AVAILABLE');

      cy.get(`[data-cy="product"]`).select(1);
      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        orderItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', orderItemPageUrlPattern);
    });
  });
});
