package fynxt.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	private static final String DEFAULT_SECRET_KEY = "default-secret-key-change-in-production";
	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 16;

	private final ObjectMapper objectMapper;
	private final String secretKey;

	public CryptoUtil(CryptoProperties cryptoProperties) {
		String configuredKey = cryptoProperties.secretKey();
		this.secretKey = normalizeSecretKey(
				configuredKey == null || configuredKey.isBlank() ? DEFAULT_SECRET_KEY : configuredKey);
		this.objectMapper = new ObjectMapper();
	}

	public Map<String, String> encryptCredential(Map<String, String> credential) throws Exception {
		if (credential == null || credential.isEmpty()) {
			return credential;
		}

		Map<String, String> encrypted = new HashMap<>();
		for (Map.Entry<String, String> entry : credential.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (value != null && !value.trim().isEmpty()) {
				String encryptedValue = encrypt(value);
				encrypted.put(key, encryptedValue);
			} else {
				encrypted.put(key, value);
			}
		}

		return encrypted;
	}

	public Map<String, String> decryptCredential(Map<String, String> encryptedCredential) throws Exception {
		if (encryptedCredential == null || encryptedCredential.isEmpty()) {
			return encryptedCredential;
		}

		Map<String, String> decrypted = new HashMap<>();
		for (Map.Entry<String, String> entry : encryptedCredential.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (value != null && !value.trim().isEmpty() && isEncrypted(value)) {
				String decryptedValue = decrypt(value);
				decrypted.put(key, decryptedValue);
			} else {
				decrypted.put(key, value);
			}
		}

		return decrypted;
	}

	public String encrypt(String plaintext) throws Exception {
		if (plaintext == null || plaintext.trim().isEmpty()) {
			return plaintext;
		}

		try {
			byte[] keyBytes = Base64.getDecoder().decode(secretKey);
			byte[] iv = new byte[GCM_IV_LENGTH];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

			byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new Exception("Encryption failed: " + e.getMessage());
		}
	}

	public String decrypt(String encryptedText) throws Exception {
		if (encryptedText == null || encryptedText.trim().isEmpty()) {
			return encryptedText;
		}

		try {
			byte[] keyBytes = Base64.getDecoder().decode(secretKey);
			byte[] combined = Base64.getDecoder().decode(encryptedText);

			byte[] iv = new byte[GCM_IV_LENGTH];
			byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
			System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
			System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted);
		} catch (Exception e) {
			throw new Exception("Decryption failed: " + e.getMessage());
		}
	}

	public JsonNode encryptCredentialJsonNode(JsonNode credentialJsonNode) throws Exception {
		if (credentialJsonNode == null || credentialJsonNode.isNull() || credentialJsonNode.isEmpty()) {
			return credentialJsonNode;
		}

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> credentialMap = objectMapper.convertValue(credentialJsonNode, Map.class);

			Map<String, String> encryptedMap = encryptCredential(credentialMap);

			return objectMapper.valueToTree(encryptedMap);
		} catch (Exception e) {
			throw new Exception("Encryption failed for JsonNode: " + e.getMessage());
		}
	}

	public JsonNode decryptCredentialJsonNode(JsonNode encryptedCredentialJsonNode) throws Exception {
		if (encryptedCredentialJsonNode == null
				|| encryptedCredentialJsonNode.isNull()
				|| encryptedCredentialJsonNode.isEmpty()) {
			return encryptedCredentialJsonNode;
		}

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> encryptedMap = objectMapper.convertValue(encryptedCredentialJsonNode, Map.class);

			Map<String, String> decryptedMap = decryptCredential(encryptedMap);

			return objectMapper.valueToTree(decryptedMap);
		} catch (Exception e) {
			throw new Exception("Decryption failed for JsonNode: " + e.getMessage());
		}
	}

	public boolean isCredentialJsonNodeEncrypted(JsonNode credentialJsonNode) {
		if (credentialJsonNode == null || credentialJsonNode.isNull() || credentialJsonNode.isEmpty()) {
			return false;
		}

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> credentialMap = objectMapper.convertValue(credentialJsonNode, Map.class);

			for (Map.Entry<String, String> entry : credentialMap.entrySet()) {
				String value = entry.getValue();
				if (value != null && !value.trim().isEmpty() && isEncrypted(value)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> parseCredentialJson(String credentialJson) throws Exception {
		if (credentialJson == null || credentialJson.trim().isEmpty()) {
			return new HashMap<>();
		}

		try {
			return objectMapper.readValue(credentialJson, Map.class);
		} catch (Exception e) {
			throw new Exception("Invalid credential JSON format: " + e.getMessage());
		}
	}

	public String credentialMapToJson(Map<String, String> credentialMap) throws Exception {
		if (credentialMap == null || credentialMap.isEmpty()) {
			return "{}";
		}

		try {
			return objectMapper.writeValueAsString(credentialMap);
		} catch (Exception e) {
			throw new Exception("Error serializing credentials: " + e.getMessage());
		}
	}

	private String normalizeSecretKey(String secretKey) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			return "default-secret-key-change-in-production-32-chars";
		}
	}

	private boolean isEncrypted(String value) {
		try {
			byte[] decoded = Base64.getDecoder().decode(value);
			return decoded.length >= GCM_IV_LENGTH + 1;
		} catch (Exception e) {
			return false;
		}
	}
}
