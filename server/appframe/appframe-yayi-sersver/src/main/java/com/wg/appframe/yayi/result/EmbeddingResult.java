package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmbeddingResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -2782693535442002787L;

    private EmbeddingData data;

    @Data
    public static class EmbeddingData implements Serializable {

        private static final long serialVersionUID = -3188870665772269062L;

        private String dataType;
        private List<List<Double>> embeddingList;

    }

}
