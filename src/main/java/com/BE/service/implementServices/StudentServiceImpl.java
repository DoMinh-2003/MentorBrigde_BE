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

    // Define regex patterns as constants
    private static final String STUDENT_CODE_PATTERN = "^[A-Za-z]{2}\\d{6}$"; // Two letters followed by six digits
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

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

        Page<User> users;

        // Determine search type and fetch results accordingly
        if (searchTerm.matches(STUDENT_CODE_PATTERN)) {
            users = userRepository.findByStudentCode(searchTerm, pageable);
        } else if (searchTerm.matches(EMAIL_PATTERN)) {
            users = userRepository.findByEmail(searchTerm, pageable);
        } else {
            users = userRepository.findByFullNameContaining(searchTerm, pageable);
        }

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
}
