/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ComponentScan({"it.mds.sdk.flusso.avn.som.residenti.controller", "it.mds.sdk.flusso.avn.som.residenti",
		"it.mds.sdk.rest.persistence.entity",
		"it.mds.sdk.libreriaregole.validator",
		"it.mds.sdk.flusso.avn.som.residenti.service", "it.mds.sdk.flusso.avn.som.residenti.tracciato",
		"it.mds.sdk.gestoreesiti", "it.mds.sdk.flusso.avn.som.residenti.parser.regole",
		"it.mds.sdk.flusso.avn.som.residenti.parser.regole.conf",
		"it.mds.sdk.connettoremds"})
@OpenAPIDefinition(info=@Info(title = "SDK Ministero Della Salute - Flusso VSX", version = "0.0.1-SNAPSHOT", description = "Flusso Vaccinazioni Somministrate Residenti"))
public class FlussoAvnSomResidenti {

	public static void main(String[] args) {
		SpringApplication.run(FlussoAvnSomResidenti.class, args);
	}

}
