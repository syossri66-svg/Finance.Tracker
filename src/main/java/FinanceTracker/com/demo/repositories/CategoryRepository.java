package FinanceTracker.com.demo.repositories;

import FinanceTracker.com.demo.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

        Optional<Category> findByIdAndUserId(Long id, Long userId);
}
