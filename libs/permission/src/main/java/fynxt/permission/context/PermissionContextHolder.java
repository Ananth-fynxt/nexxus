package fynxt.permission.context;

import java.util.Map;

public interface PermissionContextHolder {

	String getScope();

	Map<String, Object> getRolePermissions();
}
