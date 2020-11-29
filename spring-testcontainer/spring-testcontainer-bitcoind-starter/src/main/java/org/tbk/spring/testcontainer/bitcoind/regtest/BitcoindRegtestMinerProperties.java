package org.tbk.spring.testcontainer.bitcoind.regtest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Optional;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.spring.testcontainer.bitcoind-regtest-miner",
        ignoreUnknownFields = false
)
public class BitcoindRegtestMinerProperties {
    private static final NextBlockDurationProperties DEFAULT_NEXT_BLOCK_DURATION = new NextBlockDurationProperties() {{
        setMinDurationInMillis(1_000L);
        setMaxDurationInMillis(10_000L);
    }};

    /**
     * Whether the client should be enabled
     */
    private boolean enabled;

    NextBlockDurationProperties nextBlockDuration;

    public NextBlockDurationProperties getDefaultNextBlockDuration() {
        return Optional.ofNullable(nextBlockDuration)
                .orElse(DEFAULT_NEXT_BLOCK_DURATION);
    }

    @Data
    public static class NextBlockDurationProperties {
        private static final long DEFAULT_MIN_DURATION_IN_MILLIS = 1_000L;
        private static final long DEFAULT_MAX_DURATION_IN_MILLIS = 60_000L;

        Long minDurationInMillis;
        Long maxDurationInMillis;

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
