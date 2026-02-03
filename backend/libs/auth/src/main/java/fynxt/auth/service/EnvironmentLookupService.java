package fynxt.auth.service;

import java.util.Map;
import java.util.UUID;

public interface EnvironmentLookupService {

	Map<String, Object> findBySecret(UUID secret);
}
