package fa.training.fjb04.ims.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
@Getter
@Setter
public class Users extends User {

    private String department;
    public Users(String username, String password, String department, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.department = department;
    }

    public Users(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}
