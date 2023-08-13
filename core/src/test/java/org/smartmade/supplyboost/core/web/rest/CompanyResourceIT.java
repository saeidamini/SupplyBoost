package org.smartmade.supplyboost.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smartmade.supplyboost.core.IntegrationTest;
import org.smartmade.supplyboost.core.domain.Company;
import org.smartmade.supplyboost.core.domain.enumeration.CompanyType;
import org.smartmade.supplyboost.core.domain.enumeration.Gender;
import org.smartmade.supplyboost.core.repository.CompanyRepository;
import org.smartmade.supplyboost.core.repository.EntityManager;
import org.smartmade.supplyboost.core.repository.search.CompanySearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CompanyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CompanyResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "l@MzES.1k";
    private static final String UPDATED_EMAIL = "+q*X9@Aajb.t|p";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final CompanyType DEFAULT_COMPANY_TYPE = CompanyType.SUPPLIER;
    private static final CompanyType UPDATED_COMPANY_TYPE = CompanyType.RETAILER;

    private static final String ENTITY_API_URL = "/api/companies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/companies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * This repository is mocked in the org.smartmade.supplyboost.core.repository.search test package.
     *
     * @see org.smartmade.supplyboost.core.repository.search.CompanySearchRepositoryMockConfiguration
     */
    @Autowired
    private CompanySearchRepository mockCompanySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Company company;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createEntity(EntityManager em) {
        Company company = new Company()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .companyType(DEFAULT_COMPANY_TYPE);
        return company;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Company createUpdatedEntity(EntityManager em) {
        Company company = new Company()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .companyType(UPDATED_COMPANY_TYPE);
        return company;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Company.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        company = createEntity(em);
    }

    @Test
    void createCompany() throws Exception {
        int databaseSizeBeforeCreate = companyRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCompanySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Company
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate + 1);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCompany.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCompany.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testCompany.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCompany.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCompany.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testCompany.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testCompany.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCompany.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testCompany.getCompanyType()).isEqualTo(DEFAULT_COMPANY_TYPE);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).save(testCompany);
    }

    @Test
    void createCompanyWithExistingId() throws Exception {
        // Create the Company with an existing ID
        company.setId(1L);

        int databaseSizeBeforeCreate = companyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setFirstName(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setLastName(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setGender(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setEmail(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setPhone(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setAddressLine1(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setCity(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setCountry(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCompanyTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = companyRepository.findAll().collectList().block().size();
        // set the field null
        company.setCompanyType(null);

        // Create the Company, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCompaniesAsStream() {
        // Initialize the database
        companyRepository.save(company).block();

        List<Company> companyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Company.class)
            .getResponseBody()
            .filter(company::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(companyList).isNotNull();
        assertThat(companyList).hasSize(1);
        Company testCompany = companyList.get(0);
        assertThat(testCompany.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCompany.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCompany.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testCompany.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCompany.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCompany.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testCompany.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testCompany.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCompany.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testCompany.getCompanyType()).isEqualTo(DEFAULT_COMPANY_TYPE);
    }

    @Test
    void getAllCompanies() {
        // Initialize the database
        companyRepository.save(company).block();

        // Get all the companyList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(company.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].companyType")
            .value(hasItem(DEFAULT_COMPANY_TYPE.toString()));
    }

    @Test
    void getCompany() {
        // Initialize the database
        companyRepository.save(company).block();

        // Get the company
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, company.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(company.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.addressLine1")
            .value(is(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.addressLine2")
            .value(is(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.companyType")
            .value(is(DEFAULT_COMPANY_TYPE.toString()));
    }

    @Test
    void getNonExistingCompany() {
        // Get the company
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCompany() throws Exception {
        // Configure the mock search repository
        when(mockCompanySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company
        Company updatedCompany = companyRepository.findById(company.getId()).block();
        updatedCompany
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .companyType(UPDATED_COMPANY_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCompany.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCompany.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCompany.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCompany.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompany.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCompany.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCompany.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCompany.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCompany.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testCompany.getCompanyType()).isEqualTo(UPDATED_COMPANY_TYPE);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository).save(testCompany);
    }

    @Test
    void putNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, company.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void putWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void putWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void partialUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .firstName(UPDATED_FIRST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCompany.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCompany.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCompany.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompany.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCompany.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCompany.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCompany.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCompany.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testCompany.getCompanyType()).isEqualTo(DEFAULT_COMPANY_TYPE);
    }

    @Test
    void fullUpdateCompanyWithPatch() throws Exception {
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();

        // Update the company using partial update
        Company partialUpdatedCompany = new Company();
        partialUpdatedCompany.setId(company.getId());

        partialUpdatedCompany
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .companyType(UPDATED_COMPANY_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCompany.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCompany))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCompany.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCompany.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCompany.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompany.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCompany.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCompany.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCompany.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCompany.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testCompany.getCompanyType()).isEqualTo(UPDATED_COMPANY_TYPE);
    }

    @Test
    void patchNonExistingCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, company.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void patchWithIdMismatchCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void patchWithMissingIdPathParamCompany() throws Exception {
        int databaseSizeBeforeUpdate = companyRepository.findAll().collectList().block().size();
        company.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(company))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    void deleteCompany() {
        // Configure the mock search repository
        when(mockCompanySearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        companyRepository.save(company).block();

        int databaseSizeBeforeDelete = companyRepository.findAll().collectList().block().size();

        // Delete the company
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, company.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Company> companyList = companyRepository.findAll().collectList().block();
        assertThat(companyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).deleteById(company.getId());
    }

    @Test
    void searchCompany() {
        // Configure the mock search repository
        when(mockCompanySearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        companyRepository.save(company).block();
        when(mockCompanySearchRepository.search("id:" + company.getId())).thenReturn(Flux.just(company));

        // Search the company
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + company.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(company.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].companyType")
            .value(hasItem(DEFAULT_COMPANY_TYPE.toString()));
    }
}
