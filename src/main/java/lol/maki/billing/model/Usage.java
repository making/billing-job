package lol.maki.billing.model;

public class Usage {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final Long minutes;
    private final Long dataUsage;

    public Usage(Long id, String firstName, String lastName, Long minutes, Long dataUsage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.minutes = minutes;
        this.dataUsage = dataUsage;
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

    public Long getMinutes() {
        return minutes;
    }

    public Long getDataUsage() {
        return dataUsage;
    }
}

