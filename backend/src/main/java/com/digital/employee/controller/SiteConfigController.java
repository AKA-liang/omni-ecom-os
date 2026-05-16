package com.digital.employee.controller;

import com.digital.employee.entity.SiteConfig;
import com.digital.employee.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/site-config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SiteConfigController {
    
    private final SiteConfigService siteConfigService;
    
    @GetMapping
    public ResponseEntity<SiteConfig> getSiteConfig() {
        return ResponseEntity.ok(siteConfigService.getSiteConfig());
    }
    
    @PostMapping
    public ResponseEntity<SiteConfig> updateSiteConfig(@RequestBody SiteConfig config) {
        return ResponseEntity.ok(siteConfigService.updateSiteConfig(config));
    }
}
