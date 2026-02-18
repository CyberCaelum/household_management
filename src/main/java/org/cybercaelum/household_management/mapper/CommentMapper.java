package org.cybercaelum.household_management.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.cybercaelum.household_management.annotation.AutoFill;
import org.cybercaelum.household_management.enumeration.OperationType;
import org.cybercaelum.household_management.pojo.entity.Comment;
import org.cybercaelum.household_management.pojo.vo.CommentVO;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论Mapper
 * @date 2026/2/18
 */
@Mapper
public interface CommentMapper {

    /**
     * @description 新增评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param comment 评论信息
     **/
    @AutoFill(value = OperationType.INSERT)
    void addComment(Comment comment);

    /**
     * @description 根据id删除评论（逻辑删除，修改status为0）
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @param userId 当前用户id（用于校验权限）
     **/
    @Update("update comment set status = 0 where id = #{id} and user_id = #{userId}")
    void deleteComment(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * @description 更新评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param comment 评论信息
     **/
    @AutoFill(value = OperationType.UPDATE)
    void updateComment(Comment comment);

    /**
     * @description 根据id查询评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.entity.Comment
     **/
    @Select("select * from comment where id = #{id}")
    Comment getById(Long id);

    /**
     * @description 查询评论详情（包含用户信息）
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    CommentVO getCommentDetailById(Long id);

    /**
     * @description 根据被评论用户id查询评论列表
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentedUserId 被评论用户id
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    List<CommentVO> getCommentsByCommentedUserId(Long commentedUserId);

    /**
     * @description 根据订单id查询评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param orderId 订单id
     * @return org.cybercaelum.household_management.pojo.vo.CommentVO
     **/
    @Select("select * from comment where order_id = #{orderId} and status = 1")
    Comment getByOrderId(Long orderId);

    /**
     * @description 根据用户id和订单id查询评论（检查是否已评论）
     * @author CyberCaelum
     * @date 2026/2/18
     * @param userId 用户id
     * @param orderId 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Comment
     **/
    @Select("select * from comment where user_id = #{userId} and order_id = #{orderId} and status = 1")
    Comment getByUserIdAndOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    /**
     * @description 查询用户发表的所有评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param userId 用户id
     * @return java.util.List<org.cybercaelum.household_management.pojo.vo.CommentVO>
     **/
    List<CommentVO> getCommentsByUserId(Long userId);
}
