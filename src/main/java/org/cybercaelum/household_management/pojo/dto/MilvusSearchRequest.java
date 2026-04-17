package org.cybercaelum.household_management.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: ai工具查询类
 * @date 2026/4/17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MilvusSearchRequest {
    private String query;
}
