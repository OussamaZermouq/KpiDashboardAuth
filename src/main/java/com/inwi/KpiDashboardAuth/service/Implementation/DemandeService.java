package com.inwi.KpiDashboardAuth.service.Implementation;

import com.inwi.KpiDashboardAuth.model.Demande;
import com.inwi.KpiDashboardAuth.model.DemandeStatus;
import com.inwi.KpiDashboardAuth.model.User;
import com.inwi.KpiDashboardAuth.repository.DemandeRepository;
import com.inwi.KpiDashboardAuth.repository.UserRepository;
import com.inwi.KpiDashboardAuth.service.Interface.IDemandeService;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.MethodInvocationException.ERROR_CODE;

@Service
public class DemandeService implements IDemandeService {

    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Demande getDemandeById(int demandeId) {
        Optional<Demande> demandeOptional = demandeRepository.findById(demandeId);
        if (demandeOptional.isPresent()) {
            return demandeOptional.get();
        }
        return null;
    }

    @Override
    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }

    @Override
    public Demande getDemandeByEmail(String email) {
        Optional<Demande> demandeOptional = demandeRepository.findDemandeByEmail(email);
        if (demandeOptional.isPresent()) {
            return demandeOptional.get();
        }
        return null;
    }

    @Override
    public void createDemande(Demande demande) {
        if (!demande.getEmail().isEmpty()) {
            demandeRepository.save(new Demande(null,
                    demande.getUserFirstName(),
                    demande.getUserLastName(),
                    DemandeStatus.NOTAPPROVED,
                    demande.getEmail()));
        }
    }


    public String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }
            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(12, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    @Override
    public void updateDemande(Demande demande, int demandeId) {
        DemandeStatus searchResult = null;
        for (DemandeStatus demandeStatus : DemandeStatus.values()) {
            if (demandeStatus.name().equalsIgnoreCase(demande.getDemandeStatus().toString())) {
                searchResult = demandeStatus;
                break;
            }
        }
        if (!demande.getEmail().isEmpty() && demande != null) {
            Optional<Demande> demandeOptional = demandeRepository.findById(demandeId);
            if (demandeOptional.isPresent() && searchResult != null) {
                demandeOptional.get().setDemandeStatus(DemandeStatus.APPROVED);
                userRepository.save(new User(
                        null,
                        demandeOptional.get().getUserFirstName(),
                        demandeOptional.get().getUserLastName(),
                        demandeOptional.get().getEmail(),
                        generatePassword(),
                        new Date(),
                        new Date()
                ));
            }
        }
    }

    @Override
    public void deleteDemande(int demandeId) {
        Optional<Demande> demandeOptional = demandeRepository.findById(demandeId);
        if (demandeOptional.isPresent()) {
            demandeRepository.delete(demandeOptional.get());
        }
    }
}
