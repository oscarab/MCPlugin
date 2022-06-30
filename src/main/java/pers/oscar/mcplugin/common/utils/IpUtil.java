package pers.oscar.mcplugin.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class IpUtil {

    public static String getIpAddress(Authentication authentication) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        return details.getRemoteAddress();
    }
}
