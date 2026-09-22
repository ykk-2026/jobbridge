package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.AiJobRecommendationDTO;

import java.util.List;

public interface IAiJobRecommendationService {







    List<AiJobRecommendationDTO> getRecommendationList(AiJobRecommendationDTO pDTO) throws Exception;

}
