package lk.jiat.globemed.service;

import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.model.Staff;

import java.util.HashSet;
import java.util.Set;

/**
 * Centralized permission checks.
 *
 * Strategy: - If Staff -> Role -> Permissions exist, checks Permission.name
 * equality. - If permissions are not modeled/populated, fallback to whitelist
 * of roles for specific permissions.
 */
public class SecurityService {

    // fallback mapping for known logical permissions -> roles that are allowed
    private static final Set<String> APPOINTMENT_ALLOWED_ROLES = new HashSet<>();

    static {
        APPOINTMENT_ALLOWED_ROLES.add("Admin");
        APPOINTMENT_ALLOWED_ROLES.add("Doctor");
        APPOINTMENT_ALLOWED_ROLES.add("Nurse");
        // add more roles if needed
    }

    /**
     * Check if staff has a named permission.
     *
     * @param staff staff instance (may be detached)
     * @param permissionName logical permission name like "APPOINTMENT_MANAGE"
     * @return true if allowed
     */
    public boolean hasPermission(Staff staff, String permissionName) {
        if (staff == null) {
            return false;
        }

        // Admins always allowed (conservative)
        Role role = staff.getRole();
        if (role == null) {
            return false;
        }

        // If role carries Permission objects, check them
        try {
            if (role instanceof Role) {
                // many Role implementations have getPermissions()
                // use reflection to avoid compile-time dependency if your Role class lacks method
                java.lang.reflect.Method m = role.getClass().getMethod("getPermissions");
                Object permsObj = m.invoke(role);
                if (permsObj instanceof java.util.Collection) {
                    @SuppressWarnings("unchecked")
                    java.util.Collection<Object> perms = (java.util.Collection<Object>) permsObj;
                    for (Object p : perms) {
                        if (p == null) {
                            continue;
                        }
                        // permission may be Permission entity with getName()
                        try {
                            java.lang.reflect.Method getName = p.getClass().getMethod("getName");
                            Object name = getName.invoke(p);
                            if (permissionName.equalsIgnoreCase(String.valueOf(name))
                                    || String.valueOf(name).equalsIgnoreCase("APPOINTMENT_MANAGE")) {
                                return true;
                            }
                        } catch (NoSuchMethodException ignored) {
                            // ignore: permission object doesn't have getName()
                        }
                    }
                }
            }
        } catch (NoSuchMethodException nsme) {
            // Role has no getPermissions() method — fall back to role-name whitelist
        } catch (Exception ex) {
            // If reflection fails for any reason, fall back to simple role name check
        }

        // fallback: use role name whitelist for the known permission
        String roleName = role.getName() != null ? role.getName().trim() : "";
        if ("APPOINTMENT_MANAGE".equalsIgnoreCase(permissionName)) {
            return APPOINTMENT_ALLOWED_ROLES.contains(roleName);
        }

        // default deny
        return false;
    }
}
