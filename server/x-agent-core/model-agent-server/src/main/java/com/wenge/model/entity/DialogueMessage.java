package com.wenge.model.entity;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DialogueMessage extends YayiMessage {

    private static final long serialVersionUID = 1344139768573267508L;

    private String dialogueId;
    private String question;

    public DialogueMessage(String role, String content) {
        super(role, content);
    }

    public DialogueMessage(String dialogueId, String role, String content) {
        super(role, content);
        this.dialogueId = dialogueId;
    }
}
