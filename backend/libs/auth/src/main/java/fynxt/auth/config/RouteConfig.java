package fynxt.auth.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
@ConfigurationProperties(prefix = "fynxt.auth.route")
public record RouteConfig(
		String[] publicPaths, String[] adminTokenPaths, String[] secretTokenPaths, String[] openForAllOriginsPaths) {

	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

	public boolean isPublic(String path) {
		return matchesAnyPattern(path, asSet(publicPaths));
	}

	public boolean isAdminTokenPath(String path) {
		return matchesAnyPattern(path, asSet(adminTokenPaths));
	}

	public boolean isSecretTokenPath(String path) {
		return matchesAnyPattern(path, asSet(secretTokenPaths));
	}

	public boolean isJwtRequired(String path) {
		return !isPublic(path);
	}

	public String[] getPublicPaths() {
		return publicPaths != null ? publicPaths : new String[0];
	}

	public String[] getAdminTokenPaths() {
		return adminTokenPaths != null ? adminTokenPaths : new String[0];
	}

	public String[] getSecretTokenPaths() {
		return secretTokenPaths != null ? secretTokenPaths : new String[0];
	}

	public String[] getOpenForAllOriginsPaths() {
		return openForAllOriginsPaths != null ? openForAllOriginsPaths : new String[0];
	}

	private boolean matchesAnyPattern(String path, Set<String> patterns) {
		if (patterns == null || patterns.isEmpty()) {
			return false;
		}
		return patterns.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
	}

	private Set<String> asSet(String[] values) {
		if (values == null || values.length == 0) {
			return Set.of();
		}
		return Arrays.stream(values).collect(Collectors.toSet());
	}
}
