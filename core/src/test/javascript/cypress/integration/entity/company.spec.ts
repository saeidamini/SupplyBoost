import { entityItemSelector } from '../../support/commands';
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
    firstName: 'Adrienne',
    lastName: 'Mann',
    gender: 'FEMALE',
    email: '9}{{@"~8G4.le&l',
    phone: '(865) 768-0304',
    addressLine1: 'extend',
    city: 'Cathrineton',
    country: 'Cameroon',
    companyType: 'RETAILER',
  };

  let company: any;

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
      if (response!.body.length === 0) {
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
          expect(response!.statusCode).to.equal(200);
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
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      });

      it('edit button click should load edit Company page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Company');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
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
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
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
      cy.get(`[data-cy="firstName"]`).type('Neha').should('have.value', 'Neha');

      cy.get(`[data-cy="lastName"]`).type('Feest').should('have.value', 'Feest');

      cy.get(`[data-cy="gender"]`).select('FEMALE');

      cy.get(`[data-cy="email"]`).type('A=@}l.&lt;r').should('have.value', 'A=@}l.&lt;r');

      cy.get(`[data-cy="phone"]`).type('674-433-9422 x330').should('have.value', '674-433-9422 x330');

      cy.get(`[data-cy="addressLine1"]`).type('Berkshire Ngultrum deposit').should('have.value', 'Berkshire Ngultrum deposit');

      cy.get(`[data-cy="addressLine2"]`).type('Intranet').should('have.value', 'Intranet');

      cy.get(`[data-cy="city"]`).type('East Camilla').should('have.value', 'East Camilla');

      cy.get(`[data-cy="country"]`).type('Timor-Leste').should('have.value', 'Timor-Leste');

      cy.get(`[data-cy="companyType"]`).select('MANUFACTURER');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        company = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', companyPageUrlPattern);
    });
  });
});
