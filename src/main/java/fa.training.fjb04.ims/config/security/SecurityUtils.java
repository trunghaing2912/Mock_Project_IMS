package fa.training.fjb04.ims.config.security;


import fa.training.fjb04.ims.entity.user.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

public class SecurityUtils {

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return Optional.of(springSecurityUser.getUsername());
        }
        return Optional.empty();
    }

    public static Collection<? extends GrantedAuthority> getCurrentRole() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getAuthorities();
        }
        return null;
    }


}
