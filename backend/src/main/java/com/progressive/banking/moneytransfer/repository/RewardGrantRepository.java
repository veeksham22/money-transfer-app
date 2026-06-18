package com.progressive.banking.moneytransfer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.progressive.banking.moneytransfer.domain.entities.RewardGrant;

public interface RewardGrantRepository extends JpaRepository<RewardGrant, Long> {

    boolean existsByTransactionId(Integer transactionId);

    Optional<RewardGrant> findByTransactionId(Integer transactionId);

    List<RewardGrant> findByUsernameOrderByGrantedAtDesc(String username);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM RewardGrant r WHERE r.username = :username")
    long sumPointsByUsername(@Param("username") String username);

    long countByUsername(String username);
}
