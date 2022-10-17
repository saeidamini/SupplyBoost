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

describe('Customer e2e test', () => {
  const customerPageUrl = '/customer';
  const customerPageUrlPattern = new RegExp('/customer(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const customerSample = {"firstName":"Amaya","lastName":"Green","gender":"MALE","email":"#2j@N.C=-","phone":"266-839-7608 x29982","addressLine1":"matrix Concrete","city":"West Danial","country":"Liechtenstein"};

  let customer;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/retail/api/users',
      body: {"login":"scalable Curve product","firstName":"Blair","lastName":"Fritsch"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/services/retail/api/customers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/retail/api/customers').as('postEntityRequest');
    cy.intercept('DELETE', '/services/retail/api/customers/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/services/retail/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/retail/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/retail/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('Customers menu should load Customers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('customer');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Customer').should('exist');
    cy.url().should('match', customerPageUrlPattern);
  });

  describe('Customer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(customerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Customer page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/customer/new$'));
        cy.getEntityCreateUpdateHeading('Customer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', customerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/retail/api/customers',
          body: {
            ...customerSample,
            user: user,
          },
        }).then(({ body }) => {
          customer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/retail/api/customers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/retail/api/customers?page=0&size=20>; rel="last",<http://localhost/services/retail/api/customers?page=0&size=20>; rel="first"',
              },
              body: [customer],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(customerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(customerPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Customer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('customer');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', customerPageUrlPattern);
      });

      it('edit button click should load edit Customer page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Customer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', customerPageUrlPattern);
      });

      it('edit button click should load edit Customer page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Customer');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', customerPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Customer', () => {
        cy.intercept('GET', '/services/retail/api/customers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('customer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', customerPageUrlPattern);

        customer = undefined;
      });
    });
  });

  describe('new Customer page', () => {
    beforeEach(() => {
      cy.visit(`${customerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Customer');
    });

    it.skip('should create an instance of Customer', () => {
      cy.get(`[data-cy="firstName"]`).type('Alvah').should('have.value', 'Alvah');

      cy.get(`[data-cy="lastName"]`).type('Schneider').should('have.value', 'Schneider');

      cy.get(`[data-cy="gender"]`).select('MALE');

      cy.get(`[data-cy="email"]`).type('75@oF.//3xh').should('have.value', '75@oF.//3xh');

      cy.get(`[data-cy="phone"]`).type('1-261-461-6768').should('have.value', '1-261-461-6768');

      cy.get(`[data-cy="addressLine1"]`).type('Gloves').should('have.value', 'Gloves');

      cy.get(`[data-cy="addressLine2"]`).type('strategize').should('have.value', 'strategize');

      cy.get(`[data-cy="city"]`).type('North Ivy').should('have.value', 'North Ivy');

      cy.get(`[data-cy="country"]`).type('Swaziland').should('have.value', 'Swaziland');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        customer = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', customerPageUrlPattern);
    });
  });
});
