package fynxt.brand.psp.service.impl;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.IpValidationService;
import fynxt.brand.request.dto.RequestInputDto;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IpValidationServiceImpl implements IpValidationService {

	@Override
	public boolean isIpValid(Psp psp, RequestInputDto request) {
		String[] configuredIpAddresses = psp.getIpAddress();

		if (configuredIpAddresses == null || configuredIpAddresses.length == 0) {
			return true;
		}

		String clientIpAddress = request.getClientIpAddress();
		if (!StringUtils.hasText(clientIpAddress)) {
			return true;
		}

		try {
			InetAddress clientIp = InetAddress.getByName(clientIpAddress);
			boolean isExcluded = Arrays.stream(configuredIpAddresses)
					.anyMatch(configuredIp -> isIpExcluded(clientIp, configuredIp, psp.getId()));

			if (isExcluded) {
				return false;
			}

			return true;
		} catch (UnknownHostException e) {
			return true;
		}
	}

	@Override
	public List<Psp> filterValidIps(List<Psp> psps, RequestInputDto request) {
		return psps.stream().filter(psp -> isIpValid(psp, request)).collect(Collectors.toList());
	}

	private boolean isIpExcluded(InetAddress clientIp, String configuredIp, UUID pspId) {
		try {
			if (configuredIp.contains("/")) {
				return isIpInCidrRange(clientIp, configuredIp);
			} else {
				InetAddress configuredIpAddress = InetAddress.getByName(configuredIp);
				return clientIp.equals(configuredIpAddress);
			}
		} catch (UnknownHostException e) {
			return false;
		}
	}

	private boolean isIpInCidrRange(InetAddress clientIp, String cidrNotation) {
		try {
			String[] parts = cidrNotation.split("/");
			if (parts.length != 2) {
				return false;
			}

			InetAddress networkAddress = InetAddress.getByName(parts[0]);
			int prefixLength = Integer.parseInt(parts[1]);

			byte[] networkBytes = networkAddress.getAddress();
			byte[] clientBytes = clientIp.getAddress();

			if (networkBytes.length != clientBytes.length) {
				return false;
			}

			int bytesToCheck = prefixLength / 8;
			int bitsToCheck = prefixLength % 8;

			for (int i = 0; i < bytesToCheck; i++) {
				if (networkBytes[i] != clientBytes[i]) {
					return false;
				}
			}

			if (bitsToCheck > 0 && bytesToCheck < networkBytes.length) {
				int mask = 0xFF << (8 - bitsToCheck);
				if ((networkBytes[bytesToCheck] & mask) != (clientBytes[bytesToCheck] & mask)) {
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
