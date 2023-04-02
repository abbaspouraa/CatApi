package org.abbaspour.catapi.model;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "Cat")
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @NotNull
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^\\w[\\w\\s]+")
    private String name;

    @NotNull
    private Date birthDate;

    @Size(min = 1, max = 30)
    @Pattern(regexp = "^\\w[\\w\\s]+")
    private String favoriteFood;

    @Size(max = 30)
    @Pattern(regexp = "[\\w\\s]*")
    private String owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFavoriteFood() {
        return favoriteFood;
    }

    public void setFavoriteFood(String favoriteFood) {
        this.favoriteFood = favoriteFood;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
