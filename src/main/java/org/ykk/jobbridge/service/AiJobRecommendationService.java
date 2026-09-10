package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.AiJobRecommendationDTO;

import java.util.List;

public interface AiJobRecommendationService {
    List<AiJobRecommendationDTO> getRecommendations(Long memberId);
}
