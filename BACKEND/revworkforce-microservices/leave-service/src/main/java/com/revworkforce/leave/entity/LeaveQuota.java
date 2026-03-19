package com.revworkforce.leave.entity;

import com.revworkforce.leave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_quotas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private LeaveType leaveType;

    private int defaultDays;
    private int year;
}
