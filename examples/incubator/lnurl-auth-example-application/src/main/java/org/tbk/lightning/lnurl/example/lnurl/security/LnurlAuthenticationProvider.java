package org.tbk.lightning.lnurl.example.lnurl.security;

import fr.acinq.bitcoin.ByteVector64;
import fr.acinq.bitcoin.Crypto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.tbk.lightning.lnurl.example.domain.WalletUser;
import org.tbk.lightning.lnurl.example.domain.WalletUserService;
import org.tbk.lightning.lnurl.example.lnurl.K1Manager;
import org.tbk.lnurl.K1;
import scodec.bits.ByteVector;

import java.util.List;

@RequiredArgsConstructor
public class LnurlAuthenticationProvider implements AuthenticationProvider {

    @NonNull
    private final K1Manager k1Manager;

    @NonNull
    private final WalletUserService walletUserService;

    @Override
    public boolean supports(Class<?> authentication) {
        return LnurlAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isTrue(supports(authentication.getClass()), "Unsupported authentication class");

        LnurlAuthenticationToken auth = (LnurlAuthenticationToken) authentication;
        if (auth.isAuthenticated()) {
            throw new LnurlAuthenticationException("Already authenticated.");
        }

        K1 k1 = auth.getK1();

        boolean validK1 = k1Manager.isValid(k1);
        if (!validK1) {
            throw new BadCredentialsException("k1 value has either expired or was not generated by this service.");
        }

        boolean loginVerified = verifyLogin(k1, auth.getSignature(), auth.getLinkingKey());
        if (!loginVerified) {
            throw new BadCredentialsException("k1 and signature could not be verified.");
        }

        k1Manager.invalidate(k1);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("wallet_user"));
        LnurlAuthenticationToken newAuth = new LnurlAuthenticationToken(auth.getK1(), auth.getSignature(), auth.getLinkingKey(), authorities);

        WalletUser walletUser = walletUserService.login(newAuth);
        UserDetails user = User.builder()
                .username(walletUser.getName())
                .password("") // there is no "password" with lnurl-auth - set to arbitrary value
                .authorities(authorities)
                .build();

        newAuth.setDetails(user);

        return newAuth;
    }

    private boolean verifyLogin(K1 k1, byte[] signature, byte[] linkingKey) {
        ByteVector rawK1 = ByteVector.view(k1.data());
        ByteVector64 rawSig = Crypto.der2compact(ByteVector.view(signature));
        Crypto.PublicKey rawKey = new Crypto.PublicKey(ByteVector.view(linkingKey));

        return Crypto.verifySignature(rawK1, rawSig, rawKey);
    }

}