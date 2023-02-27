package com.moraisrwagner.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/get")
                        .filters(f -> f.addRequestHeader("CustomHeader", "custom URI")
//                        .addRequestParameter("Param", "custom param value")
                        )
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/currency-exchange/**")
                        .uri("lb://currency-exchange"))//talk to eureka and load balance between instances - name of the registered service
                .route(p -> p.path("/currency-conversion/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-feign/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath(
                                "currency-conversion-new/(?<segment>.*)",//Get segment from expression
                                "currency-conversion-feign/${segment}"//append segment from previous expression
                        ))
                        .uri("lb://currency-conversion"))
                .build();
    }
}
