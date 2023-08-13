package org.smartmade.supplyboost.core.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartmade.supplyboost.core.domain.Company;
import org.smartmade.supplyboost.core.repository.CompanyRepository;
import org.smartmade.supplyboost.core.repository.search.CompanySearchRepository;
import org.smartmade.supplyboost.core.web.rest.errors.BadRequestAlertException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link org.smartmade.supplyboost.core.domain.Company}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CompanyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyResource.class);

    private static final String ENTITY_NAME = "company";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    public CompanyResource(CompanyRepository companyRepository, CompanySearchRepository companySearchRepository) {
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * {@code POST  /companies} : Create a new company.
     *
     * @param company the company to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new company, or with status {@code 400 (Bad Request)} if the company has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/companies")
    public Mono<ResponseEntity<Company>> createCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        log.debug("REST request to save Company : {}", company);
        if (company.getId() != null) {
            throw new BadRequestAlertException("A new company cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return companyRepository
            .save(company)
            .flatMap(companySearchRepository::save)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/companies/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /companies/:id} : Updates an existing company.
     *
     * @param id the id of the company to save.
     * @param company the company to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated company,
     * or with status {@code 400 (Bad Request)} if the company is not valid,
     * or with status {@code 500 (Internal Server Error)} if the company couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/companies/{id}")
    public Mono<ResponseEntity<Company>> updateCompany(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Company company
    ) throws URISyntaxException {
        log.debug("REST request to update Company : {}, {}", id, company);
        if (company.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, company.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return companyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return companyRepository
                    .save(company)
                    .flatMap(companySearchRepository::save)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /companies/:id} : Partial updates given fields of an existing company, field will ignore if it is null
     *
     * @param id the id of the company to save.
     * @param company the company to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated company,
     * or with status {@code 400 (Bad Request)} if the company is not valid,
     * or with status {@code 404 (Not Found)} if the company is not found,
     * or with status {@code 500 (Internal Server Error)} if the company couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/companies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Company>> partialUpdateCompany(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Company company
    ) throws URISyntaxException {
        log.debug("REST request to partial update Company partially : {}, {}", id, company);
        if (company.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, company.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return companyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Company> result = companyRepository
                    .findById(company.getId())
                    .map(existingCompany -> {
                        if (company.getFirstName() != null) {
                            existingCompany.setFirstName(company.getFirstName());
                        }
                        if (company.getLastName() != null) {
                            existingCompany.setLastName(company.getLastName());
                        }
                        if (company.getGender() != null) {
                            existingCompany.setGender(company.getGender());
                        }
                        if (company.getEmail() != null) {
                            existingCompany.setEmail(company.getEmail());
                        }
                        if (company.getPhone() != null) {
                            existingCompany.setPhone(company.getPhone());
                        }
                        if (company.getAddressLine1() != null) {
                            existingCompany.setAddressLine1(company.getAddressLine1());
                        }
                        if (company.getAddressLine2() != null) {
                            existingCompany.setAddressLine2(company.getAddressLine2());
                        }
                        if (company.getCity() != null) {
                            existingCompany.setCity(company.getCity());
                        }
                        if (company.getCountry() != null) {
                            existingCompany.setCountry(company.getCountry());
                        }
                        if (company.getCompanyType() != null) {
                            existingCompany.setCompanyType(company.getCompanyType());
                        }

                        return existingCompany;
                    })
                    .flatMap(companyRepository::save)
                    .flatMap(savedCompany -> {
                        companySearchRepository.save(savedCompany);

                        return Mono.just(savedCompany);
                    });

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /companies} : get all the companies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companies in body.
     */
    @GetMapping("/companies")
    public Mono<List<Company>> getAllCompanies() {
        log.debug("REST request to get all Companies");
        return companyRepository.findAll().collectList();
    }

    /**
     * {@code GET  /companies} : get all the companies as a stream.
     * @return the {@link Flux} of companies.
     */
    @GetMapping(value = "/companies", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Company> getAllCompaniesAsStream() {
        log.debug("REST request to get all Companies as a stream");
        return companyRepository.findAll();
    }

    /**
     * {@code GET  /companies/:id} : get the "id" company.
     *
     * @param id the id of the company to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the company, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/companies/{id}")
    public Mono<ResponseEntity<Company>> getCompany(@PathVariable Long id) {
        log.debug("REST request to get Company : {}", id);
        Mono<Company> company = companyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(company);
    }

    /**
     * {@code DELETE  /companies/:id} : delete the "id" company.
     *
     * @param id the id of the company to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/companies/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCompany(@PathVariable Long id) {
        log.debug("REST request to delete Company : {}", id);
        return companyRepository
            .deleteById(id)
            .then(companySearchRepository.deleteById(id))
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }

    /**
     * {@code SEARCH  /_search/companies?query=:query} : search for the company corresponding
     * to the query.
     *
     * @param query the query of the company search.
     * @return the result of the search.
     */
    @GetMapping("/_search/companies")
    public Mono<List<Company>> searchCompanies(@RequestParam String query) {
        log.debug("REST request to search Companies for query {}", query);
        return companySearchRepository.search(query).collectList();
    }
}
