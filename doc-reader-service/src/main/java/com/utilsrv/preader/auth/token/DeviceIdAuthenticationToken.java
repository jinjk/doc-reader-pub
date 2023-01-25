package com.utilsrv.preader.auth.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class DeviceIdAuthenticationToken extends AbstractAuthenticationToken {
    private String deviceId;

    public DeviceIdAuthenticationToken(String deviceId) {
        super(Collections.EMPTY_LIST);
        this.deviceId = deviceId;
    }

    public DeviceIdAuthenticationToken(String deviceId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.deviceId = deviceId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return deviceId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }
}
