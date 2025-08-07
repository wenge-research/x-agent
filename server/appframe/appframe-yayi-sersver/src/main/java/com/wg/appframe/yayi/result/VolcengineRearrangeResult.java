package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class VolcengineRearrangeResult extends VolcengineResult implements Serializable {

    private static final long serialVersionUID = 4883476554701446369L;

    private VolcengineRearrangeData data;

    @Data
    public static class VolcengineRearrangeData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        /**
         * 原始评分结果
         */
        private List<BigDecimal> scores;

        /**
         * 对原始评分结果进行倒序排序后索引的结果
         */
        private List<Integer> index_list;

        /**
         * 消耗的token
         */
        private Integer token_usage;

        /**
         * 对原始评分结果进行倒序排序的结果
         */
        private List<BigDecimal> res_scores_list;

    }

}
