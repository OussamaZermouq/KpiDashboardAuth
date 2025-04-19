package com.inwi.KpiDashboardAuth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer demandeId;
    @Column(nullable = false)
    private String userFirstName;
    @Column(nullable = false)
    private String userLastName;
    @Column(nullable = false)
    private DemandeStatus demandeStatus;
    @Column(nullable = false)
    private String email;
}
