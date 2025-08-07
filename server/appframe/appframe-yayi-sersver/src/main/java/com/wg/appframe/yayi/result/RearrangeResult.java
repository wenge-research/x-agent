package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RearrangeResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 4883476554701446369L;

    private RearrangeData data;

    // Data类
    @Data
    public static class RearrangeData implements Serializable {

        private static final long serialVersionUID = 5132157250255496625L;

        private List<Integer> index_list;
        private List<BigDecimal> res_scores_list;
    }
}
