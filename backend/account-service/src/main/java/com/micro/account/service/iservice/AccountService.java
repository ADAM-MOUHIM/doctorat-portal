package com.micro.account.service.iservice;

import com.micro.account.dto.AccountResponse;
import com.micro.account.dto.CreateAdminRequest;
import com.micro.account.dto.RegisterRequest;
import com.micro.account.dto.RegisterResponse;

import java.util.UUID;
import java.util.List;

public interface AccountService {
    RegisterResponse register(RegisterRequest request);
    AccountResponse createAdmin(CreateAdminRequest request);
    AccountResponse getById(UUID id);
    List<AccountResponse> listPending();
    AccountResponse approve(UUID id);
    AccountResponse reject(UUID id);
    List<AccountResponse> listAdmins();
    List<AccountResponse> listAll();
    AccountResponse activateAdmin(UUID id);
    AccountResponse suspendAdmin(UUID id);
    AccountResponse verifyEmail(String token);
}

