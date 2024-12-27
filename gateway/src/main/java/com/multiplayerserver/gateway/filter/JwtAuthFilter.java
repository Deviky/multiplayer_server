package com.multiplayerserver.gateway.filter;


import com.multiplayerserver.gateway.service.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    @Autowired
    private RouteValidator validator;

    @Autowired
    private AuthServiceClient authServiceClient;


    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    System.out.println("Lox");
                    Long playerId = authServiceClient.getUserIdFromToken(authHeader);
                    System.out.println("Player_id is :" + playerId);
                    if (playerId == null){
                        System.out.println("Lox2");
                        throw new RuntimeException("playerId is null");
                    }
                    if (exchange.getRequest().getURI().getPath().startsWith("/room")) {
                        System.out.println("goooo!");
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(builder -> builder.header("X-Player-Id", playerId.toString()))
                                .build();
                        return chain.filter(mutatedExchange);
                    }

                } catch (Exception e) {
                    System.out.println("invalid access...!");
                    throw new RuntimeException("un authorized access to application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }

}
