package org.example.moneylog.domain.transaction.repository;

import org.example.moneylog.domain.transaction.entity.Transaction;
import org.example.moneylog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate start, LocalDate end, Pageable pageable);

    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate start, LocalDate end);

    Optional<Transaction> findByIdAndUser(Long id, User user);
}
