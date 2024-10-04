package com.BE.service.interfaceServices;

import org.springframework.web.multipart.MultipartFile;

public interface IAdminService {
    void importCSV(MultipartFile file);
}
