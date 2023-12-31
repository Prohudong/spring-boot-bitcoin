package org.tbk.bitcoin.zeromq.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

import static java.util.Objects.requireNonNullElse;

@ConfigurationProperties(
        prefix = "org.tbk.bitcoin.zeromq",
        ignoreUnknownFields = false
)
@Getter
@AllArgsConstructor(onConstructor = @__(@ConstructorBinding))
public class BitcoinZeroMqClientAutoConfigurationProperties {

    public record BitcoinKmpConfig(boolean enabled) {
    }

    public record BitcoinjConfig(boolean enabled) {
    }

    private boolean enabled;

    private BitcoinKmpConfig bitcoinKmp;


    private BitcoinjConfig bitcoinj;

    private Network network;

    private String zmqpubhashtx; // Publishes transaction hashes

    private String zmqpubhashblock; // Publishes block hashes

    private String zmqpubrawblock; // Publishes raw block information

    private String zmqpubrawtx; // Publishes raw transaction information

    public Optional<String> getZmqpubhashtx() {
        return Optional.ofNullable(zmqpubhashtx);
    }

    public Optional<String> getZmqpubhashblock() {
        return Optional.ofNullable(zmqpubhashblock);
    }

    public Optional<String> getZmqpubrawblock() {
        return Optional.ofNullable(zmqpubrawblock);
    }

    public Optional<String> getZmqpubrawtx() {
        return Optional.ofNullable(zmqpubrawtx);
    }

    public Network getNetwork() {
        return requireNonNullElse(network, Network.mainnet);
    }
}
