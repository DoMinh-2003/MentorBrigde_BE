package com.BE.service.interfaceServices;

import com.BE.enums.RoleEnum;
import com.BE.model.entity.Config;
import com.BE.model.request.ConfigRequest;
import com.BE.model.response.ConfigResponse;
import com.BE.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface IAdminService {
    void importCSV(MultipartFile file);
    Page<UserResponse> searchUsers(String search, RoleEnum role,int page,int size);

    ConfigResponse createConfig(ConfigRequest configRequest);

    ConfigResponse getConfig();

    ConfigResponse updateConfig(UUID id, ConfigRequest configRequest);

     Map<String, Object> getDashboardStats();
}
