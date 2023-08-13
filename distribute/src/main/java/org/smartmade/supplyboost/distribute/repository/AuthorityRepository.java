package org.smartmade.supplyboost.distribute.repository;

import org.smartmade.supplyboost.distribute.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
