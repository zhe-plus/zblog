package com.zblog.repository;

import com.zblog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderBySortOrderAsc();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN Post p ON p.category = c AND p.status = 'PUBLIC' AND p.deletedAt IS NULL GROUP BY c")
    List<Object[]> findAllWithPostCount();
}
