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

describe('Company e2e test', () => {
  const companyPageUrl = '/company';
  const companyPageUrlPattern = new RegExp('/company(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const companySample = {
    firstName: 'Cicero',
    lastName: 'Schneider',
    gender: 'OTHER',
    email: 'DD6#@B.`]]',
    phone: '(200) 480-4428 x835',
    addressLine1: 'withdrawal Berkshire',
    city: 'East Jamie',
    country: 'Somalia',
    companyType: 'RETAILER',
  };

  let company;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/companies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/companies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/companies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (company) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/companies/${company.id}`,
      }).then(() => {
        company = undefined;
      });
    }
  });

  it('Companies menu should load Companies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('company');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Company').should('exist');
    cy.url().should('match', companyPageUrlPattern);
  });

  describe('Company page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(companyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Company page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/company/new$'));
        cy.getEntityCreateUpdateHeading('Company');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/companies',
          body: companySample,
        }).then(({ body }) => {
          company = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/companies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [company],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(companyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Company page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('company');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      });

      it('edit button click should load edit Company page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Company');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      });

      it('edit button click should load edit Company page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Company');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      });

      it('last delete button click should delete instance of Company', () => {
        cy.intercept('GET', '/api/companies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('company').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);

        company = undefined;
      });
    });
  });

  describe('new Company page', () => {
    beforeEach(() => {
      cy.visit(`${companyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Company');
    });

    it('should create an instance of Company', () => {
      cy.get(`[data-cy="firstName"]`).type('Garth').should('have.value', 'Garth');

      cy.get(`[data-cy="lastName"]`).type('Heathcote').should('have.value', 'Heathcote');

      cy.get(`[data-cy="gender"]`).select('FEMALE');

      cy.get(`[data-cy="email"]`).type('#D&lt;ePm@6sp_bm...!?&lt;').should('have.value', '#D&lt;ePm@6sp_bm...!?&lt;');

      cy.get(`[data-cy="phone"]`).type('1-462-545-7833 x18490').should('have.value', '1-462-545-7833 x18490');

      cy.get(`[data-cy="addressLine1"]`).type('Regional Product navigating').should('have.value', 'Regional Product navigating');

      cy.get(`[data-cy="addressLine2"]`)
        .type('infomediaries Lebanese Innovative')
        .should('have.value', 'infomediaries Lebanese Innovative');

      cy.get(`[data-cy="city"]`).type('New Cristian').should('have.value', 'New Cristian');

      cy.get(`[data-cy="country"]`).type('French Guiana').should('have.value', 'French Guiana');

      cy.get(`[data-cy="companyType"]`).select('RETAILER');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        company = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', companyPageUrlPattern);
    });
  });
});
