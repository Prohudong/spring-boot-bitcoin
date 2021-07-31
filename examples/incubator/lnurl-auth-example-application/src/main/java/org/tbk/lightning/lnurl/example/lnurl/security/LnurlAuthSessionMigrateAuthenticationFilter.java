package org.tbk.lightning.lnurl.example.lnurl.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.tbk.lnurl.K1;
import org.tbk.lnurl.simple.SimpleK1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
public class LnurlAuthSessionMigrateAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public LnurlAuthSessionMigrateAuthenticationFilter(String pathRequestPattern, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(pathRequestPattern), authenticationManager);
        this.setAllowSessionCreation(false); // session will only be created on login page
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("got lnurl-auth session migration request: {}", request.getRequestURI());

        Optional<K1> k1 = Optional.of(request)
                .map(HttpServletRequest::getSession)
                .map(it -> (String) it.getAttribute("k1"))
                .map(SimpleK1::fromHex);

        if (k1.isEmpty()) {
            return null; // indicate we cannot attempt session migration and won't handle the authentication.
        }

        LnurlAuthSessionToken lnurlAuthSessionToken = new LnurlAuthSessionToken(k1.get());

        setDetails(request, lnurlAuthSessionToken);

        return this.getAuthenticationManager().authenticate(lnurlAuthSessionToken);
    }

    protected void setDetails(HttpServletRequest request, LnurlAuthSessionToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}