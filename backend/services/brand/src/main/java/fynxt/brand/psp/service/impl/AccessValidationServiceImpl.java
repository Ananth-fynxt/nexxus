package fynxt.brand.psp.service.impl;

import fynxt.brand.psp.entity.Psp;
import fynxt.brand.psp.service.AccessValidationService;
import fynxt.brand.psp.service.IpApiService;
import fynxt.brand.request.dto.RequestInputDto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AccessValidationServiceImpl implements AccessValidationService {

	private final IpApiService ipApiService;

	@Override
	public boolean isAccessValid(Psp psp, RequestInputDto request) {
		if (!Boolean.TRUE.equals(psp.getBlockVpnAccess()) && !Boolean.TRUE.equals(psp.getBlockDataCenterAccess())) {
			return true;
		}

		String clientIpAddress = request.getClientIpAddress();
		if (!StringUtils.hasText(clientIpAddress)) {
			return true;
		}

		try {
			boolean isVpnOrDataCenter = false;

			if (Boolean.TRUE.equals(psp.getBlockVpnAccess()) && ipApiService.isVpnOrProxy(clientIpAddress)) {
				isVpnOrDataCenter = true;
			}

			if (Boolean.TRUE.equals(psp.getBlockDataCenterAccess())
					&& ipApiService.isHostingOrDataCenter(clientIpAddress)) {
				isVpnOrDataCenter = true;
			}

			return !isVpnOrDataCenter;
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public List<Psp> filterValidAccess(List<Psp> psps, RequestInputDto request) {
		return psps.stream().filter(psp -> isAccessValid(psp, request)).collect(Collectors.toList());
	}
}
