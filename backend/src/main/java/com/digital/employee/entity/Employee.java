package com.digital.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String role;
    
    @Column(length = 100)
    private String position;
    
    @Column(length = 500)
    private String avatar;
    
    @Column(length = 50)
    private String status;
    
    @Column(length = 500)
    private String currentTask;
    
    @Column(length = 500)
    private String recentOutput;
    
    @Column(name = "is_on_duty")
    private Boolean isOnDuty;
    
    @Column(length = 50)
    private String schedule;
    
    @Column(length = 50)
    private String category;
    
    @Column(length = 1000)
    private String skills;
    
    @Column(name = "tasks_completed")
    private Integer tasksCompleted;
    
    @Column(length = 20)
    private String accuracy;
    
    @Column(name = "avg_response_time", length = 20)
    private String avgResponseTime;
    
    @Column(columnDefinition = "TEXT")
    private String prompt;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
