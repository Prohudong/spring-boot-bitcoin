package org.tbk.lnurl.simple.test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.acinq.bitcoin.ByteVector64;
import fr.acinq.bitcoin.Crypto;
import fr.acinq.secp256k1.Hex;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.tbk.lnurl.auth.K1;
import org.tbk.lnurl.auth.LnurlAuth;
import org.tbk.lnurl.simple.auth.SimpleK1;
import org.tbk.lnurl.simple.auth.SimpleLnurlAuth;
import scodec.bits.ByteVector;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Slf4j
class SimpleLnService {
    public static SimpleLnService of(URI domain) {
        return new SimpleLnService(domain);
    }

    private final URI base;

    private final Cache<K1, String> k1Cache = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    @SneakyThrows
    private SimpleLnService(URI domain) {
        this.base = requireNonNull(domain);
    }

    public LnurlAuth createLnUrlAuth() {
        K1 k1 = SimpleK1.random();
        k1Cache.put(k1, "");
        return SimpleLnurlAuth.create(base, k1);
    }

    public boolean verifyLogin(URI loginUri) {
        List<NameValuePair> params = URLEncodedUtils.parse(loginUri, StandardCharsets.UTF_8);

        NameValuePair k1Param = findFirstQueryParamOrThrow(params, "k1");
        NameValuePair keyParam = findFirstQueryParamOrThrow(params, "key");
        NameValuePair sigParam = findFirstQueryParamOrThrow(params, "sig");

        K1 k1 = SimpleK1.fromHex(k1Param.getValue());
        invalidateK1ValueOrThrow(k1);

        ByteVector rawK1 = ByteVector.view(k1.toArray());
        ByteVector64 rawSig = Crypto.der2compact(ByteVector.view(Hex.decode(sigParam.getValue())));
        Crypto.PublicKey rawKey = new Crypto.PublicKey(ByteVector.view(Hex.decode(keyParam.getValue())));

        return Crypto.verifySignature(rawK1, rawSig, rawKey);
    }

    private void invalidateK1ValueOrThrow(K1 k1) {
        String k1OrNull = k1Cache.getIfPresent(k1);
        if (k1OrNull == null) {
            throw new IllegalArgumentException("k1 value has either expired or was not generated by this service.");
        } else {
            k1Cache.invalidate(k1);
            k1Cache.cleanUp();
        }
    }

    private NameValuePair findFirstQueryParamOrThrow(List<NameValuePair> params, String name) {
        return findFirstQueryParam(params, name).orElseThrow(() -> {
            String message = String.format("Cannot find param '%s' in query", name);
            return new IllegalStateException(message);
        });
    }

    private Optional<NameValuePair> findFirstQueryParam(List<NameValuePair> params, String name) {
        return params.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst();
    }
}
