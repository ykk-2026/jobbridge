package org.ykk.jobbridge.recommendation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.ykk.jobbridge.util.NumberUtils.clamp;
import static org.ykk.jobbridge.util.TextUtils.compact;

/**
 * 행정구역 이름만으로 두 지역이 얼마나 가까운지 판단한다. (카카오 길찾기를 쓸 수 없을 때의 대안)
 * 광역지역 인접 정보와 서울 자치구 좌표를 상수로 가진다.
 */
final class KoreaRegions {

    /** 지역 비교 결과. label에는 점수 표기가 없다. */
    record Match(int score, String label) {
    }

    /** 위도·경도 한 쌍. 서울 자치구 사이의 직선 거리를 구할 때 쓴다. */
    private record Coordinate(double latitude, double longitude) {

        private static final double EARTH_RADIUS_KM = 6371.0;

        /** 하버사인 공식으로 구한 두 좌표 사이의 직선 거리(km). */
        double distanceKmTo(Coordinate other) {
            double dLat = Math.toRadians(other.latitude - latitude);
            double dLng = Math.toRadians(other.longitude - longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude))
                    * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        }
    }

    private static final int MAX_POINTS = JobRecommendationCalculator.REGION_POINTS;

    private static final List<String> PROVINCES = List.of(
            "서울", "경기", "인천", "강원", "충북", "충남", "대전", "세종",
            "전북", "전남", "광주", "경북", "대구", "경남", "울산", "부산", "제주");

    private static final Map<String, Set<String>> NEARBY_PROVINCES = nearbyProvinces();
    private static final Map<String, Coordinate> SEOUL_DISTRICTS = seoulDistricts();

    private KoreaRegions() {
    }

    /** 희망 지역과 공고 지역을 비교해 0~15점과 근거를 돌려준다. */
    static Match compare(String desired, String actual) {
        String normalizedDesired = normalize(desired);
        String normalizedActual = normalize(actual);
        if (normalizedDesired.equals(normalizedActual)) {
            return new Match(MAX_POINTS, "희망 지역과 정확히 일치");
        }
        if (normalizedActual.contains(normalizedDesired) || normalizedDesired.contains(normalizedActual)) {
            return new Match(MAX_POINTS, "희망 지역 범위에 포함");
        }

        Match seoulDistance = compareSeoulDistricts(desired, actual);
        if (seoulDistance != null) return seoulDistance;

        String desiredProvince = province(desired);
        String actualProvince = province(actual);
        Set<String> common = new HashSet<>(tokens(desired));
        common.retainAll(tokens(actual));
        boolean sharesCityOrDistrict = common.stream().anyMatch(token -> !token.equals(desiredProvince));

        if (sharesCityOrDistrict) return new Match(14, "같은 시·군·구 지역");
        if (!desiredProvince.isBlank() && desiredProvince.equals(actualProvince)) {
            return new Match(11, "같은 광역지역");
        }
        if (isNearby(desiredProvince, actualProvince)) return new Match(6, "인접한 광역지역");
        return new Match(2, "희망 지역과 거리가 먼 지역");
    }

    /** 둘 다 서울 자치구이면 좌표 거리로 점수를 낸다 (6~15점). 아니면 null. */
    private static Match compareSeoulDistricts(String desired, String actual) {
        Coordinate from = seoulDistrictCoordinate(desired);
        Coordinate to = seoulDistrictCoordinate(actual);
        if (from == null || to == null) return null;

        double km = from.distanceKmTo(to);
        int score = clamp((int) Math.round(MAX_POINTS - km * 0.25), 6, MAX_POINTS);
        return new Match(score, String.format(Locale.ROOT, "희망 지역과 약 %.1fkm 거리", km));
    }

    private static Coordinate seoulDistrictCoordinate(String region) {
        String compacted = compact(region);
        return SEOUL_DISTRICTS.entrySet().stream()
                .filter(entry -> compacted.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /** "서울특별시" → "서울", "경기도" → "경기"처럼 행정구역 접미어를 정리한다. */
    private static String normalize(String value) {
        return compact(value)
                .replace("특별자치도", "").replace("특별자치시", "")
                .replace("특별시", "").replace("광역시", "")
                .replace("경기도", "경기").replace("강원도", "강원")
                .replace("충청북도", "충북").replace("충청남도", "충남")
                .replace("전라북도", "전북").replace("전라남도", "전남")
                .replace("경상북도", "경북").replace("경상남도", "경남")
                .replace("제주도", "제주");
    }

    /** 지역 문자열이 속한 광역지역 이름. 찾지 못하면 빈 문자열. */
    private static String province(String value) {
        String normalized = normalize(value);
        return PROVINCES.stream().filter(normalized::contains).findFirst().orElse("");
    }

    /** 시·군·구·동 단위 이름 조각들 (광역지역 이름 포함). */
    private static Set<String> tokens(String value) {
        String cleaned = value.replace("특별자치도", " ").replace("특별자치시", " ")
                .replace("특별시", " ").replace("광역시", " ").replace("도", " ")
                .replace("시", " ").replace("군", " ").replace("구", " ")
                .replace("읍", " ").replace("면", " ").replace("동", " ");
        Set<String> tokens = new HashSet<>();
        for (String token : cleaned.split("\\s+")) {
            String normalized = normalize(token);
            if (!normalized.isBlank()) tokens.add(normalized);
        }
        String province = province(value);
        if (!province.isBlank()) tokens.add(province);
        return tokens;
    }

    private static boolean isNearby(String first, String second) {
        if (first.isBlank() || second.isBlank()) return false;
        return NEARBY_PROVINCES.getOrDefault(first, Set.of()).contains(second);
    }

    private static Map<String, Set<String>> nearbyProvinces() {
        Map<String, Set<String>> map = new HashMap<>();
        connect(map, "서울", "경기", "인천");
        connect(map, "경기", "인천", "강원", "충북", "충남");
        connect(map, "강원", "충북", "경북");
        connect(map, "충북", "충남", "대전", "세종", "경북");
        connect(map, "충남", "대전", "세종", "전북");
        connect(map, "대전", "세종", "전북");
        connect(map, "전북", "전남", "광주", "경북", "경남");
        connect(map, "전남", "광주", "경남");
        connect(map, "경북", "대구", "울산", "경남");
        connect(map, "대구", "경남");
        connect(map, "경남", "울산", "부산");
        connect(map, "울산", "부산");
        return map;
    }

    private static void connect(Map<String, Set<String>> map, String from, String... neighbors) {
        for (String neighbor : neighbors) {
            map.computeIfAbsent(from, key -> new HashSet<>()).add(neighbor);
            map.computeIfAbsent(neighbor, key -> new HashSet<>()).add(from);
        }
    }

    private static Map<String, Coordinate> seoulDistricts() {
        Map<String, Coordinate> map = new HashMap<>();
        map.put("종로구", new Coordinate(37.5735, 126.9790));
        map.put("중구", new Coordinate(37.5641, 126.9979));
        map.put("용산구", new Coordinate(37.5326, 126.9900));
        map.put("성동구", new Coordinate(37.5633, 127.0369));
        map.put("광진구", new Coordinate(37.5385, 127.0823));
        map.put("동대문구", new Coordinate(37.5744, 127.0396));
        map.put("중랑구", new Coordinate(37.6063, 127.0927));
        map.put("성북구", new Coordinate(37.5894, 127.0167));
        map.put("강북구", new Coordinate(37.6398, 127.0255));
        map.put("도봉구", new Coordinate(37.6688, 127.0471));
        map.put("노원구", new Coordinate(37.6542, 127.0568));
        map.put("은평구", new Coordinate(37.6027, 126.9291));
        map.put("서대문구", new Coordinate(37.5791, 126.9368));
        map.put("마포구", new Coordinate(37.5663, 126.9019));
        map.put("양천구", new Coordinate(37.5170, 126.8666));
        map.put("강서구", new Coordinate(37.5509, 126.8496));
        map.put("구로구", new Coordinate(37.4955, 126.8874));
        map.put("금천구", new Coordinate(37.4569, 126.8955));
        map.put("영등포구", new Coordinate(37.5264, 126.8962));
        map.put("동작구", new Coordinate(37.5124, 126.9393));
        map.put("관악구", new Coordinate(37.4784, 126.9516));
        map.put("서초구", new Coordinate(37.4837, 127.0324));
        map.put("강남구", new Coordinate(37.5172, 127.0473));
        map.put("송파구", new Coordinate(37.5145, 127.1059));
        map.put("강동구", new Coordinate(37.5301, 127.1238));
        return map;
    }
}
