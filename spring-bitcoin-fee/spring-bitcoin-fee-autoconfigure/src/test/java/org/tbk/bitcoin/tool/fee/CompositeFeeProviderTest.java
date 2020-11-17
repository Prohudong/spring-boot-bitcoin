package org.tbk.bitcoin.tool.fee;

import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.tbk.bitcoin.tool.fee.bitcoinerlive.config.BitcoinerliveFeeClientAutoConfiguration;
import org.tbk.bitcoin.tool.fee.blockchaininfo.config.BlockchainInfoFeeClientAutoConfiguration;
import org.tbk.bitcoin.tool.fee.config.BitcoinFeeClientAutoConfiguration;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CompositeFeeProviderTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void itShouldVerifyThatCompositeProviderHasProviders() {
        this.contextRunner.withUserConfiguration(BitcoinFeeClientAutoConfiguration.class).run(context -> {
            CompositeFeeProvider compositeFeeProvider = context.getBean(CompositeFeeProvider.class);
            assertThat(compositeFeeProvider, is(notNullValue()));

            assertThat(compositeFeeProvider.getProviderCount(), is(0));
        });

        this.contextRunner.withUserConfiguration(
                BitcoinFeeClientAutoConfiguration.class,
                BitcoinerliveFeeClientAutoConfiguration.class
        ).run(context -> {
            CompositeFeeProvider compositeFeeProvider = context.getBean(CompositeFeeProvider.class);
            assertThat(compositeFeeProvider, is(notNullValue()));

            assertThat(compositeFeeProvider.getProviderCount(), is(1));
        });

        this.contextRunner.withUserConfiguration(
                BitcoinFeeClientAutoConfiguration.class,
                BitcoinerliveFeeClientAutoConfiguration.class,
                BlockchainInfoFeeClientAutoConfiguration.class
        ).run(context -> {
            CompositeFeeProvider compositeFeeProvider = context.getBean(CompositeFeeProvider.class);
            assertThat(compositeFeeProvider, is(notNullValue()));

            assertThat(compositeFeeProvider.getProviderCount(), is(2));
        });
    }

    @Test
    public void itShouldRequestFeeRecommendationSuccessfully() {
        this.contextRunner.withUserConfiguration(
                BitcoinFeeClientAutoConfiguration.class,
                BitcoinerliveFeeClientAutoConfiguration.class,
                BlockchainInfoFeeClientAutoConfiguration.class
        ).run(context -> {
            CompositeFeeProvider compositeFeeProvider = context.getBean(CompositeFeeProvider.class);
            assertThat(compositeFeeProvider, is(notNullValue()));

            FeeRecommendationRequest request = FeeRecommendationRequestImpl.builder()
                    .build();

            List<FeeRecommendationResponse> response = compositeFeeProvider.request(request)
                    .collectList()
                    .block(Duration.ofSeconds(20));

            assertThat(response, hasSize(greaterThan(0)));
        });
    }
}
