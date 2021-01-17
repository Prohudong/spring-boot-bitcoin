package org.tbk.tor.spring.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.berndpruenster.netlayer.tor.HiddenServiceSocket;
import org.berndpruenster.netlayer.tor.Tor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbk.tor.hs.HiddenServiceDefinition;
import org.tbk.tor.spring.health.HiddenServiceHealthIndicator;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(CompositeHealthContributorConfiguration.class)
@ConditionalOnEnabledHealthIndicator("hiddenService")
@ConditionalOnBean(name = "torHttpClient")
@AutoConfigureAfter(TorAutoConfiguration.class)
public class HiddenServiceHealthContributorAutoConfiguration extends
        CompositeHealthContributorConfiguration<HiddenServiceHealthIndicator, HiddenServiceDefinition> {

    private final CloseableHttpClient torHttpClient;

    public HiddenServiceHealthContributorAutoConfiguration(@Qualifier("torHttpClient") CloseableHttpClient torHttpClient) {
        this.torHttpClient = requireNonNull(torHttpClient);
    }

    @Override
    protected HiddenServiceHealthIndicator createIndicator(HiddenServiceDefinition bean) {
        return new HiddenServiceHealthIndicator(bean, this.torHttpClient);
    }

    @Bean
    @ConditionalOnBean(HiddenServiceDefinition.class)
    @ConditionalOnMissingBean(name = {"hiddenServiceHealthIndicator", "hiddenServiceHealthContributor"})
    public HealthContributor hiddenServiceHealthContributor(Map<String, HiddenServiceDefinition> hiddenServices) {
        return createContributor(hiddenServices);
    }
}

