package org.tbk.spring.testcontainer.electrumx.config;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.testcontainers.shaded.com.google.common.base.CharMatcher;

import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.spring.testcontainer.electrumx",
        ignoreUnknownFields = false
)
public class ElectrumxContainerProperties implements Validator {

    /**
     * Whether the client should be enabled
     */
    private boolean enabled;

    /**
     * Specify the user to use on for RPC connections to bitcoind.
     */
    private String rpcuser;

    /**
     * Specify the password to use on for RPC connections to bitcoind.
     */
    private String rpcpass;

    /**
     * Specify the host to use on for RPC connections to bitcoind.
     */
    private String rpchost;

    /**
     * Specify the port to use on for RPC connections to bitcoind.
     */
    private Integer rpcport;

    private List<Integer> exposedPorts;

    public List<Integer> getExposedPorts() {
        return exposedPorts == null ?
                Collections.emptyList() :
                ImmutableList.copyOf(exposedPorts);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == ElectrumxContainerProperties.class;
    }

    /**
     * Validate the container properties.
     * <p>
     * Keep in mind that Testcontainers splits commands on whitespaces.
     * This means, every property that is part of a command, must not contain whitespaces.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ElectrumxContainerProperties properties = (ElectrumxContainerProperties) target;

        String rpcuserValue = properties.getRpcuser();
        if (rpcuserValue == null) {
            String errorMessage = "'rpcuser' must not be null";
            errors.rejectValue("rpcuser", "rpcuser.invalid", errorMessage);
        } else if (rpcuserValue.isBlank()) {
            String errorMessage = "'rpcuser' must not be empty";
            errors.rejectValue("rpcuser", "rpcuser.invalid", errorMessage);
        } else if (containsWhitespaces(rpcuserValue)) {
            String errorMessage = String.format("'rpcuser' must not contain whitespaces - unsupported value: '%s'", rpcuserValue);
            errors.rejectValue("rpcuser", "rpcuser.unsupported", errorMessage);
        }

        String rpcpasswordValue = properties.getRpcpass();
        if (rpcpasswordValue == null) {
            String errorMessage = "'rpcpass' must not be null";
            errors.rejectValue("rpcpass", "rpcuser.invalid", errorMessage);
        } else if (rpcpasswordValue.isBlank()) {
            String errorMessage = "'rpcpass' must not be empty";
            errors.rejectValue("rpcpass", "rpcpassword.invalid", errorMessage);
        } else if (containsWhitespaces(rpcpasswordValue)) {
            String errorMessage = "'rpcpass' must not contain whitespaces - unsupported value";
            errors.rejectValue("rpcpass", "rpcpassword.unsupported", errorMessage);
        }
    }

    private static boolean containsWhitespaces(String value) {
        return CharMatcher.WHITESPACE.matchesAnyOf(value);
    }
}

