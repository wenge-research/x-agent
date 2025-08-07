package com.wenge.model.dto.template;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DatasetDTO {
    private String question_id;
    private String query_id;
    private String input;
    private String output;
    private String session_id;
    private String reference_output;
    private String score;
    private String score_reason;
}