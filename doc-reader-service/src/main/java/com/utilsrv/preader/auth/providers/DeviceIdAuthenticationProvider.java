package com.utilsrv.preader.auth.providers;

import com.utilsrv.preader.auth.token.DeviceIdAuthenticationToken;
import com.utilsrv.preader.jpa.entities.Person;
import com.utilsrv.preader.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeviceIdAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DeviceIdAuthenticationToken deviceIdAuth = (DeviceIdAuthenticationToken) authentication;
        String deviceId = (String) deviceIdAuth.getPrincipal();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (deviceId != null) {
            Person person = userService.findOrCreatePersonByDeviceId(deviceId);
            if (person != null) {
                deviceIdAuth.setDetails(person);
                authorities.add(new SimpleGrantedAuthority("read"));
            }
            else {
                throw new BadCredentialsException("Bad Device ID", new NullPointerException("person is null"));
            }
        }
        else {
            throw new BadCredentialsException("Bad Device ID");
        }
        return new DeviceIdAuthenticationToken(deviceId, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(DeviceIdAuthenticationToken.class);
    }
}
