package com.zblog.repository;

import com.zblog.entity.PostVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVersionRepository extends JpaRepository<PostVersion, Long> {
}
