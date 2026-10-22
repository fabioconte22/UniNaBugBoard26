package com.bugboard26.shared.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component 
public class UserServiceClient implements UserDirectory {

    private final RestClient restClient; 

    public UserServiceClient(RestClient.Builder builder,
                             @Value("${services.user-service-url}") String userServiceUrl) {

        this.restClient = builder
                .baseUrl(userServiceUrl)
                .requestInterceptor(forwardAuthorization())
                .build();
    }

    @Override 
    public Map<String, UserSummary> findByEmails(Collection<String> emails) {
        if(emails.isEmpty()) {
            return Map.of(); 
        }
        List<UserSummary> users = restClient.get()
                .uri(uri -> uri.path("/api/users/lookup").queryParam("emails", emails.toArray()).build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserSummary>>() { }); 
        
        return users == null ? Map.of()
                : users.stream().collect(Collectors.toMap(UserSummary::email, Function.identity(), (a, b) -> a)); 
    }

    private static ClientHttpRequestInterceptor forwardAuthorization() {
        return (request, body, execution) -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION); 
                if (authorization != null) {
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization); 
                }
            }
            return execution.execute(request, body); 
        };
    }
    
}
