package fynxt.brand.psp.dto;

import fynxt.brand.fee.dto.FeeDto;
import fynxt.brand.riskrule.dto.RiskRuleDto;
import fynxt.brand.transactionlimit.dto.TransactionLimitDto;
import fynxt.common.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspSummaryDto {

	private UUID id;
	private String name;
	private String description;
	private String logo;
	private Status status;
	private UUID brandId;
	private UUID environmentId;
	private String flowTargetId;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	private Integer createdBy;
	private Integer updatedBy;
	private List<RiskRuleDto> riskRules;
	private List<FeeDto> feeRules;
	private List<TransactionLimitDto> transactionLimits;
}
