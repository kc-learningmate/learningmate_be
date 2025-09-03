package org.kc5.learningmate.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.response.MyStudyResponse;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.study.entity.Study;
import org.kc5.learningmate.domain.study.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    @Transactional(readOnly = true)
    public List<MyStudyResponse> getStudyStatus(Long memberId, int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = firstDay.plusMonths(1).atStartOfDay();

        return studyRepository.getMyStudyStatus(memberId, start, end)
                .stream()
                .map(r -> MyStudyResponse.of(
                        r.id(), r.keywordId(), r.studyStats(), r.createdAt(), r.updatedAt()
                ))
                .toList();

    }

}
