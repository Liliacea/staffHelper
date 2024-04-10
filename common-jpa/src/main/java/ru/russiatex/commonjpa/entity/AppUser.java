package ru.russiatex.commonjpa.entity;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.russiatex.commonjpa.entity.enums.UserState;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")

public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;
    @CreationTimestamp
    private LocalDateTime firstLoginDate;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState state;

}
