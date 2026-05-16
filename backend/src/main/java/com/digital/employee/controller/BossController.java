package com.digital.employee.controller;

import com.digital.employee.entity.Boss;
import com.digital.employee.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boss")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BossController {
    
    private final BossService bossService;
    
    @GetMapping
    public ResponseEntity<Boss> getBoss() {
        return ResponseEntity.ok(bossService.getBoss());
    }
    
    @PostMapping
    public ResponseEntity<Boss> updateBoss(@RequestBody Boss boss) {
        return ResponseEntity.ok(bossService.updateBoss(boss));
    }
    
    @PostMapping("/avatar")
    public ResponseEntity<Boss> updateAvatar(@RequestBody Boss boss) {
        return ResponseEntity.ok(bossService.updateBoss(boss));
    }
    
    @PutMapping
    public ResponseEntity<Boss> updateBossInfo(@RequestBody Boss boss) {
        return ResponseEntity.ok(bossService.updateBossInfo(boss));
    }
}
