package com.fintech.platform.auth.entity;

import com.fintech.platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity
{
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
}