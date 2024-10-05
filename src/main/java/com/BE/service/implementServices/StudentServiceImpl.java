package com.BE.service.implementServices;

import com.BE.mapper.UserMapper;
import com.BE.model.entity.User;
import com.BE.model.response.UserResponse;
import com.BE.repository.UserRepository;
import com.BE.service.interfaceServices.StudentService;
import com.BE.utils.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
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
    public Page<UserResponse> getStudentsByFullNameContaining(String fullName, int offset, int size,
                                                              String sortBy, String sortDirection) {
        // Check offset
        pageUtil.checkOffset(offset);
        if (fullName == null || fullName.trim().isEmpty()) {
            // Return an empty page since no valid fullName was provided
            return Page.empty();
        }
        // Create pageable with offset -1 (default in controller is 1)
        Pageable pageable = pageUtil.getPageable(offset - 1, size, sortBy, sortDirection);
        // Find users by containing name
        Page<User> users = userRepository.findByFullNameContaining(fullName, pageable);
        // Return user responses
        return getUserResponses(users);
    }

    @Override
    public Page<UserResponse> getStudentsByStudentCode(String studentCode, int offset, int size,
                                                       String sortBy, String sortDirection) {
        // Check offset
        pageUtil.checkOffset(offset);
        if (studentCode == null || studentCode.trim().isEmpty()) {
            // Return an empty page since no valid fullName was provided
            return Page.empty();
        }
        // Create pageable with offset -1 (default in controller is 1)
        Pageable pageable = pageUtil.getPageable(offset - 1, size, sortBy, sortDirection);
        // Find users by Student Code
        Page<User> users = userRepository.findByStudentCode(studentCode, pageable);
        // Return user responses
        return getUserResponses(users);
    }

    private UserResponse getUserResponse(User user) {
        // Use mapper to convert entity to response
        return userMapper.toUserResponse(user);
    }

    private Page<UserResponse> getUserResponses(Page<User> users) {
        // convert Page<User> to Page<UserResponse>
        return users.map(this::getUserResponse);
    }
    @Override
    public void saveStudent(User user) {
        userRepository.save(user);
    }

}
