package com.wenge.model.dto.result;

import com.wenge.model.entity.PolicyRewardInfo;
import lombok.Data;

import java.util.List;


@Data
public class PolicyRewardInfoResult {

    private List<PolicyRewardInfo> policyRewardInfoList;
    private Integer rewardSize;
    private String region;
    private String regionLevel;
    private String receivingUnit;
    private String industryType;
}
