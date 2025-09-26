package com.gymapp.service.impl;

import com.gymapp.exception.role.RoleAlreadyExistsException;
import com.gymapp.exception.role.RoleNotFoundException;
import com.gymapp.exception.user.UserAlreadyExistsException;
import com.gymapp.exception.user.UserNotFoundException;
import com.gymapp.model.Role;
import com.gymapp.model.User;
import com.gymapp.repository.RoleRepository;
import com.gymapp.service.RoleService;
import com.gymapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {

        String roleName = "ROLE_" + role.getName().toUpperCase();
        Role newRole = new Role(roleName);

        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException("Role already exists.");
        }

        return roleRepository.save(newRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found");
        }
        this.stripAllUsersOfRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    @Override
    public User stripUserOfRole(Long userId, Long roleId) {

        User user = userService.getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        if (role.getUsers().contains(user.getUsername())) {
            role.stripUserOfRole(user);
            roleRepository.save(role);
            return user;
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public User assignUserToRole(Long userId, Long roleId) {

        User user = userService.getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        if (user != null && user.getRoles().contains(role)) {
            throw new UserAlreadyExistsException(user.getFirstName() + " is already assigned the role.");
        }

        role.assignUserToRole(user);
        roleRepository.save(role);

        return user;
    }

    @Override
    public Role stripAllUsersOfRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        this.stripAllUsersOfRole(roleId);
        return roleRepository.save(role);
    }
}
