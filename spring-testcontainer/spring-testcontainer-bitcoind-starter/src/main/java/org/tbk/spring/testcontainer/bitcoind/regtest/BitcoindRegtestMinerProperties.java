package org.tbk.spring.testcontainer.bitcoind.regtest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Duration;
import java.util.Optional;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.spring.testcontainer.bitcoind-regtest-miner",
        ignoreUnknownFields = false
)
public class BitcoindRegtestMinerProperties implements Validator {
    private static final NextBlockDurationProperties DEFAULT_NEXT_BLOCK_DURATION = new NextBlockDurationProperties() {{
        setMinDurationInMillis(1_000L);
        setMaxDurationInMillis(10_000L);
    }};

    /**
     * Whether the client should be enabled
     */
    private boolean enabled;

    /**
     * Optional. A static address that all coinbase rewards will go to.
     * If not specified, a new address will be generated by the bitcoin client.
     */
    private String coinbaseRewardAddress;

    private int mineInitialAmountOfBlocks = 0;

    private Boolean scheduledMiningEnabled;

    private NextBlockDurationProperties nextBlockDuration;

    public Optional<String> getCoinbaseRewardAddress() {
        return Optional.ofNullable(coinbaseRewardAddress);
    }

    public NextBlockDurationProperties getNextBlockDuration() {
        return Optional.ofNullable(nextBlockDuration)
                .orElse(DEFAULT_NEXT_BLOCK_DURATION);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == BitcoindRegtestMinerProperties.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        BitcoindRegtestMinerProperties properties = (BitcoindRegtestMinerProperties) target;

        if (mineInitialAmountOfBlocks < 0) {
            String errorMessage = String.format("'mineInitialAmountOfBlocks' must not be less than zero - invalid value: %d", properties.getMineInitialAmountOfBlocks());
            errors.rejectValue("mineInitialAmountOfBlocks", "mineInitialAmountOfBlocks.invalid", errorMessage);
        }
    }

    @Data
    public static class NextBlockDurationProperties {
        private static final long DEFAULT_MIN_DURATION_IN_MILLIS = 1_000L;
        private static final long DEFAULT_MAX_DURATION_IN_MILLIS = 60_000L;

        private Long minDurationInMillis;
        private Long maxDurationInMillis;

        public long getMinDurationInMillis() {
            return Optional.ofNullable(minDurationInMillis)
                    .orElse(DEFAULT_MIN_DURATION_IN_MILLIS);
        }

        public long getMaxDurationInMillis() {
            return Optional.ofNullable(maxDurationInMillis)
                    .filter(val -> val >= getMinDurationInMillis())
                    .orElseGet(() -> Math.max(getMinDurationInMillis(), DEFAULT_MAX_DURATION_IN_MILLIS));
        }

        public Duration getMinDuration() {
            return Duration.ofMillis(getMinDurationInMillis());
        }

        public Duration getMaxDuration() {
            return Duration.ofMillis(getMaxDurationInMillis());
        }
    }
}
