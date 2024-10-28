package com.BE.service.implementServices;

import com.BE.enums.RoleEnum;
import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ConfigMapper;
import com.BE.mapper.UserMapper;
import com.BE.model.entity.Config;
import com.BE.model.entity.Semester;
import com.BE.model.entity.User;
import com.BE.model.request.ConfigRequest;
import com.BE.model.response.ConfigResponse;
import com.BE.model.response.UserResponse;
import com.BE.repository.ConfigRepository;
import com.BE.repository.SemesterRepository;
import com.BE.repository.UserRepository;
import com.BE.service.interfaceServices.IAdminService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    ConfigMapper configMapper;

    @Override
    public void importCSV(MultipartFile file) {
       Config config =  configRepository.findFirstBy();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            List<User> users = new ArrayList<>();
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                try {
                    User user = new User();
                    user.setFullName(values[0]);
                    user.setStudentCode(values[1]);
                    user.setGender(values[2]);
                    user.setDayOfBirth(values[3]);
                    user.setPhone(values[4]);
                    user.setAddress(values[5]);
                    user.setEmail(values[6]);
                    user.setAvatar(values[7]);
                    user.setUsername(values[8]);
                    try {
                        user.setRole(RoleEnum.valueOf(values[9].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid role found in CSV: " + values[9]);
                        continue;
                    }

                    Semester semester = semesterRepository.findByStatus(SemesterEnum.UPCOMING).orElseThrow(() -> new NotFoundException("Semester not found"));
                    if (semester != null) {
                        user.getSemesters().add(semester);
                    } else {
                        System.out.println("Invalid semester code for user: " + user.getEmail());
                        continue;
                    }

                    if (isUserExists(user)) {
                        semester.getUsers().add(user);
                        semesterRepository.save(semester);
                        System.out.println("User already exists, skipping: " + user.getEmail());
                        continue;
                    }
                    user.setPoints(config.getPointsDeducted());
                    users.add(user);
                } catch (Exception e) {
                    System.out.println("Error processing user: " + Arrays.toString(values));
                    e.printStackTrace();
                }
            }
            userRepository.saveAll(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import users from CSV", e);
        }
    }


    @Override
    public Page<UserResponse> searchUsers(String search, RoleEnum role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.searchUsers(search, role, pageable);
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public ConfigResponse createConfig(ConfigRequest configRequest) {
        Config config = configRepository.findFirstBy();
        if(config != null){
            throw new BadRequestException("Config đã tồn tại");
        }
        config = configMapper.toConfig(configRequest);
        return configMapper.toConfigResponse(configRepository.save(config));
    }

    @Override
    public ConfigResponse getConfig() {
        return configMapper.toConfigResponse(configRepository.findFirstBy());
    }

    @Override
    public ConfigResponse updateConfig(UUID id, ConfigRequest configRequest) {
        Config config  = configRepository.findById(id).orElseThrow(() -> new NotFoundException("Config not found"));
        configMapper.toUpdateConfig(config, configRequest);
        return configMapper.toConfigResponse(configRepository.save(config));
    }


    private boolean isUserExists(User user) {
        return userRepository.existsByStudentCode(user.getStudentCode()) ||
                userRepository.existsByPhone(user.getPhone()) ||
                userRepository.existsByEmail(user.getEmail()) ||
                userRepository.existsByUsername(user.getUsername());
    }








}
