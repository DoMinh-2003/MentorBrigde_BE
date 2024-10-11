package com.BE.repository;

import com.BE.enums.RoleEnum;
import com.BE.enums.SemesterEnum;
import com.BE.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    boolean existsByStudentCode(String studentCode);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:searchTerm% OR u.studentCode = :searchTerm OR u.email = :searchTerm")
    Page<User> findByFullNameContainingOrStudentCodeOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<User> findByStudentCode(String studentCode, Pageable pageable);

    Page<User> findByEmail(String email, Pageable pageable);
}
