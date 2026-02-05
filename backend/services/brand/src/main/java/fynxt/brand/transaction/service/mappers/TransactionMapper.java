package fynxt.brand.transaction.service.mappers;

import fynxt.brand.transaction.context.TransactionExecutionContext;
import fynxt.brand.transaction.dto.TransactionDto;
import fynxt.brand.transaction.entity.EmbeddableTransactionId;
import fynxt.brand.transaction.entity.Transaction;
import fynxt.brand.transaction.enums.TransactionStatus;
import fynxt.mapper.config.MapperCoreConfig;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperCoreConfig.class)
public interface TransactionMapper {

	@Mapping(target = "txnId", source = "id.txnId")
	@Mapping(target = "version", source = "id.version")
	@Mapping(target = "executePayload", expression = "java(convertJsonNodeToMap(transaction.getExecutePayload()))")
	TransactionDto toDto(Transaction transaction);

	@Mapping(target = "txnId", expression = "java(context.getTransaction().getId().getTxnId())")
	@Mapping(target = "version", expression = "java(context.getTransaction().getId().getVersion())")
	@Mapping(target = "pspId", source = "transaction.pspId")
	@Mapping(
			target = "executePayload",
			expression = "java(convertJsonNodeToMap(context.getTransaction().getExecutePayload()))")
	@Mapping(target = "customData", expression = "java(context.getCustomData())")
	TransactionDto toDto(TransactionExecutionContext context);

	@Mapping(
			target = "id",
			expression = "java(createEmbeddableTransactionId(transactionDto.getTxnId(), transactionDto.getVersion()))")
	@Mapping(target = "executePayload", expression = "java(convertMapToJsonNode(transactionDto.getExecutePayload()))")
	Transaction toEntity(TransactionDto transactionDto);

	void updateEntityFromDto(TransactionDto transactionDto, @MappingTarget Transaction transaction);

	@Mapping(target = "id", expression = "java(createIncrementedTransactionId(currentTransaction.getId()))")
	@Mapping(target = "status", source = "destinationStatus")
	Transaction createNewVersionedRecord(Transaction currentTransaction, TransactionStatus destinationStatus);

	default EmbeddableTransactionId createEmbeddableTransactionId(String txnId, Integer version) {
		if (txnId == null || txnId.isBlank()) {
			return new EmbeddableTransactionId(null, 0);
		}

		int safeVersion = (version == null || version < 1) ? 1 : version;
		return new EmbeddableTransactionId(txnId, safeVersion);
	}

	default EmbeddableTransactionId createIncrementedTransactionId(EmbeddableTransactionId currentId) {
		return new EmbeddableTransactionId(currentId.getTxnId(), currentId.getVersion() + 1);
	}

	@SuppressWarnings("unchecked")
	default Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
		if (jsonNode == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(jsonNode, Map.class);
	}

	default JsonNode convertMapToJsonNode(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(map);
	}

	default UUID convertStringToUuid(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return UUID.fromString(value);
	}

	default String convertUuidToString(UUID value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}
}
