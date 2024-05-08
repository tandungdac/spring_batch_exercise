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
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is not empty")
    private String name;

    @NotNull(message = "Budget is not empty")
    private Float budget;

    @NotBlank(message = "Type is not empty")
    private String type;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private Set<AdGroup> adGroups;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @Column(columnDefinition = "boolean default true", name = "is_valid")
    private Boolean isValid;

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", account=" + account +
                '}';
    }
}
