package com.laboratory.airlinebackend.model;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblRolePermission")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @ManyToOne
    @JoinColumn(name = "roleID")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permissionID")
    private Permission permission;

}
