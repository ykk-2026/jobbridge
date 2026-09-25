package org.ykk.jobbridge.recommendation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchNotesTests {

    @Test
    void changesExplanationOrderByScoreRange() {
        MatchNotes notes = new MatchNotes();
        notes.match("희망 직무 일치");
        notes.match("학력 조건 충족");
        notes.mismatch("요구 경력보다 3년 부족");

        String high = notes.recommendationReason(82);
        String middle = notes.recommendationReason(63);
        String low = notes.recommendationReason(42);

        assertThat(high).startsWith("희망 직무 일치").contains("다만", "3년 부족");
        assertThat(middle).contains("일부 조건은 일치하거나 충족", "3년 부족");
        assertThat(low).startsWith("요구 경력보다 3년 부족").contains("주요 조건에서 차이");
    }
}
