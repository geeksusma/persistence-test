package es.geeksusma.workshops.domain.core;

import java.util.Objects;

public class Id {

    private final String value;

    private Id(String value) {
        this.value = value;
    }

    public static Id of(String value) {
        return new Id(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Id)) return false;
        Id id = (Id) o;
        return Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
