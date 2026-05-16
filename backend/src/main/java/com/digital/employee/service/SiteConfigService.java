package com.digital.employee.service;

import com.digital.employee.entity.SiteConfig;
import com.digital.employee.repository.SiteConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteConfigService {
    
    private final SiteConfigRepository siteConfigRepository;
    
    public SiteConfig getSiteConfig() {
        List<SiteConfig> configs = siteConfigRepository.findAll();
        if (configs.isEmpty()) {
            SiteConfig defaultConfig = new SiteConfig();
            defaultConfig.setSiteName("智能数字员工");
            defaultConfig.setSiteSubtitle("Digital Employee Management");
            defaultConfig.setTotalEmployees(13);
            defaultConfig.setOnlineEmployees(13);
            return siteConfigRepository.save(defaultConfig);
        }
        return configs.get(0);
    }
    
    @Transactional
    public SiteConfig updateSiteConfig(SiteConfig config) {
        SiteConfig existing = getSiteConfig();
        
        if (config.getSiteName() != null) {
            existing.setSiteName(config.getSiteName());
        }
        if (config.getSiteSubtitle() != null) {
            existing.setSiteSubtitle(config.getSiteSubtitle());
        }
        if (config.getTotalEmployees() != null) {
            existing.setTotalEmployees(config.getTotalEmployees());
        }
        if (config.getOnlineEmployees() != null) {
            existing.setOnlineEmployees(config.getOnlineEmployees());
        }
        if (config.getLogoUrl() != null) {
            existing.setLogoUrl(config.getLogoUrl());
        }
        if (config.getThemeColor() != null) {
            existing.setThemeColor(config.getThemeColor());
        }
        
        return siteConfigRepository.save(existing);
    }
}
