package org.tbk.spring.testcontainer.bitcoind.config;

import com.msgilligan.bitcoinj.json.pojo.BlockChainInfo;
import com.msgilligan.bitcoinj.rpc.BitcoinClient;
import com.msgilligan.bitcoinj.rpc.RpcConfig;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tbk.bitcoin.jsonrpc.config.BitcoinJsonRpcClientAutoConfigProperties;
import org.tbk.spring.testcontainer.bitcoind.BitcoindContainer;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class BitcoinContainerWithJsonRpcClientTest {

    @SpringBootApplication
    public static class BitcoinContainerClientTestApplication {

        public static void main(String[] args) {
            new SpringApplicationBuilder()
                    .sources(BitcoinContainerClientTestApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
        }
    }

    @Autowired
    private BitcoinClient bitcoinJsonRpcClient;

    @Test
    public void testGetBlockChainInfo() throws IOException {
        BlockChainInfo blockChainInfo = bitcoinJsonRpcClient.getBlockChainInfo();
        assertThat(blockChainInfo.getChain(), is("regtest"));
    }

    @Test
    public void testGenerateToAddress() throws IOException {
        BlockChainInfo blockChainInfoBefore = bitcoinJsonRpcClient.getBlockChainInfo();
        int blocksAmountBefore = blockChainInfoBefore.getBlocks();

        Address newAddress = bitcoinJsonRpcClient.getNewAddress();

        List<Sha256Hash> initialMinedBlockHashes = bitcoinJsonRpcClient.generateToAddress(1, newAddress);
        assertThat(initialMinedBlockHashes, hasSize(1));

        Sha256Hash initialMinedBlockHash = initialMinedBlockHashes.get(0);

        BlockChainInfo blockChainInfoAfter = bitcoinJsonRpcClient.getBlockChainInfo();

        int blocksAmountAfter = blockChainInfoAfter.getBlocks();
        assertThat(blocksAmountAfter, is(blocksAmountBefore + 1));

        assertThat(blockChainInfoAfter.getBestBlockHash(), is(initialMinedBlockHash));
    }

    @Test
    public void testGenerateToAddressExpectingCoins() throws IOException {
        Address newAddress = bitcoinJsonRpcClient.getNewAddress();

        Coin balanceBefore = bitcoinJsonRpcClient.getBalance();
        assertThat(balanceBefore, is(Coin.ZERO));

        List<Sha256Hash> initialMinedBlockHashes = bitcoinJsonRpcClient.generateToAddress(1, newAddress);
        assertThat(initialMinedBlockHashes, hasSize(1));

        LongAdder counter = new LongAdder();
        while (counter.longValue() < 100) {
            Coin balanceDuringMining = bitcoinJsonRpcClient.getBalance();
            assertThat("balance is zero till 100 blocks are mined", balanceDuringMining, is(Coin.ZERO));

            List<Sha256Hash> newlyMinedBlocks = bitcoinJsonRpcClient.generateToAddress(1, newAddress);
            counter.add(newlyMinedBlocks.size());
        }

        Coin balanceAfter = bitcoinJsonRpcClient.getBalance();
        assertThat(balanceAfter, is(Coin.FIFTY_COINS));
    }
}
