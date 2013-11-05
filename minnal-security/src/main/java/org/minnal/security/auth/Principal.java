/**
 * 
 */
package org.minnal.security.auth;

import java.util.List;

/**
 * @author ganeshs
 *
 */
public interface Principal {
	
	List<Role> getRoles();
	
	List<Permission> getPermissions();
}
