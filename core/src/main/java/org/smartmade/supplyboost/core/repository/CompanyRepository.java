package org.smartmade.supplyboost.core.repository;

import org.smartmade.supplyboost.core.domain.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends ReactiveCrudRepository<Company, Long>, CompanyRepositoryInternal {
    @Override
    <S extends Company> Mono<S> save(S entity);

    @Override
    Flux<Company> findAll();

    @Override
    Mono<Company> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CompanyRepositoryInternal {
    <S extends Company> Mono<S> save(S entity);

    Flux<Company> findAllBy(Pageable pageable);

    Flux<Company> findAll();

    Mono<Company> findById(Long id);

    Flux<Company> findAllBy(Pageable pageable, Criteria criteria);
}
