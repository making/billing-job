package lol.maki.billing.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Bill(Long id, String firstName, String lastName, Long dataUsage,
				   Long minutes, BigDecimal billAmount) {
	public static Bill fromUsage(Usage usage) {
		final BigDecimal billAmount = calcBillAmount(usage.dataUsage(), usage.minutes());
		return new Bill(usage.id(), usage.firstName(), usage.lastName(), usage.dataUsage(), usage.minutes(), billAmount);
	}

	public static BigDecimal calcBillAmount(Long dataUsage, Long minutes) {
		// dataUsage * 0.001 + usageMinutes * 0.01
		return BigDecimal.valueOf(dataUsage).multiply(new BigDecimal("0.001"))
				.add(BigDecimal.valueOf(minutes).multiply(new BigDecimal("0.01")))
				.setScale(2, RoundingMode.FLOOR);
	}
}
