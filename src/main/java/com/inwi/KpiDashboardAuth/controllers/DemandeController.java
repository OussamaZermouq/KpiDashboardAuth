package com.inwi.KpiDashboardAuth.controllers;


import com.inwi.KpiDashboardAuth.model.Demande;
import com.inwi.KpiDashboardAuth.responses.Response;
import com.inwi.KpiDashboardAuth.service.Implementation.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demande")
public class DemandeController {
    @Autowired
    private DemandeService demandeService;

    @GetMapping("/")
    private ResponseEntity<Response<List<Demande>>> getAllDemande(){
        return ResponseEntity.ok().body(new Response<>(200,demandeService.getAllDemandes()));
    }

    @GetMapping("/{id}")
    private ResponseEntity<Response<Demande>> getDemandeById(@RequestParam("id") int demandeId){
        Demande demande = demandeService.getDemandeById(demandeId);
        if (demande!=null){
            return ResponseEntity.ok().body(new Response<>(200,demande));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    private ResponseEntity<Response<String>> createDemande(@RequestBody Demande demande){
        demandeService.createDemande(demande);
        return ResponseEntity.ok().body(new Response<>(200,"Demande has been successfully created"));

    }

    @PutMapping("/{id}")
    private ResponseEntity<Response<String>> updateDemande(@RequestParam("id") int demandeId, @RequestBody Demande demande){
        demandeService.updateDemande(demande, demandeId);
        return ResponseEntity.ok().body(new Response<>(200,"Demande has been successfully updated"));
    }
    @DeleteMapping("/{id}")
    private ResponseEntity<Response<String>> deleteCommande(@RequestParam("id") int demandeId){
        demandeService.deleteDemande(demandeId);
        return ResponseEntity.ok().body(new Response<>(200,"Demande has been successfully deleted"));

    }
}
