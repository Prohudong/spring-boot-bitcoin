package org.tbk.spring.testcontainer.eps.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tbk.spring.testcontainer.eps.ElectrumPersonalServerContainer;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ElectrumPersonalServerContainerApplicationTest {

    @SpringBootApplication
    public static class LndContainerTestApplication {

        public static void main(String[] args) {
            new SpringApplicationBuilder()
                    .sources(LndContainerTestApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
        }
    }

    @Autowired(required = false)
    private ElectrumPersonalServerContainer<?> container;

    @Test
    public void contextLoads() {
        assertThat(container, is(notNullValue()));
        assertThat(container.isRunning(), is(true));

        Boolean ranForMinimumDuration = Flux.interval(Duration.ofMillis(10))
                .map(foo -> container.isRunning())
                .filter(running -> !running)
                .timeout(Duration.ofSeconds(3), Flux.just(true))
                .blockFirst();

        assertThat("container ran for the minimum amount of time to be considered healthy", ranForMinimumDuration, is(true));
    }
}

