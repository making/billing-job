package lol.maki.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usage {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final Long minutes;
    private final Long dataUsage;

    @JsonCreator
    public Usage(@JsonProperty("id") Long id,
                 @JsonProperty("firstName") String firstName,
                 @JsonProperty("lastName") String lastName,
                 @JsonProperty("minutes") Long minutes,
                 @JsonProperty("dataUsage") Long dataUsage) {
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

