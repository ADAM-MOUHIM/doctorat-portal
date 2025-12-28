package com.micro.auth.client;

import com.micro.auth.dto.AccountAuthInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", path = "/internal/accounts")
public interface AccountClient {

    @GetMapping("/identifier/{identifier}")
    AccountAuthInfo getByIdentifier(@PathVariable("identifier") String identifier);
}

