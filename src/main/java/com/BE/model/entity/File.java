package com.BE.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @UuidGenerator
    UUID id;

    String name;

    @Lob
    byte[] content;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    Topic topic;


}
