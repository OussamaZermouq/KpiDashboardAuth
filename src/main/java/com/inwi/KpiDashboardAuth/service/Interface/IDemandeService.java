package com.inwi.KpiDashboardAuth.service.Interface;


import com.inwi.KpiDashboardAuth.model.Demande;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IDemandeService {
    public Demande getDemandeById(int demandeId);

    public List<Demande> getAllDemandes();
    public Demande getDemandeByEmail(String email);
    public void createDemande(Demande demande);
    public void updateDemande(Demande demande, int demandeId);
    public void deleteDemande(int demandeId);
}
