package com.wenge.model.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DemoService {

    void runDialogYayi(HttpServletResponse response, String startDate) throws IOException;
}
