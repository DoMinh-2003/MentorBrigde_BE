    package com.BE.model.entity;


    import com.BE.enums.RoleEnum;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.Getter;
    import lombok.Setter;
    import lombok.experimental.FieldDefaults;
    import org.hibernate.annotations.UuidGenerator;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.util.*;


    @Entity
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class User implements UserDetails {

        @Id
        @UuidGenerator
        UUID id;

        String fullName;

        @Column(unique = true, nullable = false)
        String studentCode;

        String gender;

        String dayOfBirth;

        @Column(unique = true)
        String phone;

        String address;

        @Column(unique = true)
        String email;

        @Column(length = 550)
        private String avatar;

        @Column(unique = true)
        String username;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password;

        @Enumerated(value = EnumType.STRING)
        RoleEnum role;

        int points;




        @ManyToMany
        @JoinTable(name = "user_semester",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "semester_id")
        )
        Set<Semester> semesters = new HashSet<>();


        @OneToMany(mappedBy = "user")
        @JsonIgnore
        Set<UserTeam> userTeams = new HashSet<>();


        @OneToMany(mappedBy = "creator",cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Topic> topics = new HashSet<>();

        @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
        @JsonIgnore
        Set<TimeFrame> timeFrames = new HashSet<>();

        @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Booking> bookingsStudent = new HashSet<>();

        @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Booking> bookingsMentor = new HashSet<>();


        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Notification> notifications = new HashSet<>();

        @ManyToMany
//    @JsonBackReference
        @JsonIgnore
        @JoinTable(
                name = "room_user",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "room_id"))
        Set<Room> rooms;

        @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Message> messages = new HashSet<>();

        @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
        @JsonIgnore
        Set<Feedback> feedbacks = new HashSet<>();


        @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
        @JsonIgnore
        Set<PointsHistory> pointsHistories = new HashSet<>();

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(this.role.toString()));
            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }


        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
