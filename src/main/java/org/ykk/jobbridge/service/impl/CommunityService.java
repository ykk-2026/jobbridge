package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;
import org.ykk.jobbridge.mapper.ICommunityMapper;
import org.ykk.jobbridge.service.ICommunityService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommunityService implements ICommunityService {

    private final ICommunityMapper communityMapper;

    @Override
    public List<CommunityPostDTO> getPostList(CommunityPostDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getPostList Start!");

        List<CommunityPostDTO> rList = communityMapper.getPostList();

        if (rList == null) {
            rList = new ArrayList<>();
        }

        // 게시글마다 댓글 목록과 로그인 사용자의 신고 내역 추가
        for (CommunityPostDTO rDTO : rList) {
            addCommentsAndReports(rDTO, pDTO.getMemberId());
        }

        log.info(this.getClass().getName() + ".getPostList End!");

        return rList;
    }

    @Transactional
    @Override
    public CommunityPostDTO getPostInfo(CommunityPostDTO pDTO, boolean type) throws Exception {

        log.info(this.getClass().getName() + ".getPostInfo Start!");

        // 상세보기할 때마다, 조회수 증가하기
        if (type) {
            log.info("Update ViewCount");
            communityMapper.updatePostViews(pDTO);
        }

        CommunityPostDTO rDTO = communityMapper.getPostInfo(pDTO);

        addCommentsAndReports(rDTO, pDTO.getMemberId());

        log.info(this.getClass().getName() + ".getPostInfo End!");

        return rDTO;
    }

    @Transactional
    @Override
    public void insertPostInfo(CommunityPostDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertPostInfo Start!");

        communityMapper.insertPostInfo(pDTO);
    }

    @Transactional
    @Override
    public int deletePostInfo(CommunityPostDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deletePostInfo Start!");

        return communityMapper.deletePostInfo(pDTO);
    }

    @Transactional
    @Override
    public int insertCommentInfo(CommunityCommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCommentInfo Start!");

        // 등록 성공 : 1, 없는 게시글 : 3, 오류 : 0
        int res = 0;

        // 댓글을 달 게시글이 존재하는지 확인
        CommunityPostDTO postDTO = new CommunityPostDTO();
        postDTO.setId(pDTO.getPostId());

        if (communityMapper.getPostInfo(postDTO) == null) {
            log.info("존재하지 않는 게시글 : " + pDTO.getPostId());
            res = 3;

        } else {
            int success = communityMapper.insertCommentInfo(pDTO);

            if (success > 0) {
                res = 1;
            }
        }

        log.info(this.getClass().getName() + ".insertCommentInfo End!");

        return res;
    }

    @Transactional
    @Override
    public int deleteCommentInfo(CommunityCommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCommentInfo Start!");

        return communityMapper.deleteCommentInfo(pDTO);
    }

    @Transactional
    @Override
    public int insertReportInfo(CommunityReportDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertReportInfo Start!");

        // 신고 성공 : 1, 이미 신고함 : 2, 오류 : 0
        int res = 0;

        // 이미 신고한 게시글인지 확인
        CommunityReportDTO existsDTO = communityMapper.getReportExists(pDTO);

        if (CmmUtil.nvl(existsDTO.getExistsYn()).equals("Y")) {
            log.info("이미 신고한 게시글 : " + pDTO.getPostId());
            res = 2;

        } else {
            int success = communityMapper.insertReportInfo(pDTO);

            if (success > 0) {
                res = 1;
            }
        }

        log.info(this.getClass().getName() + ".insertReportInfo End!");

        return res;
    }

    /**
     * 게시글에 댓글 목록과 로그인 사용자의 신고 내역을 추가함
     */
    private void addCommentsAndReports(CommunityPostDTO rDTO, Long memberId) throws Exception {

        if (rDTO == null) {
            return;
        }

        // 댓글 목록 조회
        CommunityCommentDTO cDTO = new CommunityCommentDTO();
        cDTO.setPostId(rDTO.getId());

        rDTO.setComments(communityMapper.getCommentList(cDTO));

        // 로그인한 사용자라면, 이 게시글을 신고했는지 조회
        if (memberId != null) {
            CommunityReportDTO rpDTO = new CommunityReportDTO();
            rpDTO.setPostId(rDTO.getId());
            rpDTO.setReporterMemberId(memberId);

            rDTO.setReports(communityMapper.getReportList(rpDTO));
        }
    }
}
