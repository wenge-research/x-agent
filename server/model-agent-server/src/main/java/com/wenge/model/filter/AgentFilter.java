package com.wenge.model.filter;

import com.wenge.model.service.impl.DialogueServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AgentFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            log.error("parse token found error.", e);
        } finally {
            DialogueServiceImpl.APPLICATION_INFO.remove();
            DialogueServiceImpl.KNOWLEDGE_ID_LIST.remove();
            DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.remove();
            DialogueServiceImpl.TRACE_ID.remove();
            DialogueServiceImpl.STEP_BY_LIST.remove();
        }
    }
}
