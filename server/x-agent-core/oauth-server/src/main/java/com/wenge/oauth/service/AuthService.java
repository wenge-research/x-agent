package com.wenge.oauth.service;

import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {

    Result smsAuthentication(StringParam phoneNumber, HttpServletRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeyException;
}
