package lol.maki.billing.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bill {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final Long dataUsage;
    private final Long minutes;
    private final BigDecimal billAmount;

    public static Bill fromUsage(Usage usage) {
        final BigDecimal billAmount = calcBillAmount(usage.getDataUsage(), usage.getMinutes());
        return new Bill(usage.getId(), usage.getFirstName(), usage.getLastName(), usage.getDataUsage(), usage.getMinutes(), billAmount);
    }

    public Bill(Long id, String firstName, String lastName, Long dataUsage, Long minutes, BigDecimal billAmount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dataUsage = dataUsage;
        this.minutes = minutes;
        this.billAmount = billAmount;
    }

    public static BigDecimal calcBillAmount(Long dataUsage, Long minutes) {
        // dataUsage * 0.001 + usageMinutes * 0.01
        return BigDecimal.valueOf(dataUsage).multiply(new BigDecimal("0.001"))
                .add(BigDecimal.valueOf(minutes).multiply(new BigDecimal("0.01")))
                .setScale(2, RoundingMode.FLOOR);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDataUsage() {
        return dataUsage;
    }

    public Long getMinutes() {
        return minutes;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }
}
