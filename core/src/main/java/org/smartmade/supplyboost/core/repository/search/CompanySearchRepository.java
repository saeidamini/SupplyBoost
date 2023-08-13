package org.smartmade.supplyboost.core.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.smartmade.supplyboost.core.domain.Company;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Company} entity.
 */
public interface CompanySearchRepository extends ReactiveElasticsearchRepository<Company, Long>, CompanySearchRepositoryInternal {}

interface CompanySearchRepositoryInternal {
    Flux<Company> search(String query);
}

class CompanySearchRepositoryInternalImpl implements CompanySearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CompanySearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Company> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Company.class).map(SearchHit::getContent);
    }
}
