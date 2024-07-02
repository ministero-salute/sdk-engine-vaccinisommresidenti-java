/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti.controller;

import it.mds.sdk.connettore.anagrafiche.tabella.EsitoBR3060;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.ParserRegoleImpl;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.RecordDtoAvnSomResidenti;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.conf.ConfigurazioneFlussoAvnSomResidenti;
import it.mds.sdk.flusso.avn.som.residenti.service.FlussoAvnSomResidentiService;
import it.mds.sdk.gestoreesiti.GestoreRunLog;
import it.mds.sdk.gestoreesiti.Progressivo;
import it.mds.sdk.gestoreesiti.modelli.*;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.dtos.CampiInputBean;
import it.mds.sdk.libreriaregole.dtos.RecordDtoGenerico;
import it.mds.sdk.libreriaregole.parser.ParserRegole;
import it.mds.sdk.libreriaregole.parser.ParserTracciato;
import it.mds.sdk.libreriaregole.regole.beans.RegoleFlusso;
import it.mds.sdk.rest.api.controller.ValidazioneController;
import it.mds.sdk.rest.persistence.entity.FlussoRequest;
import it.mds.sdk.rest.persistence.entity.RecordRequest;
import it.mds.sdk.rest.persistence.entity.RisultatoInizioValidazione;
import it.mds.sdk.rest.persistence.entity.RisultatoValidazione;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@Configuration
@EnableAsync
@Slf4j
public class FlussoAvnSomResidentiControllerRest implements ValidazioneController<RecordDtoAvnSomResidenti> {

    private static final String FILE_CSV = "CSV";


    private final ParserRegoleImpl parserRegole;
    private final ParserTracciato parserTracciato;
    private final FlussoAvnSomResidentiService flussoAvnSomResidentiService;
    private final ConfigurazioneFlussoAvnSomResidenti conf;

    private final MultiValueMap<String, String> headers;


    @Autowired
    public FlussoAvnSomResidentiControllerRest(@Qualifier("parserRegoleAvnSomResidenti") final ParserRegoleImpl parserRegole,
                                               @Qualifier("parserTracciatoAvnSomResidenti") final ParserTracciato parserTracciato,
                                               @Qualifier("flussoAvnSomResidentiService") final FlussoAvnSomResidentiService flussoAvnSomResidentiService,
                                               @Qualifier("configurazioneFlussoAvnSomResidenti") ConfigurazioneFlussoAvnSomResidenti conf) {
        this.parserRegole = parserRegole;
        this.parserTracciato = parserTracciato;
        this.flussoAvnSomResidentiService = flussoAvnSomResidentiService;
        this.conf = conf;

        headers = new HttpHeaders();
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("Content-Security-Policy", "default-src 'self'");

    }

    @Override
    @PostMapping(path = "v1/flusso/vaccinisommresidenti")
    public ResponseEntity<RisultatoInizioValidazione> validaTracciato(@RequestBody FlussoRequest flusso, String nomeFlussoController) {

        log.debug("{}.validaTracciato - BEGIN", this.getClass().getName());
        if (flusso.getAnnoRiferimento() == null || flusso.getAnnoRiferimento().isEmpty()
                || flusso.getPeriodoRiferimento() == null || flusso.getPeriodoRiferimento().isEmpty()
        ) {
            return new ResponseEntity<RisultatoInizioValidazione>(new RisultatoInizioValidazione(false, "Campi obbligatori Mancanti", "", flusso.getIdClient()), headers, HttpStatus.BAD_REQUEST);
        }

        log.debug("{}.validaTracciato - annoRiferimento[{}] - periodoRiferimento[{}]", this.getClass().getName(), flusso.getPeriodoRiferimento(), flusso.getAnnoRiferimento());

        String filename = FilenameUtils.normalize(flusso.getNomeFile());
        File tracciato = getFileFromPath(conf.getFlusso().getPercorso() + filename);
        if (!tracciato.exists()) {
            log.debug("File tracciato {} non trovato ", conf.getFlusso().getPercorso() + filename);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File tracciato non trovato");
        }

        RegoleFlusso regoleFlusso = getRegoleFlusso();

        GestoreFile gestoreFile = GestoreFileFactory.getGestoreFile(FILE_CSV);
        GestoreRunLog gestoreRunLog = getGestoreRunLog(gestoreFile, Progressivo.creaProgressivo(Progressivo.Fonte.FILE));
        String nomeFlusso = conf.getNomeFLusso().getNomeFlusso();
        InfoRun infoRun = gestoreRunLog.creaRunLog(flusso.getIdClient(), flusso.getModalitaOperativa(), 0, nomeFlusso);
        infoRun.setTipoElaborazione(TipoElaborazione.F);
        infoRun.setTimestampCreazione(new Timestamp(System.currentTimeMillis()));
        infoRun.setVersion(getClass().getPackage().getImplementationVersion());
        infoRun.setAnnoRiferimento(flusso.getAnnoRiferimento());
        infoRun.setPeriodoRiferimento(flusso.getPeriodoRiferimento());
        infoRun.setCodiceRegione(flusso.getCodiceRegione());
        infoRun.setFileAssociatiRun(flusso.getNomeFile());
        gestoreRunLog.updateRun(infoRun);
        infoRun = gestoreRunLog.cambiaStatoRun(infoRun.getIdRun(), StatoRun.IN_ELABORAZIONE);
        int dimensioneBlocco = Integer.parseInt(conf.getDimensioneBlocco().getDimensioneBlocco());

        flussoAvnSomResidentiService.lanciaValidazioniPerFlusso(dimensioneBlocco, flusso, infoRun, gestoreRunLog,regoleFlusso);

        log.debug("Fine validaTracciato per idRun {}", infoRun.getIdRun());
        return new ResponseEntity<RisultatoInizioValidazione>(new RisultatoInizioValidazione(true, "", infoRun.getIdRun(), flusso.getIdClient()), headers, HttpStatus.OK);
    }


    public RegoleFlusso getRegoleFlusso() {
        File fileRegole = getFileFromPath(conf.getRules().getPercorso());
        ClassPathResource internalRules = new ClassPathResource("/regole-avn-som-res.xml");
        try {
            if (!fileRegole.exists() || fileRegole.lastModified() < internalRules.lastModified()) {
                log.info("Loading internal rules "+internalRules);
                return parserRegole.parseRegole(internalRules);
            }
        } catch (IOException e) {
            log.error("Errore parse regole "+e.getMessage(), e);
            return null;
        }
        log.info("Loading external rules "+fileRegole);
        return parserRegole.parseRegole(fileRegole);
    }
    public RegoleFlusso getRegoleFlusso(File fileRegole) {
        return parserRegole.parseRegole(fileRegole);
    }

    public GestoreRunLog getGestoreRunLog(GestoreFile gestoreFile, Progressivo creaProgressivo) {
        return new GestoreRunLog(gestoreFile, creaProgressivo);
    }

    public File getFileFromPath(String s) {
        return new File(s);
    }
    @Override
    @PostMapping("v1/flusso/vaccinisommresidenti/record")
    public ResponseEntity<RisultatoValidazione> validaRecord(RecordRequest<RecordDtoAvnSomResidenti> recordRequest, String nomeFlusso) {
        return null;
    }

    @Override
    @GetMapping("v1/flusso/vaccinisommresidenti/info")
    public ResponseEntity<InfoRun> informazioniRun(@RequestParam(required = false) String idRun, @RequestParam(required = false) String idClient) {
        GestoreFile gestoreFile = GestoreFileFactory.getGestoreFile(FILE_CSV);
        GestoreRunLog gestoreRunLog = getGestoreRunLog(gestoreFile, Progressivo.creaProgressivo(Progressivo.Fonte.FILE));
        InfoRun infoRun = null;
        if (idRun != null) {
            infoRun = gestoreRunLog.getRun(idRun);
        } else if (idClient != null) {
            infoRun = gestoreRunLog.getRunFromClient(idClient);
        } else {
            return new ResponseEntity<InfoRun>(infoRun, headers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<InfoRun>(infoRun, headers, HttpStatus.OK);
    }
}
