package org.smartmade.supplyboost.core.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.smartmade.supplyboost.core.domain.Company;
import org.smartmade.supplyboost.core.repository.CompanyRepository;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Company} entity.
 */
public interface CompanySearchRepository extends ReactiveElasticsearchRepository<Company, Long>, CompanySearchRepositoryInternal {}

interface CompanySearchRepositoryInternal {
    Flux<Company> search(String query);

    Flux<Company> search(Query query);
}

class CompanySearchRepositoryInternalImpl implements CompanySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CompanySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Company> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Company> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Company.class).map(SearchHit::getContent);
    }
}
