package com.adapt.exercise.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignOutput {
    private Long id;

    private String name;

    private Float budget;

    private String type;

    private Float adGroupBudget;

    private Integer adGroupClick;

    private Integer adGroupView;

    @Override
    public String toString() {
        return "CampaignItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", adGroupBudget=" + adGroupBudget +
                ", adGroupClick=" + adGroupClick +
                ", adGroupView=" + adGroupView +
                '}';
    }
}
