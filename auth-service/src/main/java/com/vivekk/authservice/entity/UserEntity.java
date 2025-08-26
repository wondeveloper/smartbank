package com.vivekk.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "registered_user_details")
@AllArgsConstructor
public class UserEntity implements Serializable {
    public UserEntity(String firstName, String lastName, String emailId, Boolean isAdmin, Boolean isRevoked, LocalDateTime registeredOn, LocalDateTime profileUpdatedDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.isAdmin = isAdmin;
        this.isRevoked = isRevoked;
        this.registeredOn = registeredOn;
        this.profileUpdatedDate = profileUpdatedDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name",length = 20, nullable = false, updatable = false)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "email_id", length = 40)
    private String emailId;

    @Column(name = "admin", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAdmin;

    @Column(name = "is_revoked", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isRevoked;

    @Column(name = "registered_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registeredOn = LocalDateTime.now();

    @Column(name = "profile_updated_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime profileUpdatedDate = LocalDateTime.now();


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserEntity that = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
