/* SPDX-License-Identifier: BSD-3-Clause */

package it.mds.sdk.flusso.avn.som.residenti.parser.regole;


import it.mds.sdk.libreriaregole.parser.ParserRegole;
import it.mds.sdk.libreriaregole.regole.beans.RegoleFlusso;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component("parserRegoleAvnSomResidenti")
public class ParserRegoleImpl implements ParserRegole {

    @Override
    public RegoleFlusso parseRegole(File regole) {
        return regole.exists() ? parseFileRegole(regole) : creaRegoleFlusso();
    }

    private RegoleFlusso parseFileRegole(File regole) {

        try (FileInputStream is = new FileInputStream(regole)) {
            return parseRegole(is);
        } catch (IOException e) {
            log.error("Impossibile parse del file di regole ", e);
        }
        return null;
    }

    public RegoleFlusso parseRegole(InputStream is) {

        try {
            JAXBContext jaxbContext = org.eclipse.persistence.jaxb.JAXBContextFactory
                    .createContext(new Class[]{RegoleFlusso.class}, null);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Unmarshaller jaxbUnmarshalled = jaxbContext.createUnmarshaller();
            return (RegoleFlusso) jaxbUnmarshalled.unmarshal(is);

        } catch (JAXBException e) {
            log.error("Impossibile parse del file di regole ", e);
        }
        return null;
    }

    public RegoleFlusso parseRegole(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return parseRegole(is);
        } catch (IOException e) {
            log.error("Impossibile parse del file di regole ", e);
        }
        return null;
    }

    private RegoleFlusso creaRegoleFlusso() {
        // TODO return CreazioneRegoleSalmVig.creaRegoleAvnSomResidenti();
        return null;
    }
}
