package fa.training.fjb04.ims.config.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return SecurityUtils.getCurrentUserLogin();
    }
}
