package com.zblog.repository;

import com.zblog.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Page<Media> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    Optional<Media> findByIdAndDeletedAtIsNull(Long id);

    List<Media> findByStoragePathIn(List<String> storagePaths);

    boolean existsByStoragePath(String storagePath);
}
