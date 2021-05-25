package org.tbk.bitcoin.tool.fee.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.bitcoin.tool.fee",
        // do not throw exceptions on unknown keys:
        // this enables sub-keys for every client, e.g. "org.tbk.bitcoin.tool.fee.mempoolspace.enabled"
        ignoreUnknownFields = true
)
public class BitcoinFeeClientAutoConfigProperties implements Validator {

    private boolean enabled;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == BitcoinFeeClientAutoConfigProperties.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        BitcoinFeeClientAutoConfigProperties properties = (BitcoinFeeClientAutoConfigProperties) target;

    }
}
