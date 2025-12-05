package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID profileId;

    @Column
    private String country;

    @Column
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private boolean deleted;

    @Column(columnDefinition = "uuid")
    private UUID deletedBy;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID profileId;
        private String country;
        private String address;
        private String phoneNumber;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private boolean deleted;
        private UUID deletedBy;
        private User user;

        public Builder profileId(UUID profileId) { this.profileId = profileId; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedBy(UUID deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder user(User user) { this.user = user; return this; }

        public Profile build() {
            Profile profile = new Profile();
            profile.profileId = this.profileId;
            profile.country = this.country;
            profile.address = this.address;
            profile.phoneNumber = this.phoneNumber;
            profile.createdAt = this.createdAt;
            profile.updatedAt = this.updatedAt;
            profile.deletedAt = this.deletedAt;
            profile.deleted = this.deleted;
            profile.deletedBy = this.deletedBy;
            profile.user = this.user;
            return profile;
        }
    }
}