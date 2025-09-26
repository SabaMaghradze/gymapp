package com.gymapp.service;

import com.gymapp.model.Role;
import com.gymapp.model.User;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();

    Role createRole(Role role);

    void deleteRole(Long id);

    Role findByName(String name);

    User stripUserOfRole(Long userId, Long roleId);

    User assignUserToRole(Long userId, Long roleId);

    Role stripAllUsersOfRole(Long roleId);
}
