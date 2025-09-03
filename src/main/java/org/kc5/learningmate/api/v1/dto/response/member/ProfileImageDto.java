package org.kc5.learningmate.api.v1.dto.response.member;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record ProfileImageDto(Resource image, MediaType mediaType) {
}
