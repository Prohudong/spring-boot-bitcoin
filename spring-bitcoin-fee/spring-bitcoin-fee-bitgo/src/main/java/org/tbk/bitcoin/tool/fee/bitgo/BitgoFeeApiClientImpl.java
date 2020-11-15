package org.tbk.bitcoin.tool.fee.bitgo;

import com.google.common.collect.ImmutableMap;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.tbk.bitcoin.tool.fee.util.MoreHttpClient;
import org.tbk.bitcoin.tool.fee.util.MoreJsonFormat;
import org.tbk.bitcoin.tool.fee.util.MoreQueryString;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class BitgoFeeApiClientImpl implements BitgoFeeApiClient {
    private final CloseableHttpClient client = HttpClients.createDefault();

    private final String baseUrl;
    private final String apiToken;

    public BitgoFeeApiClientImpl(String baseUrl, String apiToken) {
        this.baseUrl = requireNonNull(baseUrl);
        this.apiToken = apiToken;
    }

    @Override
    public BtcTxFeeResponse btcTxFee(BtcTxFeeRequest request) {
        String query = MoreQueryString.toQueryString(ImmutableMap.<String, String>builder()
                .put("numBlocks", Optional.of(request.getNumBlocks())
                        .filter(val -> val > 0L)
                        .map(val -> Long.toString(val, 10))
                        .orElse("2"))
                .build());

        // https://www.bitgo.com/api/v2/btc/tx/fee
        String url = String.format("%s/%s%s", baseUrl, "api/v2/btc/tx/fee", query);
        HttpGet httpRequest = new HttpGet(url);
        String json = MoreHttpClient.executeToJson(client, httpRequest);
        return MoreJsonFormat.jsonToProto(json, BtcTxFeeResponse.newBuilder()).build();
    }
}
