// src/main/java/com/mytech/virtualcourse/services/RoleService.java
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.RoleDTO;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.RoleMapper;
import com.mytech.virtualcourse.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * Lấy tất cả các vai trò.
     */
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::roleToRoleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy vai trò theo ID.
     */
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.roleToRoleDTO(role);
    }


    @Cacheable("roles")
    public Optional<Role> findByName(ERole roleName) {
        return roleRepository.findByName(roleName);
    }
    /**
     * Tạo một vai trò mới.
     */
    public RoleDTO createRole(RoleDTO roleDTO) {
        // Kiểm tra nếu tên Role đã tồn tại trong hệ thống
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getName());
        }

        // Kiểm tra xem tên Role có phải là một giá trị hợp lệ của enum RoleName không
        try {
            ERole.valueOf(roleDTO.getName().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Role name: " + roleDTO.getName());
        }

        // Chuyển RoleDTO sang Entity và lưu vào cơ sở dữ liệu
        Role role = roleMapper.roleDTOToRole(roleDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.roleToRoleDTO(savedRole);
    }

    /**
     * Cập nhật một vai trò hiện có.
     */
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Kiểm tra nếu tên Role đã thay đổi và tên mới đã tồn tại trong hệ thống
        if (!existingRole.getName().equals(roleDTO.getName()) && roleRepository.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getName());
        }

        // Kiểm tra xem tên Role có phải là một giá trị hợp lệ của enum RoleName không
        try {
            ERole.valueOf(roleDTO.getName().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Role name: " + roleDTO.getName());
        }

        // Cập nhật các thông tin còn lại của Role
        existingRole.setName(roleDTO.getName());
        existingRole.setDescription(roleDTO.getDescription());

        // Lưu Role đã cập nhật
        Role updatedRole = roleRepository.save(existingRole);
        return roleMapper.roleToRoleDTO(updatedRole);
    }

    /**
     * Xóa một vai trò theo ID.
     */
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Kiểm tra xem có tài khoản nào đang được gắn với Role này không
        if (role.getAccounts() != null && !role.getAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete role assigned to accounts.");
        }

        // Xóa Role
        roleRepository.delete(role);
    }
}
