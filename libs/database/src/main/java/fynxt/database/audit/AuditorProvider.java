package fynxt.database.audit;

import java.util.Optional;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("auditorProvider")
public class AuditorProvider implements AuditorAware<Integer> {

	public static final Integer SYSTEM_USER_ID = 0;

	public static final Integer ANONYMOUS_USER_ID = -1;

	@Override
	@Nonnull
	public Optional<Integer> getCurrentAuditor() {
		return Optional.of(SYSTEM_USER_ID);
	}
}
