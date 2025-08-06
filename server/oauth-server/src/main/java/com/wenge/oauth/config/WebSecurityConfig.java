package com.wenge.oauth.config;

import com.wenge.oauth.filter.JwtFilter;
import com.wenge.oauth.filter.WgAuthenticationFilter;
import com.wenge.oauth.holder.WgLogoutSuccessHandler;
import com.wenge.oauth.provider.qywx.QywxAuthenticationProvider;
import com.wenge.oauth.provider.sms.SmsAuthenticationProvider;
import com.wenge.oauth.provider.userpassword.WgAuthenticationProvider;
import com.wenge.oauth.provider.wx.WxAuthenticationProvider;
import com.wg.appframe.core.filter.CoreFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${appframe.token.check:true}")
    private boolean securityEnabled;

    @Autowired
    private WgAuthenticationProvider wgAuthenticationProvider;

    @Autowired
    private WxAuthenticationProvider wxAuthenticationProvider;
    @Autowired
    private QywxAuthenticationProvider qywxAuthenticationProvider;

    @Autowired
    private SmsAuthenticationProvider smsAuthenticationProvider;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WgLogoutSuccessHandler wgLogoutSuccessHandler;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CoreFilter coreFilter;

    public static String LOGIN_PATH = "/login";
    public static String LOGOUT_PATH = "/doLogout";


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!securityEnabled) {
            // 关闭Spring Security认证
            http
                    .addFilterBefore(coreFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll().and()
                    .headers().frameOptions().disable(); // 如果使用了iframe，需要禁用
        } else {
            http
                    .addFilterBefore(coreFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .csrf().disable()
                    // 不需要通过 Cookie 进行身份验证
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests(
                            auth -> auth
                                    .anyRequest()
                                    .permitAll()
                    )
                    .formLogin(
                            form -> form
                                    .loginPage(LOGIN_PATH)
                                    .and()
                                    .addFilterAt(new WgAuthenticationFilter(authenticationManager, successHandler, failureHandler), UsernamePasswordAuthenticationFilter.class)
                    )
                    .logout(logout -> {
                        logout.logoutUrl(LOGOUT_PATH)
                                .logoutSuccessUrl(LOGIN_PATH)
                                .logoutSuccessHandler(wgLogoutSuccessHandler)
                                .clearAuthentication(true);
                    })
                    .authenticationProvider(wgAuthenticationProvider)
                    .authenticationProvider(wxAuthenticationProvider)
                    .authenticationProvider(qywxAuthenticationProvider)
                    .authenticationProvider(smsAuthenticationProvider)
                    .headers().frameOptions().disable().cacheControl();
        }

    }


    @Override
    public void configure(WebSecurity web) {
        // 静态资源放行
        web.ignoring().antMatchers(
                "/css/**",
                "/js/**",
                "/img/**", "/components/**", "/error", "/", "/cicd/**", "/cicd-deploy/**", "/doc.html", "/webjars/**", "/swagger-resources", "/v3/api-docs/**", "/oauth/**",
                "/oauthLoginLog/analyzePortalToken",
                "/actuator/**",
                "/oauthLoginLog/analyzePortalToken",
                "/**/api-docs",
                "/swagger/api-docs",
                "/swagger-ui.html",
                "/doc.html",
                "/swagger-resources/**",
                "/druid/**",
                "/model-agent/**",
                "/favicon.ico");
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final Pattern ASSIGNED_AND_NOT_ISO_CONTROL_PATTERN = Pattern
            .compile("[\\p{IsAssigned}&&[^\\p{IsControl}]]*");

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHeaderValues((header) -> {
            String parsed = new String(header.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return ASSIGNED_AND_NOT_ISO_CONTROL_PATTERN.matcher(parsed).matches();
        });
        return firewall;
    }
}
