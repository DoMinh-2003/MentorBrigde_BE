package com.BE.service.implementServices;

import com.BE.mapper.UserMapper;
import com.BE.model.entity.User;
import com.BE.model.response.UserResponse;
import com.BE.repository.UserRepository;
import com.BE.service.interfaceServices.IStudentService;
import com.BE.utils.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements IStudentService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PageUtil pageUtil;

    public StudentServiceImpl(UserRepository userRepository,
                              UserMapper userMapper,
                              PageUtil pageUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.pageUtil = pageUtil;
    }

    @Override
    public Page<UserResponse> searchStudents(String searchTerm, int offset, int size,
                                             String sortBy, String sortDirection) {
        // Check offset
        pageUtil.checkOffset(offset);

        // Create pageable with offset -1 (default in controller is 1)
        Pageable pageable = pageUtil.getPageable(offset - 1, size, sortBy, sortDirection);

        Page<User> users = userRepository.findByFullNameContainingOrStudentCodeOrEmail(searchTerm, pageable);

        // Return user responses
        return convertToUserResponses(users);
    }

    private Page<UserResponse> convertToUserResponses(Page<User> users) {
        // Convert Page<User> to Page<UserResponse>
        return users.map(userMapper::toUserResponse);
    }

    @Override
    public void saveStudent(User user) {
        userRepository.save(user);
    }

    @Override
    public User getStudentByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
