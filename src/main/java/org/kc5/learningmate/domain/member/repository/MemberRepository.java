package org.kc5.learningmate.domain.member.repository;

import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("""
                select new org.kc5.learningmate.domain.auth.entity.MemberDetail(
                       m.id, m.email, m.passwordHash
                    )
                    from Member m
                    where m.email = :email
            """)
    Optional<MemberDetail> findMemberDetailByEmail(@Param("email") String email);

    @Query("select id from Member where email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickname);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
               update Member as m
                  set m.deletedAt=now(),
                      m.email=concat("deleted_", m.id,'_', now()) ,
                      m.nickname="탈퇴한 사용자",
                      m.passwordHash=null,
                      m.imageUrl=null,
                      m.status=false
                  where m.id = :memberId
            """)
    void deleteMember(@Param("memberId") Long memberId);
}
