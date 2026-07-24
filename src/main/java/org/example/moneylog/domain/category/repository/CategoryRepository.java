package org.example.moneylog.domain.category.repository;

import org.example.moneylog.domain.category.entity.Category;
import org.example.moneylog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);
}
