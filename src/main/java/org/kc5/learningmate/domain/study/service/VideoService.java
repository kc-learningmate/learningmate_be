package org.kc5.learningmate.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.response.VideoResponse;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.keyword.repository.KeywordRepository;
import org.kc5.learningmate.domain.keyword.service.KeywordService;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.kc5.learningmate.domain.study.StudyBits;
import org.kc5.learningmate.domain.study.repository.StudyRepository;
import org.kc5.learningmate.domain.study.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.kc5.learningmate.common.exception.ErrorCode.KEYWORD_NOT_FOUND;
import static org.kc5.learningmate.common.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final KeywordService keywordService;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public VideoResponse findByKeywordId(Long keywordId) {
        keywordService.validateKeywordExists(keywordId);

        return videoRepository.findByKeywordId(keywordId)
                              .map(VideoResponse::fromEntity)
                              .orElseThrow(() -> new CommonException(ErrorCode.VIDEO_BY_KEYWORD_ID_NOT_FOUND));
    }

    @Transactional
    public void upsertFlag(Long keywordId, Long memberId) {
        if (!memberRepository.existsById(memberId)) throw new CommonException(MEMBER_NOT_FOUND);
        if (!keywordRepository.existsById(keywordId)) throw new CommonException(KEYWORD_NOT_FOUND);
        studyRepository.upsertFlag(memberId,
                keywordId, StudyBits.VIDEO);
    }
}
