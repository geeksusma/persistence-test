package es.geeksusma.workshops.domain.core;

public class Id {

    private final String value;

    private Id(String value) {
        this.value = value;
    }

    public static Id of(String value) {
        return new Id(value);
    }

}
