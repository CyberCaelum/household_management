package org.cybercaelum.household_management.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 订单定时任务
 * @date 2026/3/15
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTask {

    private final OrderService orderService;

    /**
     * 自动开始服务（每天凌晨1点执行）
     * 检查到达开始时间的订单，将状态更新为"服务中"
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoStartService() {
        log.info("定时任务：自动开始服务");
        try {
            orderService.autoStartService();
        } catch (Exception e) {
            log.error("自动开始服务定时任务执行失败", e);
        }
    }

    /**
     * 自动确认每日服务（每小时执行一次）
     * 检查超时未确认的每日服务记录，自动确认
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoConfirmDailyService() {
        log.info("定时任务：自动确认每日服务");
        try {
            orderService.autoConfirmDailyService();
        } catch (Exception e) {
            log.error("自动确认每日服务定时任务执行失败", e);
        }
    }

    /**
     * 处理超时取消申请（每30分钟执行一次）
     * 将超时未响应的取消申请转交平台介入
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void processTimeoutCancelApplications() {
        log.info("定时任务：处理超时取消申请");
        try {
            orderService.processTimeoutCancelApplications();
        } catch (Exception e) {
            log.error("处理超时取消申请定时任务执行失败", e);
        }
    }
}
