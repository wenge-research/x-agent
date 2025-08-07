/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wenge.oauth.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenge.oauth.exception.OauthException;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.utils.AESUtils;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.jwt.JwtHelper.decode;

/**
 * <p>
 * Jwt token工具类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-11-10
 */
@Component
public class JwtUtil {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Value("${appframe.token.aesSignKey}")
    private String aesKey;
    private static String AES_KEY;
    @Value("${appframe.token.aesSignIv}")
    private String aesIv;
    private static String AES_IV;
    @Value("${appframe.token.jwtPre}")
    private String jwtPre;
    private static String JWT_PRE;

    @Value("${appframe.token.subject}")
    private String subject;
    private static String SUBJECT;
    @Value("${appframe.token.issuer}")
    private String issuer;
    private static String ISSUER;

    @Value("${appframe.token.expired:1800000}")
    private Long expired;
    private static Long DEFAULT_EXPIRED;

    @Value("${appframe.token.alg}")
    private String alg;
    private static String ALG;
    @Value("${appframe.token.type}")
    private String type;
    private static String TYPE;

    @PostConstruct
    public void init() {
        AES_KEY = aesKey;
        AES_IV = aesIv;
        SUBJECT = subject;
        ISSUER = issuer;
        DEFAULT_EXPIRED = expired;
        ALG = alg;
        TYPE = type;
        JWT_PRE = jwtPre;
    }

    /**
     * 获取JWT签名
     *
     * @return 返回
     */
    public static String getSigningKey() {
        String data = new String(JWT_PRE.getBytes(), StandardCharsets.UTF_8);
        return AESUtils.encrypt(data, AES_KEY, AES_IV);
    }

    /**
     * token 校验
     *
     * @param token token
     * @return 返回
     */
    public static boolean verify(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            return verifyCustom(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验自定义Token
     * @param token token
     * @return 返回
     */
    public static boolean verifyCustom(String token) {
        try {
            final Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            return claims != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * token转map
     *
     * @param token token
     * @return map
     */
    public static Map<String, Object> parseMap(String token) {
        if (StrUtil.isBlank(token)) {
            return new HashMap<>();
        }
        String claimsStr = parseToken(token).getClaims();
        JSONObject entries = JSONUtil.parseObj(claimsStr);
        Map<String, Object> claims = entries.toBean(Map.class);
        if (claims.containsKey("exp") && claims.get("exp") instanceof Integer) {
            Integer intValue = (Integer) claims.get("exp");
            claims.put("exp", new Long((long) intValue));
        }
        return claims;
    }

    /**
     * 生产Jwt token
     *
     * @param data 自定义包含信息
     * @return 返回token
     */
    public static String generateCustomToken(Map<String, Object> data) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", ALG);
        header.put("type", TYPE);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(IdUtil.simpleUUID())
                .setSubject(SUBJECT)
                .setHeader(header)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRED))
                .signWith(SignatureAlgorithm.forName(ALG), getSigningKey());
        if (CollectionUtil.isNotEmpty(data)) {
            for (String key : data.keySet()) {
                builder.claim(key, data.get(key));
            }
        }
        return builder.compact();
    }

    public static Claims parseCustomToken(String token) {
        try {
            // 尝试解析token
            return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // 处理过期的token异常
            throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "Token已过期");
        } catch (UnsupportedJwtException e) {
            // 处理不支持的token类型异常
            throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "不支持的Token类型");
        } catch (MalformedJwtException e) {
            // 处理格式错误的token异常
            throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "Token格式错误");
        } catch (SignatureException e) {
            // 处理签名验证失败的异常
            throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "Token签名无效");
        } catch (IllegalArgumentException e) {
            // 处理空的或者null的token字符串异常
            throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "Token无效");
        }
    }

    /**
     * token 转jwt对象
     *
     * @param token token
     * @return 返回
     */
    public static Jwt parseToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        boolean verify = verify(token);
        if (verify) {
            return decode(token);
        }
        return null;
    }

    private static final String PUBKEY_START = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBKEY_END = "-----END PUBLIC KEY-----";
    

    
    /**
     * {"exp":1563256084,"user_name":"admin","authorities":["ADMIN"],"jti":"4ce02f54-3d1c-4461-8af1-73f0841a35df","client_id":"webApp","scope":["app"]}
     * @param jwtToken token值
     * @param rsaPublicKey 公钥
     * @return
     */
    public static JsonNode decodeAndVerify(String jwtToken, RSAPublicKey rsaPublicKey) {
        SignatureVerifier rsaVerifier = new RsaVerifier(rsaPublicKey);
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, rsaVerifier);
        return parse(jwt.getClaims());
    }

    public static JsonNode parse(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断jwt是否过期
     * @param claims jwt内容
     * @param currTime 当前时间
     * @return 未过期：true，已过期：false
     */
    public static boolean checkExp(JsonNode claims, long currTime) {
        long exp = claims.get("exp").asLong();
        if (exp < currTime) {
            return false;
        }
        return true;
    }
    
    /**
     * 判断jwt是否过期
     * @param claims jwt内容
     * @return 未过期：true，已过期：false
     */
    public static boolean checkExp(JsonNode claims) {
        return checkExp(claims, System.currentTimeMillis());
    }
    
    public static Jwt encode(CharSequence content, KeyProperties keyProperties) {
        KeyPair keyPair = new KeyStoreKeyFactory(
                keyProperties.getKeyStore().getLocation(),
                keyProperties.getKeyStore().getSecret().toCharArray())
                .getKeyPair(keyProperties.getKeyStore().getAlias());
        PrivateKey privateKey = keyPair.getPrivate();
        Signer rsaSigner = new RsaSigner((RSAPrivateKey) privateKey);
        return JwtHelper.encode(content, rsaSigner);
    }
    
    public static String encodeStr(CharSequence content, KeyProperties keyProperties) {
        return encode(content, keyProperties).getEncoded();
    }


    public static void main(String[] args) throws Exception {
//        String token = "0IpKB7sgVXwss8EAg8eOBuQnLly3+DRmlDmqG4DmHuZxAxi1bCCV6JJLeJS69LW56bxFWYEPWt9F65LRy5LoN3wK56nxI1CdlfCyhz/n6Gf6mBT0Id0+mIpP4cqBOeCSRLIHP5cj+Hn7HhcRSLNpd2+B4/FlLWGoR1NOxpC9bx0kZ+lW9vfzCnZBw0Eb7CwOC2bmWTAoY6PKjoLQx83JoZVkouQWscKwPd1vjFBNZHPd3U4qiRAzYYivrrFc96Bi3uKDuinSnziPOW5S1c6beZRohGXpf1L2/cM6J/nG5PtiUgu3ZnHHSnMWmzP8JTye3SZ4Xz35ZH+QCRLTCrMuL8tuBwmH7A28AinhValOcqD6aJ3XUybVZ9nqrEoxYekpKZU2h2i1vQmbmIS2l8Mtay4H6RRw7VgecLRwnckULAGJPrgbW3Gb7kLn4UwPTnaOZ1EiOpy0IxrNa7D4hPVGAQdnozkYQ4BiRsrVR3EU2cV/CoL45CtvdU5n1sNxDhkNTgEnQULxD1kd4f+6xbU1DmyS7OPqK0wuvlboVkCWCkCYCOMslm/Edi9k+IrbiAv91fXGJ/G9WNxvUqFC1SUsHb+4RZH/V3qaSg9AKSS335ibWJo6FapU1L2LVPnYPrp30Gznf1BqTCtzb9PM5ggrNWA0h8z9NOoC+n/IAc6vZJ/AUJCd0VfP2YgRLMCXZ2dsyJLJ6tVx+cpXVE0eRPUT7C/BlyVxMYVLdC3l0JbtoFWylYyOh0lRZvU0dsFwiWJakNVwqywEjgJxqTNSJEnCrVenGJPY+ez45582xLkgUjSeplTVg0hZdLLdlqw3adrozFgdkW5RmjVVn4da0Ny45qWj0G5lQhMDnnsESfhfRX1xmVwkuMLvF7vI7hdccaHiYUqyAFACfHkDtkja8T6WSVNG+IdFPtkL0SJ8rB6HHx0IWQOLk5CuRnJhxwiA6xY/QiUxwmjlBrhdBgzq9XBRaI6oCXWVEHprOmIDLkwi7UNyQDBuIHZnzeMAv6ec/V3T3aAhizF0CxDq4uSCvaXUXFfcTmjh6vdGGZvYboMY9bBYlx4CffTHsxiJN78NNa/RbR7tD30ZjeX4T9/ctNDGygtYCej9yeVegIxxGJKB8Bfpzj+3dMqhW0yKol0JC36ZjP3XV0N9VX7f3qRngbYciVCo86fJWsc2nHEB+efJm81uZUyBSNJlTCI0CoUbbLLEjArCmUJs6wXtuEi0DQvmZFaC+y35V0n4Or7CRNilvGBJCT8+t9XsoNTFfIqUI5pdOD3UQByVp9qwP6PDlSpamf6hKnq5hHzSIwwm07v4slUbRJxFE+cc5w8wokgFL4CHIRT1vfv5a602GYNgB/HwZKe49xelHcDhuiUBT3VEdwl1Wyyr5G6Q4kYkXQyK/sYg3hgzOMAL/tv7fVzYtuOzwXMjVIJgipFII/CJitNabticnDZ6k7lX/D31fD0p2sIS/5twmAKPbqTaYo2THuziDz5P/+dA36BHjFVqzMZtVrxJCPW5P3u1mAdiW7ii7498AH08Wns9fv72qplYkaUSaTi7Fc+hJxsXZwrQzpPCzytf5kki4mAQUm3PWROzSr//aeMjfZKY4PFGEqv0bzLt1J0B/pd22QRONdgvpJ83ixqe44zIIFb815MoE4O14QHZ/v/Ixpix1YOC2FbSZffCUQ0KhLsvb8/J/6Y1vJQmD6gZ7Xafg7rp8QxrTMOIT2D2zjkcspjh54uXZnA372TIgsksVsJmzIkFF4+I7U1B9t8=";
//        String token = "0IpKB7sgVXwss8EAg8eOBuQnLly3+DRmlDmqG4DmHuZxAxi1bCCV6JJLeJS69LW5DeXQzgSRmB29rPP1v36CrAEaUZXt55MsnJHUh4NOOCNSEMtVZMPb/JTHAR7C9l6+dszMeauu/lbVxAtSPA8N12nc+26/OaN9iVFiXCLUZLEFCbzZXVcfitSA9AND/TB2SznB7hjt0hYlUveOQ4P41TMDo6PXy8A6+aR1FLVpyd/JZ2kkE/DUmzfMc8rjomV7ZsKScpiHAo8LRxBQ3tKIk5ZtTHtl1FfSBc7v3Vi7ZBh2CP/v17BU6ZxEF9/D5Afm4Y2efTz8NMmUjtLaf0p+MwNleTIQFmHA5ItMiONcJB7y/K9gKl+X+6GuRrtEK/GeR+s0m7tdo8BzFQuhHcMBtClqzckUFRo9i+wxLI7qIu3tyDIc5Z3M8MWvMb1hYU/x0rMn92muRIIQDLCfO7JLHNuiuBVeqaCnxLbivo4xyiEVadl7GH3Y0pnctbzs5qUVLGYul/5AMbR1nBPGnejBPFtK0rIu3ROvDS77BGFx7tQ+fc2GV7CzuYsp580QbiJKgpKlGdq0rG+WlJWmP2amg9HnYVygwmr4Rfa79kh8sdF1oSwz7+XWzHb2uB+RtvLQwGShKmxrwysx1GOp0TfUyAtla9cULWlultxfmYvT/4cXsRSe/plluitMslBbY8v/0ulSzJ3WPIwrzv/gyec0z+jqSK6DB3HJBD+6iTbhtNZyL1Mrh9kZeISi8VBtG9aT5RM6ob7vWsr/om5PLiNgMTKdhetQ+jn30d6fZh5Ztt8z2mZJv72flqoXG+4xSNRvxlSaRiQ+l/oMy/2Jpv2QEGkTS/RhzduSHQEm7mnZ5p+yFMp60WL05FmVjWlOZt0O0BDQih7ZjT/qX7I5AEcoIJOJYBzOnff/XZVilauy/KAPvRbbsLDBAw6gYN00ZRLkl8c2mNVjjNSTst1SNKNxRgwnz1yRSEnylaQRzDATLPY4SdGswkBMylNH7umScuNVX2r16JM74KhizF7kthvaLeFK7jwLbUCTMb6wk5x5ZV9zpWAR6FjO1bZarko13V8M3wNZCijXO0oakW0JBAcHDyhkQwcnytPcSF7qPieRLxlYpwguPMyqPqMy+pBscmmPJQ2spXqO7yTg28I9k7F3XiZMJPN8cFlltvWvxTrM/yuI76lif4671+PI3/i/RiR/0HDThJ6+NrpS5EMCg1cuG6E2Xo/BN83ZcdAhtzYbBKtDHBxUVi4Mc9QTcVE4ZXcO/1NLsddfRrekGUQvudMSJRt9ibQrhts6nj1B9vMlDSgq3bVnMUKth6gquygu3ZmCG8GJC4wIc4RZ+cMO/nZUxiLrjNRnPOWL0L9YpTHGPwPzaApubftE+Pe1A08+O1nIhZWc4P9BqrqUCd1xJWPiPkZovVX3z24hyDRdgMach7UZdB9g+yZH5guZ4eM7ZWnF1L6Qh8vYS2lGRrBrhT1IsPPbaPZvN1DkppHo7nI4YAAPFRQg0vJ3qZhSobUNVP1+rm/yvZ15lNk/1Bnr9qNF1jcZax9UI7MYkklhgHOmqt+mEO/uiQlAHz6XsnGSbL4aLZ1BZRvDin1AwGM5M/2fmu/X4k50TOvws+O1LtJGI8gfAmKKvJCttg4C0KNkpHiVnNIU3W2mqIlhuejbeCrcg37qsOk2YKLd0UZPtiTquOpVARF4RgoFapAkRa4gmG7nyXc4pR5sX7X5xeiHAY1MldT/gqNSYAI+DuKgvYOHeRxlzrANRpBpbVVR22vCFmZ9NBUh+W021I0WUwyGL1SJcPtJle7NtA85bC2a9kmJKQ5IJL716KPV19JfbU//Tftva3vizKi0+aqIGrhXUH0sLDtVp4q3IUfoS+V/F7zeA6k=";
        String token = "0IpKB7sgVXwss8EAg8eOBuQnLly3+DRmlDmqG4DmHuZxAxi1bCCV6JJLeJS69LW5DeXQzgSRmB29rPP1v36CrAEaUZXt55MsnJHUh4NOOCNSEMtVZMPb/JTHAR7C9l6+dszMeauu/lbVxAtSPA8N12nc+26/OaN9iVFiXCLUZLEFCbzZXVcfitSA9AND/TB2SznB7hjt0hYlUveOQ4P41TMDo6PXy8A6+aR1FLVpyd/JZ2kkE/DUmzfMc8rjomV7ZsKScpiHAo8LRxBQ3tKIk5ZtTHtl1FfSBc7v3Vi7ZBh2CP/v17BU6ZxEF9/D5Afm4Y2efTz8NMmUjtLaf0p+MwNleTIQFmHA5ItMiONcJB7y/K9gKl+X+6GuRrtEK/GeR+s0m7tdo8BzFQuhHcMBtClqzckUFRo9i+wxLI7qIu3tyDIc5Z3M8MWvMb1hYU/x0rMn92muRIIQDLCfO7JLHNuiuBVeqaCnxLbivo4xyiEVadl7GH3Y0pnctbzs5qUVLGYul/5AMbR1nBPGnejBPFtK0rIu3ROvDS77BGFx7tQ+fc2GV7CzuYsp580QbiJKgpKlGdq0rG+WlJWmP2amg9HnYVygwmr4Rfa79kh8sdF1oSwz7+XWzHb2uB+RtvLQwGShKmxrwysx1GOp0TfUyAtla9cULWlultxfmYvT/4cicTubBiE/30X0DtV+4aROLtr42fXCMwb0+QUVje1me2F7wZxNRk3kNIqmFvP4SmwBs6SYbVmcqcpLmvWnEn3hZ+61jZSHIC+LWfnV0lACSLOe6X6+cNGCMUwX9xa5QcS0yaAOa3X8O195n7JHQQMwaFxJYVG2Qm/mP52GrfMMUTT8/q/e9sWKkJ2KHzcEFeYcw1CSJ7WBGIX3uAI86OXrATrZ0LlNrqG15IFrevNSaEBbBSxA6Ld+Xl+SXMEkbJK+aK5pO7PXqDBt1DwJZv/WyTN2QllrGktNChWvOuGwKvukfnV6v/ZQrD2pdw9Zo84AiPzsom9uPxLqLm8Z1rU4BSgrXvulceN+Uy8mIWtJhvDDMRvgaC3trMtJuVqiqSTA0Yg8ChWs486WqpVwK+MRm5EOvfvBDNx7y/JlgO3+fDadgL59WJL6LYu//d3ggdhakkqKE+iWVghTyLqY0tJMunNP6dSJGGDzkpU5dhKv/2periI16srswKIQo93uWIy0zo7uTQt8tO2vtXX1YVkP607RxR/Yo4QKxkSX+HbqszwESLgRkeJCLXU5uogqKXc9FouZAygiwW6p01X99/uiI+h6e2NcWRxmxn47j/UOD6+Gce3N7ztxdtdY1PRS+fiaxlMQkUqyc8mWIwgWfQ+unqSgs1Pxy9BROzNADQpvfgFUXCCKuvk+7mLM+nplvfFJ9ZtbtV1QlPs+6YW91K2VWr8jRP9sUzvIlgPhJ0GQYYdqLydALBY/kuVc0cOOhewZTl7e5dOSwJLvHr4aVsKJ/lZyhPuE8ktUkuKCLV1bMQyzvJbAU4coUb1AsY4KfYKNxcpdB7TqoPqWG0G06G1ypF2PlLGW0ZMjSYYfAILEsFUs84/wcmU3j5JSqA2rza7UeYqSaB14w+vx1GFKThliGrYGtKOvMJWrPswTiqEKOlE5GITWQc67Nbm/wLeDLHuZ1cUrObMDbMrbHKzXU5cfD2EQNmxvuFMk7LOhCGlWIc9PYx7K3SvJIodUuCTBPnstuqQNK6eFYtaPVOSmq5iXeij3lnXVGoQmEiNpJvz3dMvIGvwTEJZbZnwZKtuGUhBw59dp+V9AAJjEO5q/AHMSzyCweXX8v/WL0npEPofpjBhpCyCrkdtl1gxso/EEmq1qUc9wmWD14UGwHxD38leylIP9cZClMqK/0+mQeAX1YVDGCV+FhJYEViun0RnSkEg=";
        token = AESUtils.desEncrypt(token, "sz.wenge.app2021", "sz.wenge.app2021");
//
//        System.out.println(token);
        Map<String, Object> map = parseMap(token);
        System.out.println(JSONUtil.toJsonStr(map));
//        Map<String, Object> data = new HashMap<>();
//        data.put("name", "yangyunjun");
//        data.put("userid", "1234");
//        token = generateCustomToken(data);
//        System.out.println("token:" + token);
//        Claims claims = parseCustomToken(token);
//        System.out.println("result:" + JSON.toJSONString(claims.get("name") ));

        //System.out.println(generateMysbOrSpa("19874132225","SPA"));
        //System.out.println(generateMysbOrSpa("19874132225","MYSB"));
    }
}
