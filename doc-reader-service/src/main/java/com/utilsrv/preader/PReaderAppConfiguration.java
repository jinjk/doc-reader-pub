package com.utilsrv.preader;

import com.utilsrv.doc.DFSConfiguration;
import com.utilsrv.preader.auth.TFUAuthenticationConverter;
import com.utilsrv.preader.auth.providers.DeviceIdAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Configuration
@Import(DFSConfiguration.class)
public class PReaderAppConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("*");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DeviceIdAuthenticationProvider deviceIdAuthenticationProvider,
                                           TFUAuthenticationConverter authenticationConverter) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(new ProviderManager(
                List.of(deviceIdAuthenticationProvider)),
                authenticationConverter);

        authenticationFilter.setSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            }
        });

        return http
                .addFilterBefore(authenticationFilter,
                        BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .antMatchers("/books", "/books/*","/books/**/*")
                        .authenticated())
                .csrf().disable()
                .build();
    }

//    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/*");
    }


}
