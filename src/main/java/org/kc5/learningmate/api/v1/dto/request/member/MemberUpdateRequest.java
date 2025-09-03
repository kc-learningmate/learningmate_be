package org.kc5.learningmate.api.v1.dto.request.member;

import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(@Size(min = 8, max = 128) String password,
                                  @Size(min = 1, max = 50) String nickname) {
}
