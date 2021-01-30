package org.tbk.bitcoin.autodca.example.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.kraken.service.KrakenAccountService;
import org.springframework.boot.ApplicationArguments;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Slf4j
public class KrakenBalanceCommandRunner extends ConditionalOnNonOptionApplicationRunner {

    private final KrakenExchange exchange;

    public KrakenBalanceCommandRunner(KrakenExchange exchange) {
        super("balance");
        this.exchange = requireNonNull(exchange);
    }

    @Override
    @SneakyThrows
    protected void doRun(ApplicationArguments args) {
        log.debug("Fetch balance on exchange {}", exchange);

        KrakenAccountService accountService = (KrakenAccountService) exchange.getAccountService();
        Map<String, BigDecimal> krakenBalance = accountService.getKrakenBalance();

        Map<String, BigDecimal> krakenPositiveBalances = krakenBalance.entrySet().stream()
                .filter(it -> it.getValue().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (krakenPositiveBalances.isEmpty()) {
            System.out.println("❌ There is no currency pair with a positive balance.");
        } else {
            krakenPositiveBalances.forEach((currencyCode, balance) -> {
                System.out.printf("💰 %5s:\t%s%n", currencyCode, balance.toPlainString());
            });
        }
    }
}
