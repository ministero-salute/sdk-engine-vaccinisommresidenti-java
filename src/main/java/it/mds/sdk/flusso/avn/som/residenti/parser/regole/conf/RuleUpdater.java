/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti.parser.regole.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class RuleUpdater {

    @Autowired
    ConfigurazioneFlussoAvnSomResidenti configurazioneFlussoAvnSomResidenti;

    @EventListener(ApplicationReadyEvent.class)
    public void updateRules() throws IOException {
        ClassPathResource internalRules = new ClassPathResource("/regole-avn-som-res.xml");
        Path externalRules = Paths.get(configurazioneFlussoAvnSomResidenti.getRules().getPercorso());
        if (Files.exists(externalRules.getParent())) {
            Files.copy(internalRules.getInputStream(), externalRules, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}