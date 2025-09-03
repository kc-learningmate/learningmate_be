package org.kc5.learningmate.domain.study.repository;

import org.kc5.learningmate.api.v1.dto.response.MyStudyResponse;
import org.kc5.learningmate.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =
            """ 
            INSERT INTO study
            (member_id, keyword_id, study_stats, created_at, updated_at) 
            VALUES 
            (:memberId, :keywordId, :flag, NOW(6), NOW(6)) 
            ON DUPLICATE KEY UPDATE 
            study_stats = study.study_stats | VALUES(study_stats), updated_at = NOW(6) 
            """, nativeQuery = true)
    void upsertFlag(@Param("memberId") Long memberId, @Param("keywordId") Long keywordId, @Param("flag") int flag);

    @Query("""
        select new org.kc5.learningmate.api.v1.dto.response.MyStudyResponse (
            s.id,
            s.keyword.id,
            s.studyStats,
            0,
            false,
            false,
            false,
            s.createdAt,
            s.updatedAt
        )
        from Study s
        where s.member.id = :memberId
          and s.createdAt >= :start
          and s.createdAt < :end
        order by s.createdAt asc
    """)
    List<MyStudyResponse> getMyStudyStatus(@Param("memberId") Long memberId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
