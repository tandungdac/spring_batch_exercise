package com.adapt.exercise.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is not empty")
    private String name;

    private Boolean isExpired;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Campaign> campaigns;

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean isValid;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isExpired=" + isExpired +
                '}';
    }
}
