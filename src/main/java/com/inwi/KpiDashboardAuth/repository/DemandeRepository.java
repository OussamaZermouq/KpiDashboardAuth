package com.inwi.KpiDashboardAuth.repository;

import com.inwi.KpiDashboardAuth.model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    Optional<Demande> findDemandeByEmail(String email);

}
