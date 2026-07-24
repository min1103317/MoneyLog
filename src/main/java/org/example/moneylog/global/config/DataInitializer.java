package org.example.moneylog.global.config;

import org.example.moneylog.domain.category.entity.Category;
import org.example.moneylog.domain.category.entity.CategoryType;
import org.example.moneylog.domain.category.repository.CategoryRepository;
import org.example.moneylog.domain.transaction.entity.Transaction;
import org.example.moneylog.domain.transaction.repository.TransactionRepository;
import org.example.moneylog.domain.user.entity.User;
import org.example.moneylog.domain.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) {
        User user = userRepository.save(User.builder()
                .email("test@likelion.com")
                .password("temp_password")
                .nickname("머니로그유저")
                .build());

        Category food = categoryRepository.save(Category.builder()
                .user(user)
                .name("식비")
                .type(CategoryType.EXPENSE)
                .build());

        transactionRepository.save(Transaction.builder()
                .user(user)
                .category(food)
                .type(CategoryType.EXPENSE)
                .amount(12000L)
                .description("점심 김치찌개")
                .transactionDate(LocalDate.of(2026, 7, 8))
                .build());

        log.info("저장된 거래 수 = {}", transactionRepository.count());
    }
}