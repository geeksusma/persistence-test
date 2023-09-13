package es.geeksusma.workshops.domain.core;

public class Name {

    private final String name;
    private final String lastName;

    private Name(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    public static Name of(String name, String lastName) {
        return new Name(name, lastName);
    }

    public String getFullName() {
        return name + " " + lastName;
    }
}
