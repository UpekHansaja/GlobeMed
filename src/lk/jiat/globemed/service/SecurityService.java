package lk.jiat.globemed.service;

import java.util.HashSet;
import java.util.Set;
import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.model.Staff;

public class SecurityService {

    private static final Set<String> APPOINTMENT_ALLOWED_ROLES = new HashSet<>();

    static {
        APPOINTMENT_ALLOWED_ROLES.add("Admin");
        APPOINTMENT_ALLOWED_ROLES.add("Doctor");
        APPOINTMENT_ALLOWED_ROLES.add("Nurse");

    }

    public boolean hasPermission(Staff staff, String permissionName) {
        if (staff == null) {
            return false;
        }

        Role role = staff.getRole();
        if (role == null) {
            return false;
        }

        try {
            if (role instanceof Role) {

                java.lang.reflect.Method m = role.getClass().getMethod("getPermissions");
                Object permsObj = m.invoke(role);

                if (permsObj instanceof java.util.Collection) {

                    @SuppressWarnings("unchecked")
                    java.util.Collection<Object> perms = (java.util.Collection<Object>) permsObj;
                    for (Object p : perms) {
                        if (p == null) {
                            continue;
                        }

                        try {
                            java.lang.reflect.Method getName = p.getClass().getMethod("getName");
                            Object name = getName.invoke(p);
                            if (permissionName.equalsIgnoreCase(String.valueOf(name))
                                    || String.valueOf(name).equalsIgnoreCase("APPOINTMENT_MANAGE")) {
                                return true;
                            }
                        } catch (NoSuchMethodException ignored) {
                            System.out.println("Method Exception : " + ignored);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException nsme) {
            System.out.println("Method Exception : " + nsme);

        } catch (Exception ex) {
            System.out.println("Exception : " + ex);

        }

        String roleName = role.getName() != null ? role.getName().trim() : "";
        if ("APPOINTMENT_MANAGE".equalsIgnoreCase(permissionName)) {
            return APPOINTMENT_ALLOWED_ROLES.contains(roleName);
        }

        return false;
    }
}
