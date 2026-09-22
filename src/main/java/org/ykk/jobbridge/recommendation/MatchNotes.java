package org.ykk.jobbridge.recommendation;

import java.util.ArrayList;
import java.util.List;











final class MatchNotes {

    private record Note(String summary, String detail) {
    }

    private final List<Note> matches = new ArrayList<>();
    private final List<Note> mismatches = new ArrayList<>();


    void match(String label) {
        matches.add(new Note(label, label));
    }

    void mismatch(String label) {
        mismatches.add(new Note(label, label));
    }


    int match(String label, int score, int max) {
        return note(true, label, withScore(label, score, max), score);
    }

    int mismatch(String label, int score, int max) {
        return note(false, label, withScore(label, score, max), score);
    }


    int note(boolean matched, String label, int score, int max) {
        return note(matched, label, withScore(label, score, max), score);
    }


    int note(boolean matched, String summary, String detail, int score) {
        (matched ? matches : mismatches).add(new Note(summary, detail));
        return score;
    }


    String mismatchReason() {
        if (mismatches.isEmpty()) return "없음";
        return String.join(", ", mismatches.stream().map(Note::detail).toList());
    }


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
