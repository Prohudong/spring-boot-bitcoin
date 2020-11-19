package org.tbk.bitcoin.tool.fee;

import java.math.BigDecimal;
import java.util.List;

public interface FeeRecommendationResponse {

    List<FeeRecommendation> getFeeRecommendations();

    ProviderInfo getProviderInfo();

    interface FeeRecommendation {
        SatPerVbyte getSatPerVbyte();
    }

    interface SatPerVbyte {
        BigDecimal getSatPerVbyteValue();
    }
}
