package fynxt.brand.request.service;

import fynxt.brand.request.dto.RequestInputDto;
import fynxt.brand.request.dto.RequestOutputDto;

import java.util.UUID;

public interface RequestService {

	RequestOutputDto fetchPsp(RequestInputDto requestInputDto);

	CustomerInfo getCustomerInfoByRequestId(UUID requestId);

	record CustomerInfo(String customerId, String customerTag, String customerAccountType) {}
}
