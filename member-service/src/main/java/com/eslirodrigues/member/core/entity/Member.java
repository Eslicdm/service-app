package com.eslirodrigues.member.core.entity;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String photo;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "manager_id")
    private String managerId;

    public Member() {
    }

    public Member(
            Long id, String name, String email,
            LocalDate birthDate, String photo,
            ServiceType serviceType, String managerId
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.photo = photo;
        this.serviceType = serviceType;
        this.managerId = managerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", birthDate=" + birthDate + ", photo='" + photo + '\'' + ", serviceType=" + serviceType + ", managerId='" + managerId + '\'' + '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass;
        if (o instanceof HibernateProxy hibernateProxy) {
            oEffectiveClass =
                    hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        } else {
            oEffectiveClass = o.getClass();
        }
        Class<?> thisEffectiveClass;
        if (this instanceof HibernateProxy hibernateProxy) {
            thisEffectiveClass =
                    hibernateProxy.getHibernateLazyInitializer().getPersistentClass();
        } else {
            thisEffectiveClass = this.getClass();
        }
        if (thisEffectiveClass != oEffectiveClass) return false;
        if (o instanceof Member member) {
            return getId() != null && Objects.equals(getId(), member.getId());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy) {
            return hibernateProxy.getHibernateLazyInitializer()
                    .getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}