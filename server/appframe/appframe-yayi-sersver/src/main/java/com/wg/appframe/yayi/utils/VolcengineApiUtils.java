package com.wg.appframe.yayi.utils;

import cn.hutool.json.JSONUtil;
import com.volcengine.auth.ISignerV4;
import com.volcengine.auth.impl.SignerV4Impl;
import com.volcengine.model.Credentials;
import com.volcengine.service.SignableRequest;
import com.wg.appframe.yayi.param.VolcengineParam;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.List;

public class VolcengineApiUtils {

    public static String invokeVolcengineApi(VolcengineParam volcengineParam) throws Exception {
        URI uri = URI.create(volcengineParam.getUri());
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String region = volcengineParam.getRegion();
        String service = volcengineParam.getServe();
        String body = JSONUtil.toJsonStr(volcengineParam);
        try {
            SignableRequest signableRequest = prepareRequest(region,  service, scheme, host, path, "POST", null, body,
            volcengineParam.getAppKey(), volcengineParam.getAppSecret());
            URI requestUri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path)
                    .build();

            HttpPost httpPost = new HttpPost(requestUri);
            httpPost.setConfig(signableRequest.getConfig());
            httpPost.setEntity(signableRequest.getEntity());
            for (Header header : signableRequest.getAllHeaders()) {
                httpPost.setHeader(header.getName(), header.getValue());
            }

            HttpClient httpClient = HttpClients.createDefault();

            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static SignableRequest prepareRequest(String region, String service, String scheme, String host, String path, String method, List<NameValuePair> params, String body, String ak, String sk) throws Exception {
        SignableRequest request = new SignableRequest();
        request.setMethod(method);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Host", host);
        request.setEntity(new StringEntity(body, "utf-8"));

        URIBuilder builder = request.getUriBuilder();
        builder.setScheme(scheme);
        builder.setHost(host);
        builder.setPath(path);
        if (params != null) {
            builder.setParameters(params);
        }

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();
        request.setConfig(requestConfig);

        Credentials credentials = new Credentials(region, service);
        credentials.setAccessKeyID(ak);
        credentials.setSecretAccessKey(sk);

        // 签名
        ISignerV4 ISigner = new SignerV4Impl();
        ISigner.sign(request, credentials);

        return request;
    }

}
