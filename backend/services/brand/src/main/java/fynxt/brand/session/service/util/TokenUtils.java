package fynxt.brand.session.service.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class TokenUtils {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String HMAC_ALGORITHM = "HmacSHA256";

	public static String generateToken(int bytes) {
		byte[] randomBytes = new byte[bytes];
		SECURE_RANDOM.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

	public static String generateSessionToken() {
		return generateToken(48);
	}

	public static String generateSessionTokenFromData(String data) {
		try {
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] compressedBytes = compress(dataBytes);
			return Base64.getUrlEncoder().withoutPadding().encodeToString(compressedBytes);
		} catch (Exception e) {
			return generateSessionToken();
		}
	}

	public static String decodeSessionTokenData(String token) {
		try {
			byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
			byte[] decompressedBytes = decompress(decodedBytes);
			return new String(decompressedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Failed to decode session token data", e);
		}
	}

	private static byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
			gzos.write(data);
		}
		return baos.toByteArray();
	}

	private static byte[] decompress(byte[] compressedData) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
		try (GZIPInputStream gzis = new GZIPInputStream(bais);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gzis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toByteArray();
		}
	}

	public static String hmacSha256(String key, String data) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
			mac.init(secretKeySpec);
			byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create HMAC hash", e);
		}
	}

	public static boolean constantTimeEquals(String a, String b) {
		if (a == null || b == null) return false;
		byte[] aa = a.getBytes(StandardCharsets.UTF_8);
		byte[] bb = b.getBytes(StandardCharsets.UTF_8);
		return MessageDigest.isEqual(aa, bb);
	}
}
