package org.kc5.learningmate.api.v1.dto.response;

import lombok.Builder;
import lombok.Value;
import org.kc5.learningmate.domain.study.entity.Video;


/**
 * DTO for {@link org.kc5.learningmate.domain.study.entity.Video}
 */
@Value
@Builder
public class VideoResponse {
    Long id;
    String link;

    public static VideoResponse fromEntity(Video video) {
        return VideoResponse.builder()
                            .id(video.getId())
                            .link(video.getLink())
                            .build();
    }
}