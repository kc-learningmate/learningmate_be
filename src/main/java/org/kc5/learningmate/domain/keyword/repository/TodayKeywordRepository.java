package org.kc5.learningmate.domain.keyword.repository;

import org.kc5.learningmate.domain.keyword.entity.TodaysKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TodayKeywordRepository extends JpaRepository<TodaysKeyword, Long> {
    @Query("SELECT tk FROM TodaysKeyword tk JOIN FETCH tk.keyword WHERE tk.date BETWEEN :startDate AND :endDate")
    List<TodaysKeyword> findByPeriodWithKeyword(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
