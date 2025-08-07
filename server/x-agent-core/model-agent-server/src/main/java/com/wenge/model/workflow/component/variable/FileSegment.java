package com.wenge.model.workflow.component.variable;

import com.wenge.model.workflow.component.file.File;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

public class FileSegment extends Segments<File> {
    public FileSegment(String variable, File value) {
        super(variable, MetaParamEnum.FILE, value);
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        return null;
    }

    @Override
    public String text() {
        return super.text();
    }

    @Override
    public String markdown() {
        return super.markdown();
    }

    @Override
    public String json() {
        return super.json();
    }
}
