package com.BE.service.implementServices;

import com.BE.enums.RoleEnum;
import com.BE.model.entity.Semester;
import com.BE.model.entity.User;
import com.BE.repository.SemesterRepository;
import com.BE.repository.UserRepository;
import com.BE.service.interfaceServices.IAdminService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public void importCSV(MultipartFile file) {
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

                    String code = values[10].toUpperCase();
                    Semester semester = semesterRepository.findSemesterByCode(code);
                    if (semester != null) {
                        user.getSemesters().add(semester);
                    } else {
                        System.out.println("Invalid semester code for user: " + user.getEmail());
                        continue;
                    }

                    if (isUserExists(user)) {
                        System.out.println("User already exists, skipping: " + user.getEmail());
                        continue;
                    }

                    users.add(user);
                } catch (Exception e) {
                    System.out.println("Error processing user: " + Arrays.toString(values));
                    e.printStackTrace();
                }
            }
            userRepository.saveAll(users); // Save all valid users after processing
        } catch (Exception e) {
            throw new RuntimeException("Failed to import users from CSV", e);
        }
    }


    private boolean isUserExists(User user) {
        return userRepository.existsByStudentCode(user.getStudentCode()) ||
                userRepository.existsByPhone(user.getPhone()) ||
                userRepository.existsByEmail(user.getEmail()) ||
                userRepository.existsByUsername(user.getUsername());
    }


}
