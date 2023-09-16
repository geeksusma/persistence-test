package es.geeksusma.workshops.domain.core;

import java.util.Objects;

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

    public String name() {
        return name;
    }

    public String lastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name) &&
                Objects.equals(lastName, name1.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastName);
    }
}
