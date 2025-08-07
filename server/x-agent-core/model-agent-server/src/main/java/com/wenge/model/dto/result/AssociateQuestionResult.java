package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssociateQuestionResult extends WGResult {

    private static final long serialVersionUID = -8984448011070502933L;

    private String question;
    private String answer;

    private String category;
    private String link;
}
