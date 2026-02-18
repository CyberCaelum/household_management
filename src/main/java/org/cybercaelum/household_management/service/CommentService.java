package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.CommentDTO;
import org.cybercaelum.household_management.pojo.vo.CommentVO;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论服务接口
 * @date 2026/2/18
 */
public interface CommentService {

    /**
     * @description 新增评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     **/
    void addComment(CommentDTO commentDTO);

    /**
     * @description 删除评论（逻辑删除）
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     **/
    void deleteComment(Long id);

    /**
     * @description 修改评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     **/
    void updateComment(CommentDTO commentDTO);

    /**
     * @description 根据id查询评论详情
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    CommentVO getCommentById(Long id);

    /**
     * @description 根据被评论用户id查询评论列表
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentedUserId 被评论用户id
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    List<CommentVO> getCommentsByCommentedUserId(Long commentedUserId);

    /**
     * @description 查询当前用户发表的所有评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    List<CommentVO> getMyComments();

    /**
     * @description 根据订单id查询评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param orderId 订单id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    CommentVO getCommentByOrderId(Long orderId);
}
