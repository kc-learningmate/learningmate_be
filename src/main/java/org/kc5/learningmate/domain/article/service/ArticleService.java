package org.kc5.learningmate.domain.article.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.member.MemberQuizRequest;
import org.kc5.learningmate.api.v1.dto.response.ArticlePreviewResponse;
import org.kc5.learningmate.api.v1.dto.response.ArticleResponse;
import org.kc5.learningmate.api.v1.dto.response.QuizResponse;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.article.repository.ArticleRepository;
import org.kc5.learningmate.domain.keyword.service.KeywordService;
import org.kc5.learningmate.domain.member.entity.Member;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.kc5.learningmate.domain.quiz.entity.MemberQuiz;
import org.kc5.learningmate.domain.quiz.entity.Quiz;
import org.kc5.learningmate.domain.quiz.repository.MemberQuizRepository;
import org.kc5.learningmate.domain.quiz.repository.QuizRepository;
import org.kc5.learningmate.domain.study.StudyBits;
import org.kc5.learningmate.domain.study.repository.StudyRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final QuizRepository quizRepository;
    private final MemberQuizRepository memberQuizRepository;
    private final MemberRepository memberRepository;
    private final KeywordService keywordService;
    private final StudyRepository studyRepository;

    @Cacheable(value = "articleQuizzes", key = "#articleId", unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizList(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new CommonException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        List<Quiz> quizList = quizRepository.findByArticleId(articleId);

        if (quizList.isEmpty()) {
            throw new CommonException(ErrorCode.QUIZ_LIST_NOT_FOUND);
        }

        return QuizResponse.from(quizList);

    }

    @Transactional(readOnly = true)
    public ArticleResponse findById(Long articleId) {
        return articleRepository.findById(articleId)
                                .map(ArticleResponse::from)
                                .orElseThrow(() -> new CommonException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ArticlePreviewResponse> findArticlePreviewByKeywordId(Long keywordId) {
        keywordService.validateKeywordExists(keywordId);

        return articleRepository.findArticlePreviewByKeywordId(keywordId);
    }

    public void validateArticleExists(Long articleId) {
        if (!articleRepository.existsById(articleId))
            throw new CommonException(ErrorCode.ARTICLE_NOT_FOUND);
    }

    @Transactional
    public QuizResponse solveQuiz(Long articleId, Long quizId, MemberQuizRequest req, Long memberId) {

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() ->
                                                new CommonException(ErrorCode.MEMBER_NOT_FOUND)
                                        );

        Quiz quiz = quizRepository.findById(quizId)
                                  .orElseThrow(() -> new CommonException(ErrorCode.QUIZ_NOT_FOUND));

        if (!quiz.getArticle().getId().equals(articleId)) {
            throw new CommonException(ErrorCode.BAD_REQUEST);
        }

        boolean isExist = memberQuizRepository.existsSolved(quizId, memberId);

        boolean isCorrect = java.util.Objects.equals(quiz.getAnswer(), req.getMemberAnswer());

        if (!isExist) {
            MemberQuiz newMemberQuiz = MemberQuiz.builder()
                                                 .quiz(quiz)
                                                 .member(member)
                                                 .memberAnswer(req.getMemberAnswer())
                                                 .build();
            memberQuizRepository.save(newMemberQuiz);
        } else {
            memberQuizRepository.updateAnswer(quizId, memberId, req.getMemberAnswer());
        }

        // 내가 푼 개수
        long solved = memberQuizRepository.countSolvedByArticleAndMember(articleId, memberId);
        long total = 5L;

        Long keywordId = quizRepository.findKeywordIdByQuizId(quizId);
        // 모두 풀었을 때만 Study 비트 세팅
        if (solved == total) {
            studyRepository.upsertFlag(memberId, keywordId, StudyBits.QUIZ);
        }

        return QuizResponse.from(quiz, isCorrect, req.getMemberAnswer());

    }
}
