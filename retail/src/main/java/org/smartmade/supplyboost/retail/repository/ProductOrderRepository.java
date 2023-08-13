package org.smartmade.supplyboost.retail.repository;

import java.util.List;
import java.util.Optional;
import org.smartmade.supplyboost.retail.domain.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProductOrder entity.
 */
@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    default Optional<ProductOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct productOrder from ProductOrder productOrder left join fetch productOrder.customer",
        countQuery = "select count(distinct productOrder) from ProductOrder productOrder"
    )
    Page<ProductOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct productOrder from ProductOrder productOrder left join fetch productOrder.customer")
    List<ProductOrder> findAllWithToOneRelationships();

    @Query("select productOrder from ProductOrder productOrder left join fetch productOrder.customer where productOrder.id =:id")
    Optional<ProductOrder> findOneWithToOneRelationships(@Param("id") Long id);
}
