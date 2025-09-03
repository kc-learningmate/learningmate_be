package org.kc5.learningmate.domain.study.repository;

import org.kc5.learningmate.domain.study.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByKeywordId(Long keywordId);
}