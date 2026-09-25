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

    String recommendationReason(int totalScore) {
        List<String> strengths = matches.stream().map(Note::summary).distinct().limit(3).toList();
        List<String> gaps = mismatches.stream().map(Note::summary).distinct().limit(3).toList();

        if (totalScore >= 70) {
            String matched = strengths.isEmpty()
                    ? "주요 조건이 전반적으로 잘 맞습니다."
                    : String.join(", ", strengths) + " 등 주요 조건이 잘 맞습니다.";
            return gaps.isEmpty() ? matched : matched + " 다만 " + String.join(", ", gaps) + ".";
        }

        if (totalScore >= 50) {
            String matched = strengths.isEmpty()
                    ? "일치하는 조건이 제한적입니다."
                    : String.join(", ", strengths) + " 등 일부 조건은 일치하거나 충족합니다.";
            return gaps.isEmpty() ? matched : matched + " 다만 " + String.join(", ", gaps) + ".";
        }

        String different = gaps.isEmpty()
                ? "주요 조건의 일치 여부를 추가로 확인해야 합니다."
                : String.join(", ", gaps) + " 등 주요 조건에서 차이가 있습니다.";
        return strengths.isEmpty()
                ? different
                : different + " 다만 " + String.join(", ", strengths) + " 조건은 일치하거나 충족합니다.";
    }

    private static String withScore(String label, int score, int max) {
        return label + ": " + score + "/" + max + "점";
    }
}
