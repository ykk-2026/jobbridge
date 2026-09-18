package org.ykk.jobbridge.recommendation;

import java.util.ArrayList;
import java.util.List;

/**
 * 점수 계산 과정에서 나온 근거 문장을 모아 두고, 마지막에 추천 문구를 만든다.
 * <p>
 * 각 근거는 두 가지 표현을 가진다.
 * <ul>
 *   <li>summary: 점수 없이 사람이 읽는 문장 → recommendationReason에 사용</li>
 *   <li>detail: "문장: 10/15점" 형태 → mismatchReason에 사용</li>
 * </ul>
 * 점수를 받는 메서드는 기록 후 그 점수를 그대로 돌려주므로 {@code return notes.match(...)} 형태로 쓴다.
 */
final class MatchNotes {

    private record Note(String summary, String detail) {
    }

    private final List<Note> matches = new ArrayList<>();
    private final List<Note> mismatches = new ArrayList<>();

    /** 점수 표기가 없는 근거. 예: "재택근무 조건 충족" */
    void match(String label) {
        matches.add(new Note(label, label));
    }

    void mismatch(String label) {
        mismatches.add(new Note(label, label));
    }

    /** "label: score/max점" 형태의 근거. */
    int match(String label, int score, int max) {
        return note(true, label, withScore(label, score, max), score);
    }

    int mismatch(String label, int score, int max) {
        return note(false, label, withScore(label, score, max), score);
    }

    /** matched이면 match, 아니면 mismatch로 기록한다. */
    int note(boolean matched, String label, int score, int max) {
        return note(matched, label, withScore(label, score, max), score);
    }

    /** summary와 detail을 따로 지정하고 싶을 때 사용한다. */
    int note(boolean matched, String summary, String detail, int score) {
        (matched ? matches : mismatches).add(new Note(summary, detail));
        return score;
    }

    /** 부족한 조건을 모두 나열한 문자열. 없으면 "없음". */
    String mismatchReason() {
        if (mismatches.isEmpty()) return "없음";
        return String.join(", ", mismatches.stream().map(Note::detail).toList());
    }

    /** 사용자에게 보여줄 한 줄 요약. 부족한 점이 있으면 그것을, 없으면 잘 맞는 점을 설명한다. */
    String recommendationReason() {
        List<String> gaps = mismatches.stream().map(Note::summary).distinct().limit(3).toList();
        if (!gaps.isEmpty()) return String.join("\n", gaps);

        String base = "현재 프로필 기준으로 뚜렷하게 부족한 조건은 없습니다.";
        List<String> strengths = matches.stream().map(Note::summary).distinct().limit(2).toList();
        if (strengths.isEmpty()) return base;
        return base + "\n" + String.join(", ", strengths);
    }

    private static String withScore(String label, int score, int max) {
        return label + ": " + score + "/" + max + "점";
    }
}
