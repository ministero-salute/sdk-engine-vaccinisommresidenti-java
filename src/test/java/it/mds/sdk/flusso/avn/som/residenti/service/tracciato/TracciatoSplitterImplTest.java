/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti.service.tracciato;

import it.mds.sdk.flusso.avn.som.residenti.parser.regole.ParserTracciatoImpl;
import it.mds.sdk.flusso.avn.som.residenti.parser.regole.RecordDtoAvnSomResidenti;
import it.mds.sdk.flusso.avn.som.residenti.tracciato.TracciatoSplitterImpl;
import it.mds.sdk.libreriaregole.dtos.RecordDtoGenerico;
import it.mds.sdk.libreriaregole.parser.ParserTracciato;
import it.mds.sdk.libreriaregole.tracciato.TracciatoSplitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TracciatoSplitterImplTest {

    private static final String FILE_TRACCIATO_VSX = "VSX.csv";

    @Test
    void dividiTracciatoTest() {
        ParserTracciato parserTracciato = new ParserTracciatoImpl();
        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        File tracciato = new File(absolutePath + FileSystems.getDefault().getSeparator() + FILE_TRACCIATO_VSX);
        List<RecordDtoGenerico> listaRecord = parserTracciato.parseTracciato(tracciato);
        TracciatoSplitter<RecordDtoAvnSomResidenti> impl = new TracciatoSplitterImpl();
        //impl.dividiTracciato(listaRecord.stream().map(element ->(RecordDtoDir)element).collect(Collectors.toList()));
    }
}