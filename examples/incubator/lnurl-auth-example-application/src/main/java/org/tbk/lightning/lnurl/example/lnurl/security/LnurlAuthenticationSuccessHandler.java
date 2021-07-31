package org.tbk.lightning.lnurl.example.lnurl.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
class LnurlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.debug("Received successful lnurl-auth request of user '{}'", authentication.getPrincipal());

        response.setStatus(HttpStatus.OK.value());

        Map<String, String> successBody = ImmutableMap.<String, String>builder()
                .put("status", "OK")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(successBody));
    }
}
