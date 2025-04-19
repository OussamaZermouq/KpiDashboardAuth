package com.inwi.KpiDashboardAuth;

import com.inwi.KpiDashboardAuth.model.Demande;
import com.inwi.KpiDashboardAuth.service.Implementation.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@SpringBootApplication
public class KpiDashboardAuthApplication implements CommandLineRunner {

	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	DemandeService demandeService;
	public static void main(String[] args) {
		SpringApplication.run(KpiDashboardAuthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		UserDetails userDetails = userDetailsService.loadUserByUsername("amine@example.com");
		List<Demande> demandeList = demandeService.getAllDemandes();
		System.out.println(demandeList);
	}
}

