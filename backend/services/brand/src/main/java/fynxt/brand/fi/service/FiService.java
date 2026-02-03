package fynxt.brand.fi.service;

import fynxt.brand.fi.dto.FiDto;

public interface FiService {

	FiDto create(FiDto fiDto);

	FiDto findByUserId(Integer userId);
}
