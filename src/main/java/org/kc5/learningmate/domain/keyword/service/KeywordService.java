package org.kc5.learningmate.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.response.KeywordResponse;
import org.kc5.learningmate.api.v1.dto.response.TodaysKeywordResponse;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.keyword.entity.TodaysKeyword;
import org.kc5.learningmate.domain.keyword.repository.KeywordRepository;
import org.kc5.learningmate.domain.keyword.repository.TodayKeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final TodayKeywordRepository todayKeywordRepository;

    @Transactional(readOnly = true)
    public List<TodaysKeywordResponse> findByPeriodWithKeywords(LocalDate startDate, LocalDate endDate) {
        List<TodaysKeyword> todaysKeywords = todayKeywordRepository.findByPeriodWithKeyword(startDate, endDate);

        if (todaysKeywords.isEmpty())
            throw new CommonException(ErrorCode.KEYWORD_LIST_NOT_FOUND);

        return todaysKeywords.stream()
                             .map(TodaysKeywordResponse::fromEntityWithKeyword)
                             .toList();
    }


    @Transactional(readOnly = true)
    public KeywordResponse findById(Long id) {
        return keywordRepository.findById(id)
                                .map(KeywordResponse::fromEntity)
                                .orElseThrow(() -> new CommonException(ErrorCode.KEYWORD_NOT_FOUND));
    }

    public void validateKeywordExists(Long keywordId) {
        if (!keywordRepository.existsById(keywordId))
            throw new CommonException(ErrorCode.KEYWORD_NOT_FOUND);
    }

}
