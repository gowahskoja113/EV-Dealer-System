package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealershipRepository extends JpaRepository<Dealership, Long> {

    @Query("""
    select d.name 
    from Dealership d 
    where d.status = com.swp391.evdealersystem.enums.DealershipStatus.ACTIVE
    order by d.dealershipId asc limit 1
""")
        Optional<String> findDefaultDealerShip();

}