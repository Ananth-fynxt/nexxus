package fynxt.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	private final String secretKey;

	public CryptoUtil(CryptoProperties cryptoProperties) {
		String configuredKey = cryptoProperties.secretKey();
		this.secretKey = configuredKey == null || configuredKey.isBlank() ? "defaultSecretKey123456" : configuredKey;
	}

	public String encrypt(String data) throws Exception {
		SecretKeySpec keySpec = generateKey();
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public String decrypt(String encryptedData) throws Exception {
		SecretKeySpec keySpec = generateKey();
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decoded = Base64.getDecoder().decode(encryptedData);
		byte[] decrypted = cipher.doFinal(decoded);
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	private SecretKeySpec generateKey() throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
		key = sha.digest(key);
		byte[] key16 = new byte[16];
		System.arraycopy(key, 0, key16, 0, 16);
		return new SecretKeySpec(key16, "AES");
	}
}
