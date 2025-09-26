package com.gymapp.controller;

import com.gymapp.model.Role;
import com.gymapp.model.User;
import com.gymapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.FOUND).body(roleService.getRoles());
    }

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody Role theRole) {
        roleService.createRole(theRole);
        return ResponseEntity.ok("Role has been successfully created");
    }

    @DeleteMapping("/{roleId}")
    public void deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Role> getByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.FOUND).body(roleService.findByName(name));
    }

    @DeleteMapping("/deactivation/{roleId}")
    public void stripAllUsersOfRole(@PathVariable Long roleId) {
        roleService.stripAllUsersOfRole(roleId);
    }

    @DeleteMapping("/removal")
    public ResponseEntity<User> stripUserOfRole(
            @RequestParam Long userId,
            @RequestParam Long roleId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(roleService.stripUserOfRole(userId, roleId));
    }

    @PostMapping("/assignment")
    public ResponseEntity<User> assignUserToRole(
            @RequestParam Long userId,
            @RequestParam Long roleId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.assignUserToRole(userId, roleId));
    }
}
