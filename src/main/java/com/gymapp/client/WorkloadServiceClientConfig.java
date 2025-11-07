package com.gymapp.client;

import com.gymapp.security.jwt.JwtUtil;
import com.gymapp.security.user.UserDetailsCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WorkloadServiceClientConfig {

    private final Logger logger = LoggerFactory.getLogger(WorkloadServiceClientConfig.class);

    @Bean
    public WorkloadServiceClient workloadServiceClient(RestClient.Builder restClientBuilder, JwtUtil jwtUtil) {

        String serviceToken = jwtUtil.generateToken(
                new UsernamePasswordAuthenticationToken(
                        new UserDetailsCustom("training-service", "password", List.of(new SimpleGrantedAuthority("ROLE_SYSTEM"))),
                        null
                )
        );

        logger.info("Generated service JWT token: {}", serviceToken);

        RestClient restClient = restClientBuilder
                .baseUrl("http://gymapp-workload-service")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceToken)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = StreamUtils.copyToString(res.getBody(), StandardCharsets.UTF_8);
                    logger.warn("Workload Service returned {} for {}: {}",
                            res.getStatusCode(), req.getURI(), body);
                    throw new ResponseStatusException(res.getStatusCode(), body);
                })
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(WorkloadServiceClient.class);
    }
}
