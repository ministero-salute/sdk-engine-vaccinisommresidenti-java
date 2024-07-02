/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti.controller;

import it.mds.sdk.connettore.anagrafiche.tabella.EsitoBR3060;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.ParserRegoleImpl;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.ParserTracciatoImpl;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.conf.ConfigurazioneFlussoAvnSomResidenti;
import it.mds.sdk.flusso.avn.som.residenti.service.FlussoAvnSomResidentiService;
import it.mds.sdk.gestoreesiti.GestoreRunLog;
import it.mds.sdk.gestoreesiti.modelli.InfoRun;
import it.mds.sdk.gestoreesiti.modelli.ModalitaOperativa;
import it.mds.sdk.gestorefile.GestoreFile;
import it.mds.sdk.gestorefile.factory.GestoreFileFactory;
import it.mds.sdk.libreriaregole.parser.ParserRegole;
import it.mds.sdk.libreriaregole.regole.beans.RegoleFlusso;
import it.mds.sdk.rest.persistence.entity.FlussoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
class FlussoAvnSomResidentiControllerRestTest {
    @InjectMocks
    @Spy
    private FlussoAvnSomResidentiControllerRest controller;

    private FlussoRequest flussoRequest = new FlussoRequest();

    private MockedStatic<GestoreFileFactory> gestoreFileFactory;

    @Spy
    private ConfigurazioneFlussoAvnSomResidenti conf;
    @Spy
    private ParserRegoleImpl parserRegole;
    @Mock
    private FlussoAvnSomResidentiService service;
    private ConfigurazioneFlussoAvnSomResidenti.Rules rules = mock(ConfigurazioneFlussoAvnSomResidenti.Rules.class);

    private ConfigurazioneFlussoAvnSomResidenti.Flusso flusso = mock(ConfigurazioneFlussoAvnSomResidenti.Flusso.class);

    private File file = mock(File.class);
    private RegoleFlusso regoleFlusso = Mockito.mock(RegoleFlusso.class);
    private GestoreFile gestoreFile = Mockito.mock(GestoreFile.class);
    private final ParserTracciatoImpl parser = Mockito.mock(ParserTracciatoImpl.class);

    private GestoreRunLog gestoreRunLog = mock(GestoreRunLog.class);
    private InfoRun infoRun = Mockito.mock(InfoRun.class);
    private ConfigurazioneFlussoAvnSomResidenti.NomeFlusso nomeFlusso = Mockito.mock(ConfigurazioneFlussoAvnSomResidenti.NomeFlusso.class);

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        initFlussoRequest();
    }

    @Test
        //tofix
    void validaTracciatoTest() {
        when(conf.getRules()).thenReturn(rules);
        when(rules.getPercorso()).thenReturn("percorso1_");

        when(conf.getFlusso()).thenReturn(flusso);
        when(flusso.getPercorso()).thenReturn("percorso2_");
        EsitoBR3060 esito = new EsitoBR3060(1, "abc", "cde");
        List<EsitoBR3060> esitoBR3060s = List.of(esito);

        when(conf.getNomeFLusso()).thenReturn(nomeFlusso);
        when(nomeFlusso.getNomeFlusso()).thenReturn("nomeFlusso");
        when(controller.getFileFromPath(anyString())).thenReturn(file);
        when(file.exists()).thenReturn(true);

        gestoreFileFactory = mockStatic(GestoreFileFactory.class);
        gestoreFileFactory.when(() -> GestoreFileFactory.getGestoreFile("CSV")).thenReturn(gestoreFile);
        when(controller.getGestoreRunLog(any(), any())).thenReturn(gestoreRunLog);
        when(gestoreRunLog.creaRunLog(any(), any(), anyInt(), any())).thenReturn(infoRun);
        when(gestoreRunLog.cambiaStatoRun(any(), any())).thenReturn(infoRun);

        doReturn(regoleFlusso).when(controller).getRegoleFlusso(file);
        doReturn(regoleFlusso).when(parserRegole).parseRegole(file);

        doNothing().when(service)
                .validazioneBlocchi(
                        anyInt(),
                        anyString(),
                        any(),
                        anyString(),
                        anyString(),
                        any(),
                        anyString(),
                        anyString(),
                        anyString(),
                        any(),
                        eq(esitoBR3060s)
                );

        controller.validaTracciato(
                flussoRequest,
                "nomeFlusso"
        );
        gestoreFileFactory.close();
    }

    @Test
    public void informazioniRunTest() {
        gestoreFileFactory = mockStatic(GestoreFileFactory.class);
        gestoreFileFactory.when(() -> GestoreFileFactory.getGestoreFile("CSV")).thenReturn(gestoreFile);
        when(controller.getGestoreRunLog(any(), any())).thenReturn(gestoreRunLog);
        when(gestoreRunLog.getRun(any())).thenReturn(infoRun);

        controller.informazioniRun("idRun", "idClient");
        gestoreFileFactory.close();
    }
    private void initFlussoRequest() {
        flussoRequest.setNomeFile("nomeFile.txt");
        flussoRequest.setModalitaOperativa(ModalitaOperativa.T);
        flussoRequest.setIdClient("1");
        flussoRequest.setAnnoRiferimento("2022");
        flussoRequest.setPeriodoRiferimento("S2");
        flussoRequest.setCodiceRegione("080");
    }
}