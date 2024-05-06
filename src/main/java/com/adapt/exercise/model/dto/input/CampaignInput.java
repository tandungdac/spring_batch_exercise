package com.adapt.exercise.model.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignInput {
    private Long id;

    private String name;

    private String budget;

    private String type;

    private Long accountId;

    @Override
    public String toString() {
        return "CampaignDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", accountId=" + accountId +
                '}';
    }
}
