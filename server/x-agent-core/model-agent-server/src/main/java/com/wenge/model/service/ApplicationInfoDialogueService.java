package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.entity.ApplicationInfoDialogue;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApplicationInfoDialogueService extends IService<ApplicationInfoDialogue> {
    public Result<Page<ApplicationInfoDialogue>> getApplicationInfoDialogueList(ApplicationInfoDialogueParam applicationInfoDialogueParam);
    public Result insertApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam);
    public Result deleteApplicationInfoDialogue(Integer id);
    public Result saveApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam);
}
