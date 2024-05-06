package com.adapt.exercise.model.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdGroupInput {
    private Long id;

    private String name;

    private String budget;

    private String type;

    private String status;

    private Integer click;

    private Integer view;

    private Long campaignId;

    @Override
    public String toString() {
        return "AdGroupDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", click=" + click +
                ", view=" + view +
                ", campaignId=" + campaignId +
                '}';
    }
}
