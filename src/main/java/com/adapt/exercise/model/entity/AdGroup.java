package com.adapt.exercise.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ad_groups")
public class AdGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is not empty")
    private String name;

    @NotNull(message = "Budget is not empty")
    private Float budget;

    @NotBlank(message = "Type is not empty")
    private String type;

    private String status;

    private Integer click;

    private Integer view;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean isValid;

    @Override
    public String toString() {
        return "AdGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", click=" + click +
                ", view=" + view +
                ", campaign=" + campaign +
                '}';
    }
}
