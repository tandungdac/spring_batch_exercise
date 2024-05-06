package com.adapt.exercise.job.jobExport.process;

import com.adapt.exercise.model.dto.output.CampaignOutput;
import com.adapt.exercise.model.entity.AdGroup;
import com.adapt.exercise.model.entity.Campaign;
import com.adapt.exercise.repository.IAdGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class CampaignExportProcessor implements ItemProcessor<Campaign, CampaignOutput> {

    public static final Logger log = LoggerFactory.getLogger(CampaignExportProcessor.class);

    @Autowired
    private IAdGroupRepository adGroupRepository;

    @Override
    public CampaignOutput process(Campaign item) throws Exception {
        CampaignOutput campaignExport = new CampaignOutput();
        campaignExport.setId(item.getId());
        campaignExport.setName(item.getName());
        campaignExport.setBudget(item.getBudget());
        campaignExport.setType(item.getType());

        float adGroupBudget = 0;
        int adGroupClick = 0;
        int adGroupView = 0;

        for (AdGroup adGroup : item.getAdGroups()) {
            adGroupBudget += adGroup.getBudget();
            adGroupClick += adGroup.getClick();
            adGroupView += adGroup.getView();
        }

        campaignExport.setAdGroupBudget(adGroupBudget);
        campaignExport.setAdGroupClick(adGroupClick);
        campaignExport.setAdGroupView(adGroupView);

        return campaignExport;
    }
}
