package org.tbk.bitcoin.tool.fee.btcdotcom;

import org.tbk.bitcoin.tool.fee.FeeProvider;
import org.tbk.bitcoin.tool.fee.FeeRecommendationRequest;
import org.tbk.bitcoin.tool.fee.FeeRecommendationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BtcdotcomFeeProvider implements FeeProvider {
    @Override
    public Flux<FeeRecommendationResponse> request(FeeRecommendationRequest request) {
        return Flux.empty();
    }
}
