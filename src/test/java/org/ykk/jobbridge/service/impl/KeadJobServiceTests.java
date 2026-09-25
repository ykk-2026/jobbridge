package org.ykk.jobbridge.service.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeadJobServiceTests {

    @Test
    void normalizesZeroCareerToAny() {
        assertThat(KeadJobService.normalizeExperienceLevel("0년0개월")).isEqualTo("ANY");
        assertThat(KeadJobService.normalizeExperienceLevel("0년 0개월")).isEqualTo("ANY");
        assertThat(KeadJobService.normalizeExperienceLevel("0년개월")).isEqualTo("ANY");
        assertThat(KeadJobService.normalizeExperienceLevel("무관")).isEqualTo("ANY");
        assertThat(KeadJobService.normalizeExperienceLevel("1년0개월")).isEqualTo("1년0개월");
    }
}
