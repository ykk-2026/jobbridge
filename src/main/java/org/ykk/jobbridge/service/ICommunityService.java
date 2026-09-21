package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;

import java.util.List;

public interface ICommunityService {







    List<CommunityPostDTO> getPostList(CommunityPostDTO pDTO) throws Exception;








    CommunityPostDTO getPostInfo(CommunityPostDTO pDTO, boolean type) throws Exception;






    void insertPostInfo(CommunityPostDTO pDTO) throws Exception;







    int deletePostInfo(CommunityPostDTO pDTO) throws Exception;







    int insertCommentInfo(CommunityCommentDTO pDTO) throws Exception;







    int deleteCommentInfo(CommunityCommentDTO pDTO) throws Exception;







    int insertReportInfo(CommunityReportDTO pDTO) throws Exception;

}
