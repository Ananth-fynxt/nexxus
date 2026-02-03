package fynxt.auth.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Data
@Configuration
@ConfigurationProperties(prefix = "fynxt.auth.route")
public class RouteConfig {

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private String[] publicPaths = new String[0];

	private String[] adminTokenPaths = new String[0];

	private String[] secretTokenPaths = new String[0];

	private String[] openForAllOriginsPaths = new String[0];

	private Set<String> publicPathsSet;
	private Set<String> adminTokenPathsSet;
	private Set<String> secretTokenPathsSet;

	public boolean isPublic(String path) {
		if (publicPathsSet == null) {
			publicPathsSet = new HashSet<>(Arrays.asList(publicPaths));
		}
		return matchesAnyPattern(path, publicPathsSet);
	}

	public boolean isAdminTokenPath(String path) {
		if (adminTokenPathsSet == null) {
			adminTokenPathsSet = new HashSet<>(Arrays.asList(adminTokenPaths));
		}
		return matchesAnyPattern(path, adminTokenPathsSet);
	}

	public boolean isSecretTokenPath(String path) {
		if (secretTokenPathsSet == null) {
			secretTokenPathsSet = new HashSet<>(Arrays.asList(secretTokenPaths));
		}
		return matchesAnyPattern(path, secretTokenPathsSet);
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
		return patterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
	}
}
