package fynxt.brand.transaction.enums;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum TimeRange {
	LAST_24_HOURS(1, ChronoUnit.DAYS),
	LAST_2_DAYS(2, ChronoUnit.DAYS),
	LAST_3_DAYS(3, ChronoUnit.DAYS),
	LAST_4_DAYS(4, ChronoUnit.DAYS),
	LAST_7_DAYS(7, ChronoUnit.DAYS),
	LAST_30_DAYS(30, ChronoUnit.DAYS),
	LAST_3_MONTHS(3, ChronoUnit.MONTHS),
	LAST_6_MONTHS(6, ChronoUnit.MONTHS),
	LAST_YEAR(1, ChronoUnit.YEARS);

	private final long amount;
	private final ChronoUnit unit;

	TimeRange(long amount, ChronoUnit unit) {
		this.amount = amount;
		this.unit = unit;
	}

	public LocalDateTime getStartDate() {
		return LocalDateTime.now().minus(amount, unit);
	}

	public LocalDateTime getEndDate() {
		return LocalDateTime.now();
	}

	public DateRange toDateRange() {
		return new DateRange(getStartDate(), getEndDate());
	}

	public record DateRange(LocalDateTime start, LocalDateTime end) {}
}
