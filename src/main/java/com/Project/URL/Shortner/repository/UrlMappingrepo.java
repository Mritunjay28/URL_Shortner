package com.Project.URL.Shortner.repository;

import com.Project.URL.Shortner.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingrepo extends JpaRepository< UrlMapping ,Long > {
    Optional<UrlMapping> findByShortCode(String ShortCode);

    boolean existsByShortCode(String ShortCode);
}
