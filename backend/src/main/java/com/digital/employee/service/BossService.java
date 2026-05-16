package com.digital.employee.service;

import com.digital.employee.entity.Boss;
import com.digital.employee.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BossService {
    
    private final BossRepository bossRepository;
    
    public Boss getBoss() {
        List<Boss> bosses = bossRepository.findAll();
        if (bosses.isEmpty()) {
            Boss defaultBoss = new Boss();
            defaultBoss.setName("张总");
            defaultBoss.setPosition("数字员工管理系统负责人");
            defaultBoss.setEmail("boss@company.com");
            defaultBoss.setPhone("138-0013-8000");
            defaultBoss.setDepartment("AI运营中心");
            defaultBoss.setBio("致力于打造最优秀的AI数字员工团队，推动企业智能化转型");
            defaultBoss.setAvatar("");
            defaultBoss.setJoinDate("2024-01-15");
            defaultBoss.setTeamSize(13);
            defaultBoss.setProjectsCompleted(48);
            defaultBoss.setEfficiency("98.5%");
            return bossRepository.save(defaultBoss);
        }
        return bosses.get(0);
    }
    
    @Transactional
    public Boss updateBoss(Boss boss) {
        Boss existingBoss = getBoss();
        
        if (boss.getName() != null) {
            existingBoss.setName(boss.getName());
        }
        if (boss.getPosition() != null) {
            existingBoss.setPosition(boss.getPosition());
        }
        if (boss.getEmail() != null) {
            existingBoss.setEmail(boss.getEmail());
        }
        if (boss.getPhone() != null) {
            existingBoss.setPhone(boss.getPhone());
        }
        if (boss.getDepartment() != null) {
            existingBoss.setDepartment(boss.getDepartment());
        }
        if (boss.getBio() != null) {
            existingBoss.setBio(boss.getBio());
        }
        if (boss.getAvatar() != null) {
            existingBoss.setAvatar(boss.getAvatar());
        }
        
        return bossRepository.save(existingBoss);
    }
    
    @Transactional
    public Boss updateAvatar(String avatarUrl) {
        Boss boss = getBoss();
        boss.setAvatar(avatarUrl);
        return bossRepository.save(boss);
    }
    
    @Transactional
    public Boss updateBossInfo(Boss boss) {
        return updateBoss(boss);
    }
}
