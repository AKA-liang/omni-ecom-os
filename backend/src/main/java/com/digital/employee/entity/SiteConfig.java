package com.digital.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "site_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "site_name", length = 200)
    private String siteName;
    
    @Column(name = "site_subtitle", length = 500)
    private String siteSubtitle;
    
    @Column(name = "total_employees")
    private Integer totalEmployees;
    
    @Column(name = "online_employees")
    private Integer onlineEmployees;
    
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    
    @Column(name = "theme_color", length = 20)
    private String themeColor;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
