package com.utilsrv.preader.auth;

import com.utilsrv.preader.auth.token.DeviceIdAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TFUAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        String deviceId = request.getHeader("device-id");
        // disable device id as it will cause too many garbage records in database
        deviceId = "SINGLE-DEVICE-ID-FOR-ANONYMOUS-USER";
        if (StringUtils.isNotEmpty(deviceId)) {
            return new DeviceIdAuthenticationToken(deviceId);
        }
        return null;
    }
}
