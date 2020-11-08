package org.tbk.bitcoin.jsonrpc;

import com.msgilligan.bitcoinj.rpc.BitcoinClient;
import com.msgilligan.bitcoinj.rpc.RpcConfig;

public final class BitcoinJsonRpcClientFactoryImpl implements BitcoinJsonRpcClientFactory {

    @Override
    public BitcoinClient create(RpcConfig config) {
        return new BitcoinClient(config);
    }
}
