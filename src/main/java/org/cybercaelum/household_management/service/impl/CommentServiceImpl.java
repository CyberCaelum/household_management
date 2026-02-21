package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.constant.MessageConstant;
import org.cybercaelum.household_management.context.BaseContext;
import org.cybercaelum.household_management.exception.BaseException;
import org.cybercaelum.household_management.mapper.CommentMapper;
import org.cybercaelum.household_management.pojo.dto.CommentDTO;
import org.cybercaelum.household_management.pojo.entity.Comment;
import org.cybercaelum.household_management.pojo.vo.CommentVO;
import org.cybercaelum.household_management.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论服务实现类
 * @date 2026/2/18
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    /**
     * @description 新增评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     **/
    @Transactional
    @Override
    public void addComment(CommentDTO commentDTO) {
        Long userId = BaseContext.getUserId();
        
        // 检查是否已对该订单发表过评论
        Comment existComment = commentMapper.getByUserIdAndOrderId(userId, commentDTO.getOrderId());
        if (existComment != null) {
            throw new BaseException(MessageConstant.COMMENT_ALREADY_EXISTS);
        }
        
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        comment.setStatus(1); // 设置状态为可见
        
        log.info("新增评论：{}", comment);
        commentMapper.addComment(comment);
    }

    /**
     * @description 删除评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     **/
    @Transactional
    @Override
    public void deleteComment(Long id) {
        Long userId = BaseContext.getUserId();
        
        // 先查询评论是否存在
        Comment comment = commentMapper.getById(id);
        if (comment == null) {
            throw new BaseException(MessageConstant.COMMENT_NOT_FOUND);
        }
        
        // 只能删除自己的评论
        if (!userId.equals(comment.getUserId())) {
            throw new BaseException(MessageConstant.COMMENT_NOT_ALLOWED);
        }
        
        log.info("删除评论，id：{}，userId：{}", id, userId);
        commentMapper.deleteComment(id, userId);
    }

    /**
     * @description 修改评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     **/
    @Transactional
    @Override
    public void updateComment(CommentDTO commentDTO) {
        Long userId = BaseContext.getUserId();
        
        // 查询评论是否存在
        Comment comment = commentMapper.getById(commentDTO.getId());
        if (comment == null) {
            throw new BaseException(MessageConstant.COMMENT_NOT_FOUND);
        }
        
        // 只能修改自己的评论
        if (!userId.equals(comment.getUserId())) {
            throw new BaseException(MessageConstant.COMMENT_NOT_ALLOWED);
        }
        
        // 复制新内容
        comment.setContent(commentDTO.getContent());
        comment.setCommentLevel(commentDTO.getCommentLevel());
        
        log.info("修改评论：{}", comment);
        commentMapper.updateComment(comment);
    }

    /**
     * @description 根据id查询评论详情
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    @Override
    public CommentVO getCommentById(Long id) {
        CommentVO commentVO = commentMapper.getCommentDetailById(id);
        if (commentVO == null || commentVO.getStatus() == 0) {
            throw new BaseException(MessageConstant.COMMENT_NOT_FOUND);
        }
        return commentVO;
    }

    /**
     * @description 根据被评论用户id查询评论列表
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentedUserId 被评论用户id
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    @Override
    public List<CommentVO> getCommentsByCommentedUserId(Long commentedUserId) {
        return commentMapper.getCommentsByCommentedUserId(commentedUserId);
    }

    /**
     * @description 查询当前用户发表的所有评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    @Override
    public List<CommentVO> getMyComments() {
        Long userId = BaseContext.getUserId();
        return commentMapper.getCommentsByUserId(userId);
    }

    /**
     * @description 根据订单id查询评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param orderId 订单id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    @Override
    public CommentVO getCommentByOrderId(Long orderId) {
        Comment comment = commentMapper.getByOrderId(orderId);
        if (comment == null) {
            return null;
        }
        return commentMapper.getCommentDetailById(comment.getId());
    }
}
