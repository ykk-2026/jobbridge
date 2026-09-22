package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;

import java.util.List;

@Mapper
public interface ICommunityMapper {

    List<CommunityPostDTO> getPostList() throws Exception;

    CommunityPostDTO getPostInfo(CommunityPostDTO pDTO) throws Exception;

    int insertPostInfo(CommunityPostDTO pDTO) throws Exception;

    int updatePostViews(CommunityPostDTO pDTO) throws Exception;

    int deletePostInfo(CommunityPostDTO pDTO) throws Exception;

    List<CommunityCommentDTO> getCommentList(CommunityCommentDTO pDTO) throws Exception;

    int insertCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    int deleteCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    List<CommunityReportDTO> getReportList(CommunityReportDTO pDTO) throws Exception;

    CommunityReportDTO getReportExists(CommunityReportDTO pDTO) throws Exception;

    int insertReportInfo(CommunityReportDTO pDTO) throws Exception;

}
