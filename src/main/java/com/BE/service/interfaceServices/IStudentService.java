package com.BE.service.interfaceServices;

import com.BE.model.entity.User;
import com.BE.model.response.UserResponse;
import org.springframework.data.domain.Page;

public interface IStudentService {
    Page<UserResponse> searchStudents(String searchTerm, int offset, int size,
                                               String sortBy, String sortDirection);

    void saveStudent(User user);
    User getStudentByEmail(String email);
}
