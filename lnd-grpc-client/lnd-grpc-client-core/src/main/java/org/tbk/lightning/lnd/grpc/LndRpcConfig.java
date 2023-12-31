package org.tbk.lightning.lnd.grpc;

import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.lightningj.lnd.wrapper.MacaroonContext;

public interface LndRpcConfig {

    /**
     * IP address or hostname where lnd daemon is reachable.
     * e.g. localhost, 192.168.0.1, etc.
     *
     * @return IP address or hostname where lnd daemon is reachable.
     */
    String getHost();

    /**
     * Port where lnd daemon is listening.
     *
     * @return Port where lnd daemon is listening.
     */
    Integer getPort();

    /**
     * The {@link MacaroonContext} to access the lnd api.
     *
     * @return The {@link MacaroonContext} to access the lnd api.
     */
    MacaroonContext getMacaroonContext();

    /**
     * The {@link SslContext} to access the lnd api.
     *
     * @return The {@link SslContext} to access the lnd api.
     */
    SslContext getSslContext();
}
