package org.smartmade.supplyboost.notification.repository;

import java.util.List;
import java.util.Optional;
import org.smartmade.supplyboost.notification.domain.User;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findOneByLogin(String login);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
