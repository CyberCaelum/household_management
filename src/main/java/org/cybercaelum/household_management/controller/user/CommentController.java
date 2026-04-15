package org.cybercaelum.household_management.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.annotation.RequireRole;
import org.cybercaelum.household_management.constant.RoleConstant;
import org.cybercaelum.household_management.pojo.dto.CommentDTO;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.pojo.vo.CommentVO;
import org.cybercaelum.household_management.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 评论Controller
 * @date 2026/2/18
 */
@RestController
@RequestMapping("/comment")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "评论服务", description = "订单评价相关接口")
@Validated
public class CommentController {

    private final CommentService commentService;

    /**
     * @description 新增评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @RequireRole(RoleConstant.USER)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/add")
    @Operation(summary = "新增评论", description = "对订单和用户进行评价")
    public Result addComment(@Valid @RequestBody CommentDTO commentDTO) {
        log.info("新增评论：{}", commentDTO);
        commentService.addComment(commentDTO);
        return Result.success();
    }

    /**
     * @description 删除评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @RequireRole(RoleConstant.USER)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除评论", description = "删除自己发表的评论")
    public Result deleteComment(@PathVariable Long id) {
        log.info("删除评论，id：{}", id);
        commentService.deleteComment(id);
        return Result.success();
    }

    /**
     * @description 修改评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param commentDTO 评论信息
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @RequireRole(RoleConstant.USER)
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/update")
    @Operation(summary = "修改评论", description = "修改自己发表的评论")
    public Result updateComment(@Valid @RequestBody CommentDTO commentDTO) {
        log.info("修改评论：{}", commentDTO);
        commentService.updateComment(commentDTO);
        return Result.success();
    }

    /**
     * @description 根据id查询评论详情
     * @author CyberCaelum
     * @date 2026/2/18
     * @param id 评论id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @GetMapping("/{id}")
    @Operation(summary = "查询评论详情", description = "根据评论id查询详情")
    public Result getCommentById(@PathVariable Long id) {
        log.info("查询评论详情，id：{}", id);
        CommentVO commentVO = commentService.getCommentById(id);
        return Result.success(commentVO);
    }

    /**
     * @description 根据被评论用户id查询评论列表
     * @author CyberCaelum
     * @date 2026/2/18
     * @param userId 被评论用户id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户收到的评论", description = "查询指定用户收到的所有评价")
    public Result getCommentsByCommentedUserId(@PathVariable Long userId) {
        log.info("查询用户收到的评论，userId：{}", userId);
        List<CommentVO> list = commentService.getCommentsByCommentedUserId(userId);
        return Result.success(list);
    }

    /**
     * @description 查询当前用户发表的所有评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    @Operation(summary = "查询我发表的评论", description = "查询当前登录用户发表的所有评价")
    public Result getMyComments() {
        log.info("查询我发表的评论");
        List<CommentVO> list = commentService.getMyComments();
        return Result.success(list);
    }

    /**
     * @description 根据订单id查询评论
     * @author CyberCaelum
     * @date 2026/2/18
     * @param orderId 订单id
     * @return org.cybercaelum.household_management.pojo.entity.Result
     **/
    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询订单评论", description = "根据订单id查询对应的评价")
    public Result getCommentByOrderId(@PathVariable Long orderId) {
        log.info("查询订单评论，orderId：{}", orderId);
        CommentVO commentVO = commentService.getCommentByOrderId(orderId);
        return Result.success(commentVO);
    }
}
