# **1.Introduzione**

## ***1.1 Obiettivi del documento***

Il Ministero della Salute (MdS) metterà a disposizione degli Enti, da cui riceve dati, applicazioni SDK specifiche per flusso logico e tecnologie applicative (Java, PHP e C#) per verifica preventiva (in casa Ente) della qualità del dato prodotto.

![](img/img4.png)

Nel presente documento sono fornite la struttura e la sintassi dei tracciati previsti dalla soluzione SDK per avviare il proprio processo elaborativo e i controlli di merito sulla qualità, completezza e coerenza dei dati.

Gli obiettivi del documento sono:

- fornire una descrizione funzionale chiara e consistente dei tracciati di input a SDK;
- fornire le regole funzionali per la verifica di qualità, completezza e coerenza dei dati;

In generale, la soluzione SDK è costituita da 2 diversi moduli applicativi (Access Layer e Validation Engine) per abilitare

- l’interoperabilità con il contesto tecnologico dell’Ente in cui la soluzione sarà installata;
- la validazione del dato ed il suo successivo invio verso il MdS.

La figura che segue descrive la soluzione funzionale ed i relativi benefici attesi.

![](img/img2.png)

## ***1.2 Acronimi***

Nella tabella riportata di seguito sono elencati tutti gli acronimi e le definizioni adottati nel presente documento.


|**#**|**Acronimo / Riferimento**|**Definizione**|
| - | - | - |
|1|NSIS|Nuovo Sistema Informativo Sanitario|
|2|SDK|Software Development Kit|
|3|AVN|Anagrafe Nazionale Vaccini|
|4|SSN|Sistema Sanitario Nazionale|
|5|CI|Codice Identificativo|
|6|AIC|Autorizzazione alla Immissione in Commercio del vaccino in Italia rilasciata dall’Agenzia Italiana del Farmaco|


# **2. Architettura SDK**

L'architettura degli SDK è disponibile al seguente link [`ARCHITECTURE.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/ARCHITECTURE.md).

# **3 Funzionamento della soluzione SDK**

In questa sezione è descritta le specifica di funzionamento del flusso **VSX**  per l’alimentazione dello stesso


## ***3.1 Input SDK***

In fase di caricamento del file verrano impostati i seguenti parametri che andranno in input al SDK in fase di processamento del file:


|**NOME PARAMETRO**|**DESCRIZIONE**|**LUNGHEZZA**|**DOMINIO VALORI**|
| :- | :- | :- | :- |
|ID CLIENT|Identificativo univoco della trasazione che fa richiesta all'SDK|100|Non definito|
|NOME FILE INPUT|Nome del file per il quale si richiede il processamento lato SDK|256|Non definito|
|ANNO RIFERIMENTO|Stringa numerica rappresentante l’anno di riferimento per cui si intende inviare la fornitura|4|Anno (Es. 2022)|
|TIPO TRASMISSIONE |Indica se la trasmissione dei dati verso MDS avverrà in modalità full (F) o record per record (R). Per questo flusso la valorizzazione del parametro sarà impostata di default a F|1|F/R|
|FINALITA' ELABORAZIONE|Indica se i flussi in output prodotti dal SDK verranno inviati verso MDS (Produzione) oppure se rimarranno all’interno del SDK e il processamento vale solo come test del flusso (Test)|1|Produzione/Test|
|CODICE REGIONE|<p>Individua la Regione a cui afferisce la struttura. Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.</p><p></p>|3|Es. 010|

Inoltre è previsto anche il parametro Periodo Riferimento, i cui valori differiscono in base al flusso specifico.
Di seguito la tabella specifica per il flusso VSX:



|**NOME PARAMETRO**|**DESCRIZIONE**|**LUNGHEZZA**|**DOMINIO VALORI**|
| :- | :- | :- | :- |
|PERIODO RIFERIMENTO|Stringa alfanumerica rappresentante il periodo per il quale si intende inviare la fornitura|2|Q1 ,Q2, Q3, Q4|

## ***3.2 Tracciato input a SDK***

Il flusso di input avrà formato **csv** posizionale e una naming convention libera a discrezione dell’utente che carica il flusso senza alcun vincolo di nomenclatura specifica (es: nome\_file.csv). Il separatore per il file csv sarà la combinazione di caratteri tra doppi apici: “~“

All’interno della specifica del tracciato sono indicati i dettagli dei campi di business del tracciato di input atteso da SDK, il quale differisce per i sei  flussi dell’area AVN. All’interno di tale file è presente la colonna **Posizione nel file** la quale rappresenta l’ordinamento delle colonne del tracciato di input da caricare all’SDK.


Di seguito la tabella in cui è riportata la specifica del tracciato di input per il flusso in oggetto:

|**Nome campo**|**Posizione nel File2**|**Key**|**Descrizione**|**Tipo** |**Obbligatorietà**|**Informazioni di Dominio**|**Lunghezza campo**|**XPATH Tracciato Output**|
| :- | :- | :- | :- | :- | :- | :- | :- | :- |
|Modalita|0| |Campo tecnico utilizzato per distinguere le modalità di invio di: schede vaccinali di soggetti residenti in regione |A|OBB|Devono essere utilizzati i codici 'RE  per la trasmissione delle informazioni anagarfiche relative alla scheda vaccinale dei **soggetti residenti** nella regione che sta trasmettendo.|2|**/vaccinazioniSomministrate/@Modalita** |
|Tipo Trasmisione|1| |Campo tecnico utilizzato per distinguere trasmissioni di informazioni nuove, modificate o eventualmente annullate|A|OBB|Valori ammessi: <br>I: Inserimento (per la trasmissione di informazioni nuove o per la ritrasmissione di informazioni precedentemente scartate dal sistema di acquisizione);<br>V: Variazione (per la trasmissione di informazioni per le quali si intende far effettuare una sovrascrittura dal sistema di acquisizione);<br>C: Cancellazione (per la trasmissione di informazioni per le quali si intende far effettuare una cancellazione dal sistema di acquisizione).|1|<br>**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@TipoTrasmissione** |
|Codice Regione|2|KEY|Individua la Regione che trasmette il dato. |AN|OBB|Il codice da utilizzare è quello a tre caratteri definito con DM 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n.240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.I valori ammessi sono quelli riportati all’**Allegato 1 – Regioni**|3|**/vaccinazioniSomministrate/@CodiceRegione**|
|Codice Identificatico dell’Assistito|3|KEY|Codice identificativo dell’assistito|AN|OBB|Il campo deve avere lunghezza massima di 20 caratteri in input alla procedura di cifratura che produrrà un output di massimo 172 caratteri.<br>Le modalità di alimentazione del presente campo sono descritte nel paragarfo 3.6 Codice Identificativo dell’ Assistito – procedura di cifratura|172|**/vaccinazioniSomministrate/Assistito/@IdAssistito**|
|Tipologia Erogatore|4| |Tipologia di erogatore che ha effettuato la vaccinazione|AN|OBB|Valori ammessi:<br>0 - Istituto di ricovero pubblico o privato<br>1 - Altra struttura sanitaria pubblica o privata accreditata (ambulatori, laboratori, strutture residenziali e semiresidenziali di assistenza sanitaria agli anziani, a pazienti con disabilità, hospice, consultori, strutture per la salute mentale, SERT,Centri Vaccinali Primula, Centri Vaccinali di Popolazione, etc.)<br>2- Centro vaccinale<br>3- Medico di Medicina Generale (MMG)<br>4 – Pediatra di Libera Scelta (PLS)<br>5 – Specialista<br>6– altro<br>7– Struttura privata autorizzata non accreditata  SSN<br>8- Struttura di riabilitazione<br>9 - Strutture che non effettuano attività sanitaria <br>10 – Struttura Militare<br>11 – Ambulatorio nei luoghi di lavoro<br>12- Farmacia<br>99 – Dato non disponibile|2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@TipoErogatore** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@TipoErogatore%20\(Prestazione\))|
|Codice Struttura|5| |Indica il codice della struttura presso la quale è stata effettuata la vaccinazione oppure a cui appartiene l'erogatore che ha effettuato la vaccinazione|AN|NBB|Valori di riferimento:<br>Se la tipologia di erogatore indicata è 0 – Istituto di ricovero pubblico o privato rappresenta il codice struttura (dei modelli HSP11 , HSP11bis (di cui al decreto ministeriale 5 dicembre 2006, pubblicato nella Gazzetta Ufficiale n. 22 del 27 gennaio 2007, e s.m.i) per Istituti di ricovero pubblici o privati. Ciascun codice è composto da 8 caratteri (campo B del modello HSP11 o campo B + Campo C del modello HSP11Bis) dei quali i primi tre identificano la regione/PA di appartenenza, i successivi tre sono costituiti da un progressivo numerico attribuito in ambito regionale, gli ultimi due costituiscono un eventuale ulteriore progressivo per individuare la singola struttura/stabilimento afferente al complesso ospedaliero;<br>Se la tipologia di erogatore indicata è 1 – Altra struttura sanitaria pubblica o privata accreditata rappresenta il codice struttura (campo D) dei modelli STS11 (di cui al decreto ministeriale 5 dicembre 2006), per altra struttura sanitaria pubblica o privata accreditata (ambulatori, laboratori, strutture residenziali e semiresidenziali di assistenza sanitaria agli anziani, a pazienti con disabilità, hospice, consultori, strutture per la salute mentale, SERT, etc.). Ciascun codice è composto da 6 caratteri <br>Se la tipologia di erogatore indicata è 2 – Centro Vaccinale rappresenta il codice dell’azienda sanitaria a cui afferisce il centro vaccinale.<br>Ciascun codice è composto da 6 caratteri.(codice regione + codice asl)<br>Se la tipologia di erogatore indicata è 3-MMG o 4-PLS o 5-Specialista , 7 – Struttura privata non accreditata, 9- Strutture che non effettua attività sanitaria o 10 – Struttura Militare o 11 – Ambulatorio nei luoghi di lavoro o 12- Farmacia rappresenta il codice dell’azienda sanitaria di iscrizione del MMG/PLS o di riferimento territoriale della struttura privata non accreditata o della Struttura Militare<br>Ciascun codice è composto da 6 caratteri .(codice regione + codice asl)<br>Se la tipologia di erogatore indicata è 8 – Centro di riabilitazione rappresenta il codice struttura (campo B) dei modelli RIA11 (di cui all’ex art. 26 L. 833/78)<br>Ciascun codice è composto da 6 caratteri<br>Se la tipologia di erogatore indicata è  6 – Altro oppure 99- Dato non disponibile, il campo non deve essere valorizzato  |8|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@CodiceStruttura** |
|Condizioni sanitarie a rischio|6| |Indica le condizioni sanitarie del soggetto da vaccinare per cui è raccomandata la vaccinazione (es. Diabete, HIV, Emodializzato...), coerenti con le categorie previste dal PNPV vigente e con il Piano vaccinazioni anti-Covid19|AN|OBB|I valori ammessi sono quelli riportati **all’Allegato 2 - Condizione sanitarie a rischio** |2|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@CodCondizioneSanitaria** |
|Categoria a rischio |7| |Indica la categoria a rischio per la quali viene raccomandata la vaccinazione (esposizione lavorativa, stile di vita, viaggi..), coerenti con le categorie previste dal PNPV vigente e con il Piano vaccinazioni anti-Covid19|AN|OBB|I valori ammessi sono quelli riportati **all’Allegato 3 – Categorie a rischio**|2|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@CodCategoriaRischio**|
|Codice AIC|8| |Codice di autorizzazione immissione in commercio in Italia del vaccino rilasciato dall’ AIFA (AIC)<br>In caso di vaccini esteri, rappresenta il codice con cui viene riconosciuto in Italia.|AN|NBB (Obbligatorio per eventi vaccinali successivi alla data del 01/07/2019)|Il valore ammesso è il codice a 9 cifre concesso dall’Agenzia Italiana del Farmaco (AIFA) al momento del rilascio dell'autorizzazione all’immissione in commercio di un medicinale ad uso umano.<br>Gli attuali bollini farmaceutici presenti su ogni confezione di un medicinale presentano, nella zona alta del medesimo bollino, un codici a barre 39 (conforme al decreto del Ministro della Sanità 2 agosto 2001, pubblicato nella Gazzetta Ufficiale n. 202 del 31 agosto 2001), che consente la lettura ottica del codice AIC.<br>In caso di vaccini esteri è il codice a 9 cifre “E+8 cifre numeriche” che si può recuperare dal portale opendata del Ministero della salute all’indirizzo: http://www.dati.salute.gov.it/|9|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@CodiceAICVaccino** |
|Denominazione Vaccino|9| |Indica il nome commerciale del vaccino somministrato|AN|NBB (Obbligatorio se non viene indicato il codice AIC per eventi vaccinali successivi alla data del 01/07/2019)|Valorizzare sulla base degli elenchi dei vaccini autorizzati resi disponibili da parte dell’AIFA oppure in caso di vaccini esteri sulla base dell’elenco pubblicato sul portale opendata del Ministero della salute all’indirizzo: http://www.dati.salute.gov.it/|100|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@DenomVaccino** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@DenomVaccino)|
|Tipo Formulazione|10| |Indica il tipo di formulazione del vaccino|AN|OBB|I valori ammessi sono quelli riportati all’Allegato 4 – Tipologie Formulazione<br>Nel caso in cui il vaccino somministrato non sia la combinazione di più antigeni riportati nell’allegato 4 valorizzare con Monovalente |2|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@CodTipoFormulazione** |
|Via di Somministrazione|11| |Indica la via di somministrazione del vaccino|AN|OBB|I valori ammessi sono:<br>01 - Intramuscolo<br>02 - Sottocutaneo<br>03 - Intradermico<br>04 - Orale<br>05 – Altro<br>99 – Dato non disponibile|2|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@ViaSomministrazione** |
|Lotto|12| |Indica il numero di lotto di produzione del vaccino|AN|NBB (Obbligatorio per eventi vaccinali successivi alla data del 01/07/2019)|Il valore ammesso è il numero che l’azienda produttrice del medicinale stampa sulle confezioni in maniera tale che, tutte quelle prodotte nello stesso ciclo di lavorazione, siano facilmente riconoscibili|40|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@LottoVaccino** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@LottoVaccino)|
|Data Scadenza |13| |Indica la data di scadenza del vaccino riportata sulla confezione|AN|NBB (Obbligatorio per eventi vaccinali successivi alla data del 01/07/2019)|Formato: AAAA-MM-GG|10|**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@DataScadenza** |
|Modalità Pagamento|14| |Indica le modalità di pagamento del vaccino|AN|OBB|I valori ammessi sono:<br>01 - Vaccinazione a carico SSN<br>02 -Vaccinazione in compartecipazione alla spesa o prezzo sociale<br>03 - Pagamento integrale a carico dell’assistito<br>99 – Dato non disponibile|2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@ModalitaPagamento** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@ModalitaPagamento)|
|Data Somministrazione|15|KEY|Indica la data di somministrazione del vaccino|D|OBB|Formato: AAAA-MM-GG|10|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@DataSomministrazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@DataSomministrazione)|
|Sito Inoculazione|16| |Indica la sede corporea in cui è stato somministrato il vaccino|AN|OBB|I valori ammessi sono:<br>01 - deltoide sinistro<br>02 - deltoide destro<br>03 - quadricipite della coscia sinistra<br>04 - quadricipite della coscia destra<br>05 - gluteo sinistro<br>06 - gluteo destro<br>07 – Altro<br>99 -  Dato non disponibile|2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@SitoInoculazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@SitoInoculazione)|
|Codice Comune Somministrazione|17| |Comune in cui è localizzata la struttura o il centro vaccinale distrettuale o sub distrettuale in cui è stata eseguita la vaccinazione|AN|NBB <br>(Obbligatorio per eventi vaccinali successivi alla data di entrata in vigore del decreto AVN 01/01/2019)|Formato: NNNNNN<br>Il codice da utilizzare è il codice secondo codifica ISTAT, i cui primi tre caratteri individuano la provincia e i successivi un progressivo all’interno di ciascuna provincia che individua il singolo comune. <br>In caso di vaccinazione effettuata all’estero va indicato il codice 999999|6|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@ComuneSomministrazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@ComuneSomministrazione)|
|Codice ASL Somministrazione|18| |Indicare il codice della ASL a cui afferisce la struttura o il centro vaccinale distrettuale o sub distrettuale in cui è stata eseguita la vaccinazione|AN|NBB <br>(Obbligatorio per eventi vaccinali successivi alla data di entrata in vigore del decreto AVN 01/01/2019)|Il campo deve essere valorizzato con i codici a tre caratteri della ASL (di cui al D.M. 05/12/2006 e successive modifiche - Anagrafica MRA fase 1) utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali.<br>In caso di vaccinazione effettuata all’estero va indicato il codice 999|3|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@AslSomministrazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@AslSomministrazione)|
|Codice Regione Somministrazione|19| |Indica il codice della regione a cui afferisce la struttura o il centro vaccinale distrettuale o sub distrettuale in cui è stata eseguita la vaccinazione|AN|NBB <br>(Obbligatorio per eventi vaccinali successivi alla data di entrata in vigore del decreto AVN 01/01/2019)|Formato: NNN<br>I valori ammessi sono quelli a tre caratteri definiti con decreto del Ministero della sanità del 17 settembre 1986, pubblicato nella Gazzetta Ufficiale n. 240 del 15 ottobre 1986, e successive modifiche, utilizzato anche nei modelli per le rilevazioni delle attività gestionali ed economiche delle Aziende unità sanitarie locali. In caso di vaccinazione effettuata all’estero va indicato il codice 999.|3|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@RegioneSomministrazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@RegioneSomministrazione)|
|Stato Estero Somministrazione|20| |Indica lo stato presso cui è stata somministrata la vaccinazione.|AN|NBB <br>(Obbligatorio per eventi vaccinali successivi alla data di entrata in vigore del decreto AVN 01/01/2019)|La codifica da utilizzare è quella Alpha2 (a due lettere) prevista dalla normativa ISO 3166-2.<br>Se compilato con uno stato diverso da IT (Italia), compilare gli altri attributi afferenti la residenza nel modo seguente:<br>Regione di somministrazione=999<br>ASL di somministrazione=999<br>Comune di somministrazioe=999999<br>Ulteriori valori ammessi:<br>XK = Kosovo<br>XX = Stato residenza sconosciuto;<br>ZZ = Apolidi.|2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@StatoEsteroSomministrazione** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/@StatoEsteroSomministrazione)|
|Antigene |21|KEY|Indica il singolo antigene/principio vaccinale che costituisce il vaccino somministrato|AN|OBB|Valorizzare sulla base degli elenchi dei vaccini autorizzati resi disponibili da parte dell’AIFA. <br>In caso di vaccini combinati indicare i singoli antigeni che compongono il vaccino.<br>Ad esempio in caso di somministrazione di vaccino trivalente MPR (Morbillo/Parotite/Rosolia) dovranno essere inseriti tre record.<br>I valori ammessi sono quelli riportati all’Allegato 5 – Antigeni/Principi Vaccinali|2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/PrincipioVaccinale/@CodAntigene** ](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/PrincipioVaccinale/@CodAntigene)|
|Dose|22|KEY|Indica il numero di dose somministrata rispetto al calendario vaccinale per il singolo antigene/principio vaccinale|N|OBB|I valori ammessi sono da 1 a 99|Max 2|[**/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/PrincipioVaccinale/@Dose**](mailto:/vaccinazioniSomministrate/Assistito/VaccinoSomministrato/PrincipioVaccinale/@Dose)|


## ***3.3 Controlli di validazione del dato (business rules)***

Di seguito sono indicati i controlli da configurare sulla componente di Validation Engine e rispettivi error code associati riscontrabili sui dati di input per il flusso VSX.

Gli errori sono solo di tipo scarti (mancato invio del record).

Al verificarsi anche di un solo errore di scarto, tra quelli descritti, il record oggetto di controllo sarà inserito tra i record scartati.

Business Rule non implementabili lato SDK:

- Storiche (Business Rule che effettuano controlli su dati già acquisiti/consolidati che non facciano parte del dato anagrafico)
- Transazionali (Business Rule che effettuano controlli su record, i quali rappresentano transazioni, su cui andrebbe garantito l’ACID (Atomicità-Consistenza-Isolamento-Durabilità))
- Controllo d’integrità (cross flusso) (Business Rule che effettuano controlli sui record utilizzando informazioni estratte da record di altri flussi)


Di seguito le BR per il flusso in oggetto:


|**CAMPO**|**FLUSSO**|**CODICE ERRORE**|**FLAG ATTIVAZIONE**|**DESCRIZIONE ERRORE**|**DESCRIZIONE ALGORITMO**|**TABELLA ANAGRAFICA**|**CAMPI DI COERENZA**|**SCARTI/ANOMALIE**|**TIPOLOGIA BR**|
| :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: | :-: |
|Modalità|VSX|1801|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Modalità|VSX|1802|ATTIVA|Non appartenenza al dominio di riferimento|Valori diversi da quelli ammessi : RE| | |Scarti|Basic|
|Modalità|VSX|1803|ATTIVA|Modalità non coerente con il flusso inviato|in caso di flusso Residenti il naming del flusso conterrà Re e la modalità è diversa da RE| | |Scarti|Basic|
|Tipo Prestazione|VSX|1901.2|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Tipo Prestazione|VSX|1902.2|ATTIVA|Non appartenenza al dominio di riferimento|Valori diversi da quelli ammessi : I,i,V,v,C,c| | |Scarti|Basic|
|Codice Regione |VSX|1921|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Codice Regione |VSX|1922|ATTIVA|Non appartenenza al dominio di riferimento|Il valore inserito e controllato non è presente in anagrafica regioni|Anagrafiche: REGIONE| |Scarti|Anagrafica|
|Codice Regione |VSX|1905|ATTIVA|Il codice regione non coincide con la regione inviante.|Il campo Codice Regione non coincide con la regione che sta trasmettendo il file. | | |Scarti|Basic|
|Codice Identificatico dell’Assistito|VSX|1701|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Codice Identificatico dell’Assistito|VSX|1703|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 172 caratteri| | |Scarti|Basic|
|Tipologia Erogatore|VSX|5001|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML o attributo non presente o tag XML o attributo  presente ma non valorizzato.| | |Scarti|Basic|
|Tipologia Erogatore|VSX|5002|ATTIVA|Non appartenenza al dominio di riferimento|Valore diverso da quelli  ammessi:0,1,2,3,4,5,6,7,8,9,10,11,12,99| | |Scarti|Basic|
|Tipologia Erogatore|VSX|3310|ATTIVA|Incoerenza tra mittente e tipologia Erogatore|Se il codice inviante è 300 -“Ministero della difesa” e la tipologia Erogatore è diversa da 10- Strutture Militari| |codice struttura|Scarti|Basic|
|Codice Struttura|VSX|2011(Ex. 2010)|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza non compresa tra 0 e 8 caratteri| | |Scarti|Basic|
|Codice Struttura|VSX|3005|ATTIVA|Mancata valorizzazione del codice struttura|Il valore è nullo e il campo Tipologia erogatore è diverso da “6” e da “99”’| |Tipologia di erogatore|Scarti|Basic|
|Codice Struttura|VSX|3010|ATTIVA|Codice struttura non presente nell’anagrafica HSP11|Se il tipo erogatore è valorizzato con 0 il codice non è presente nei modelli HSP11 , HSP11bis (di cui al decreto ministeriale 5 dicembre 2006, pubblicato nella Gazzetta Ufficiale n. 22 del 27 gennaio 2007, e s.m.i) -- Istituti di ricovero a 8 cifre|Anagrafiche: HPS11| |Scarti|Anagrafica|
|Codice Struttura|VSX|3015|ATTIVA|Codice struttura non presente nell’anagrafica STS11|Se il tipo erogatore è valorizzato con **1** il codice non è presente nei modelli STS11** (di cui al decreto ministeriale 5 dicembre 2006) a 6 cifre|Anagrafiche: STS11| |Scarti|Anagrafica|
|Codice Struttura|VSX|3020|ATTIVA|Codice struttura non presente nell’anagrafica ASL – MRA Fase 1|Se il tipo erogatore è valorizzato con **2 o 3 o 4 o 5 o 7 o 10 o 11 o 12** il codice non è presente nell’anagrafe di riferimento (D.M. 05/12/2006 e successive modifiche - Anagrafica MRA fase 1) – 6 cifre|Anagrafiche: ASL| |Scarti|Anagrafica|
|Codice Struttura|VSX|3021|ATTIVA|Codice struttura non presente nell’anagrafica RIA11|Se il tipo erogatore è valorizzato con **8** il codice non è presente nei modelli RIA11** (di cui all’ex art. 26 L. 833/78)) a 6 cifre|Anagrafiche: RIA11| |Scarti|Anagrafica|
|Condizioni sanitarie a rischio|VSX|6001|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML o attributo non presente o tag XML o attributo  presente ma non valorizzato.| | |Scarti|Basic|
|Condizioni sanitarie a rischio|VSX|6000|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Condizioni sanitarie a rischio|VSX|3030|ATTIVA|Codice Condizioni Sanitarie a rischio non presente in anagrafica|Se il codice con è presente nell’anagrafe di riferimento – Allegato 2 delle presenti specifiche|Condizioni sanitarie a rischio| |Scarti|Anagrafica|
|Categoria a rischio|VSX|6010|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML o attributo non presente o tag XML o attributo  presente ma non valorizzato.| | |Scarti|Basic|
|Categoria a rischio|VSX|6011|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Categoria a rischio|VSX|5025|ATTIVA|Codice categorie a rischio non presente in anagrafica |Se il codice con è presente nell’anagrafe di riferimento – Allegato 3 delle presenti specifiche|Categorie a rischio| |Scarti|Anagrafica|
|Categoria a rischio|VSX|5026|ATTIVA|Codice categorie a rischio non coerente con Antigene 47 – Vaiolo e Vaiolo delle scimmie|Codice categorie a rischio diverso da “01” e Antigene Valorizzato con “47”| |Antigene|Scarti|Basic|
|Codice AIC|VSX|6020|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 9 caratteri se valorizzato| | |Scarti|Basic|
|Codice AIC|VSX|5020|ATTIVA|Codice AIC non valorizzato|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al **01/07/2019** e lo stato estero di somministrazione è nullo oppure uguale a ‘IT’| |stato estero di Residenza|Scarti|Basic|
|Codice AIC|VSX|3035|ATTIVA|Codice AIC non presente nelle anagrafiche di riferimento AIFA|Se il codice non è presente nell’anagrafe di riferimento dei farmaci/vaccini di AIFA o in caso di vaccini esteri nell’elenco pubblicato dal Ministero della Salute.|AIFA| |Scarti|Anagrafica|
|Denominazione Vaccino|VSX|6060|ATTIVA|Lunghezza maggiore di quella attesa|La lunghezza è maggiore di 100 caratteri se valorizzato| | |Scarti|Basic|
|Denominazione Vaccino|VSX|3040|ATTIVA|Denominazione Vaccino non valorizzata|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al **01/07/2019**| | |Scarti|Basic|
|Tipo formulazione|VSX|6031|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Tipo formulazione|VSX|6030|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Tipo formulazione|VSX|3055|ATTIVA|Tipo Formulazione non presente nelle anagrafiche di riferimento |Se il codice con è presente nell’anagrafe di riferimento – Allegato 4 delle presenti specifiche|Tipologie Formulazione| |Scarti|Anagrafica|
|Tipo formulazione|VSX|3060|ATTIVA|Mancata coerenza tra tipo formulazione e antigeni|Il tipo di formulazione non è coerente con il numero degli antigeti indicati (ES. Monovalente e presenza di 2 antigeni oppure Trivalente e presenza di un solo antigene) e la data di somministrazione del vaccino è valorizzata e successiva al **01/07/2019 <br><br>Descrizione Approfondita:**<br><br>Dato un record su cui il valore del campo tipo formulazione può essere uno dei seguenti:<br><br>- 01 (MONOVALENTE)<br>- 02 (BIVALENTE)<br>- 03 (TRIVALENTE)<br>- 04 (TETRAVALENTE)<br>- 05 (PENTAVALENTE)<br>- 06 (ESAVALENTE):<br><br>Dopo aver convertito il valore del tipo formulazione (Es. 06) in numero **n** (Es. 6), occorre controllare che siano presenti **n-1** record all'interno del file che abbiano gli stessi valori su <br>**codice regione, codice identificativo assistito, data somministrazione, dose**  e **tipo trasmissione prestazione.** <br>Per esempio se un assistito effettua una vaccinazione con tipo formulazione Bivalente allora devono essere presenti per la stessa vaccinazione 2 antigeni. | |antigeni, data di somministrazione vaccino|Scarti|Basic|
|Via di somministrazione|VSX|6041|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Via di somministrazione|VSX|6040|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Via di somministrazione|VSX|6042|ATTIVA|Non appartenenza al dominio di riferimento|Il valore è diverso dai vaolori ammessi:01,02,03,04,05,99| | |Scarti|Basic|
|Lotto|VSX|6043|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza deve essere compresa tra 1 e 40 caratteri se valorizzato| | |Scarti|Basic|
|Lotto|VSX|3070|ATTIVA|Lotto non valorizzato|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al **01/07/2019**| |data somministrazione|Scarti|Basic|
|Data Scadenza|VSX|6050|ATTIVA|Datatype errato|Il campo deve essere valorizzato con il formato data AAAA-MM-GG| | |Scarti|Basic|
|Data Scadenza|VSX|3075|ATTIVA|Data scadenza non valorizzata|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al **01/07/2019**| |data somministrazione|Scarti|Basic|
|Modalità di pagamento|VSX|6061|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Modalità di pagamento|VSX|6063(Ex 6060)|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Modalità di pagamento|VSX|6062|ATTIVA|Non appartenenza al dominio di riferimento|Il valore è diverso dai vaolori ammessi:01,02,03,99| | |Scarti|Basic|
|Data somministrazione|VSX|6071|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML o attributo non presente o tag XML o attributo  presente ma non valorizzato.| | |Scarti|Basic|
|Data somministrazione|VSX|6070|ATTIVA|Datatype errato|Il campo deve essere valorizzato con il formato data AAAA-MM-GG| | |Scarti|Basic|
|Data somministrazione|VSX|4000|ATTIVA|Incoerenza tra data somministrazione e data scadenza del vaccino |La data somministrazione > data scadenza del vaccino| |data scadenza vaccino|Scarti|Basic|
|Sito Inoculazione|VSX|6081|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Sito Inoculazione|VSX|6080|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri se valorizzato| | |Scarti|Basic|
|Sito Inoculazione|VSX|6082|ATTIVA|Non appartenenza al dominio di riferimento|Il valore è diverso da quelli ammessi:01,02,03,04,05,06,07,99| | |Scarti|Basic|
|Sito Inoculazione|VSX|4001|ATTIVA|Sito Inoculazione non congruo|Il valore è uguale a 07-Altro o 99-Dato non Disponibile  e la Via di somministrazione è diversa da 4-Orale, 5-Altro o 99-Dato non disponibile | |via di somministrazione|Scarti|Basic|
|Codice Comune Somministrazione|VSX|6090|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 6 caratteri se valorizzato| | |Scarti|Basic|
|Codice Comune Somministrazione|VSX|4005|ATTIVA|Comune di somministrazione non valorizzato|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al 01/01/2019 | |Data somministrazione|Scarti|Basic|
|Codice Comune Somministrazione|VSX|4010|ATTIVA|Codice comune non presente nel dominio di riferimento|Il codice è diverso da 999999 (somministrato in Italia)  e non è secondo codifica ISTAT, i cui primi tre caratteri individuano la provincia e i successivi un progressivo all’interno di ciascuna provincia che individua il singolo comune. Il controllo non viene effettuato rispetto alla data di somminstrazione.|Comuni Istat| |Scarti|Anagrafica|
|Codice Comune Somministrazione|VSX|4020|ATTIVA|Comune di somministrazione incoerente con Regione o ASL  di Somministrazione|Il codice è diverso da 999999 e nel dominio e il codice  ASL di somministrazione e/o  la Regione di somministrazione sono valorizzati con 999 oppure sono valorizzati con valori che non afferiscono al comune di somministrazione. Il controllo non viene effettuato rispetto alla data di somminstrazione.|tabella di raccordo regioni-province-comuni-asl|codice ASL , regione di somministrazione|Scarti|Anagrafica|
|Codice Comune Somministrazione|VSX|4015(Ex 6102)|ATTIVA|Comune di somministrazione incoerente|Il codice è uguale a 999999 (residenti all’estero)  e lo stato estero di somministrazione è uguale a ‘IT’| | |Scarti|Basic|
|Codice ASL Somministrazione|VSX|6100|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 3 caratteri se valorizzato| | |Scarti|Basic|
|Codice ASL Somministrazione|VSX|4025|ATTIVA|ASL di somministrazione non valorizzata|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al 01/01/2019.| |data di somministrazione|Scarti|Basic|
|Codice ASL Somministrazione|VSX|4030|ATTIVA|Codice ASL  non presente nel dominio di riferimento|Il codice è diverso da 999 e non presente nell’anagrafe di riferimento (D.M. 05/12/2006 e successive modifiche - Anagrafica MRA fase 1).Il controllo non viene effettuato rispetto alla data di somminstrazione.|Anagrafiche: ASL| |Scarti|Anagrafica|
|Codice ASL Somministrazione|VSX|4035( Ex 6112)|ATTIVA|Asl di Somministrazione incoerente|Il codice è uguale a 999 (residenti all’estero)  e lo stato estero di Somministrazione è uguale a ‘IT’| | |Scarti|Basic|
|Codice ASL Somministrazione|VSX|4040|ATTIVA|Asl di Somministrazione incoerente con Regione o Comune  di Somministrazione|Il codice è diverso da 999 e nel dominio e il codice Comune di somministrazione è valorizzato con 999999 e/o  la Regione di somministrazione è valorizzata con 999 oppure sono valorizzati con valori che non afferiscono alla asl di somministrazione . Il controllo non viene effettuato rispetto alla data di somminstrazione.|tabella di raccordo regioni-province-comuni-asl|Regione e comune di somministrazione|Scarti|Anagrafica|
|Codice Regione Somministrazione|VSX|6110|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 3 caratteri| | |Scarti|Basic|
|Codice Regione Somministrazione|VSX|4045|ATTIVA|Regione di somministrazione non valorizzata|Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva al  01/01/2019| | |Scarti|Basic|
|Codice Regione Somministrazione|VSX|4050|ATTIVA|Codice regione non presente nel dominio di riferimento|Il codice è diverso da 999 e non presente nell’anagrafe di riferimento delle regioni. Il controllo non viene effettuato rispetto alla data di somminstrazione.|Anagrafiche: REGIONE| |Scarti|Anagrafica|
|Codice Regione Somministrazione|VSX|4055 (Ex. 6113)|ATTIVA|Regione di Somministrazione incoerente|Il codice è uguale a 999 (residenti all’estero)  e lo stato estero di Somministrazione è uguale a ‘IT’| | |Scarti|Basic|
|Codice Regione Somministrazione|VSX|4060|ATTIVA|Regione di Somministrazione incoerente con Comune o ASL  di Somministrazione|Il codice è diverso da 999 e nel dominio e il codice  Comune di Somministrazione è valorizzato con 999999 e/o  la Asl di Somministrazione è valorizzata con 999 oppure sono valorizzati con valori che non afferiscono alla regione di somministrazione. Il controllo non viene effettuato rispetto alla data di somminstrazione. |tabella di raccordo regioni-province-comuni-asl|codice comune somministrazione, asl somministrazione|Scarti|Anagrafica|
|Stato Estero di somministrazione|VSX|6120|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri| | |Scarti|Basic|
|Stato Estero di somministrazione|VSX|4075|ATTIVA|Stato di somministrazione non valorizzato |Il valore è nullo e la data di somministrazione del vaccino è valorizzata e successiva 01/01/2019| |data somministrazione|Scarti|Basic|
|Stato Estero di somministrazione|VSX|4080|ATTIVA|Codice stato non presente nel dominio di riferimento|Il codice non è presente nella codifica Alpha2 (a due lettere) prevista dalla normativa ISO 3166-2.|Codifica Alpha2| |Scarti|Anagrafica|
|Stato Estero di somministrazione|VSX|4085|ATTIVA|Stato estero di somministrazione diverso da “IT” incoerente con Regione, comune e ASL di somministrazione|Se compilato e diverso da IT (Italia), e Regione di somministrazione diverso da 999 e/o ASL di somministrazione diverso da 999 e/o Comune di somministrazione diverso da 999999| | |scarti|Basic|
|Stato Estero di somministrazione|VSX|4090|ATTIVA|Stato estero di somministrazione uguale a “IT” incoerente con Regione, comune e ASL di somministrazione|Se compilato e uguale a IT (Italia), e Regione di somministrazione uguale a 999 e/o ASL di somministrazione uguale a 999 e/o Comune di somministrazione uguale a 999999| |regione, asl, comune di somministrazione|Scarti|Basic|
|Antigene|VSX|6141|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Antigene|VSX|6140|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza è diversa da 2 caratteri| | |Scarti|Basic|
|Antigene|VSX|4095|ATTIVA|Antigene non presente nelle anagrafiche di riferimento |Se il codice con è presente nell’anagrafe di riferimento – Allegato 5 delle presenti specifiche|Antigeni/Principi Vaccinali| |Scarti|Anagrafica|
|Antigene|VSX|4100|ATTIVA|Antigene non valorizzato in modo corretto |Il valore è uguale a 08-INFLUENZA oppure 09 – ERPES ZOSTER e la data di somministrazione del vaccino è valorizzata e successiva al 01/01/2019| |data di somministrazione|Scarti|Basic|
|Dose|VSX|6051|ATTIVA|Mancata valorizzazione di un campo obbligatorio|Tag XML non presente o tag XML presente ma non valorizzato.| | |Scarti|Basic|
|Dose|VSX|6052(Ex. 6050)|ATTIVA|Lunghezza diversa da quella attesa|La lunghezza al massimo 2 caratteri| | |Scarti|Basic|


## ***3.4 Accesso alle anagrafiche***

I controlli applicativi saranno implementati a partire dall’acquisizione dei seguenti dati anagrafici disponibili in ambito MdS e recuperati con servizi ad hoc (Service Layer mediante PDI):

- REGIONE
- ASL
- tabella di raccordo regioni-province-comuni-asl
- Codifica Alpha2
- HPS11
- STS11
- MRA
- RIA11
- Condizioni sanitarie a rischio
- Categorie a rischio
- AIFA
- ELENCO MdS
- Tipologie Formulazione
- Antigeni
- Principi Vaccinali
- Comuni Istat


Il dato anagrafico sarà presente sottoforma di tabella composta da tre colonne:

- Valore (in cui è riportato il dato, nel caso di più valori, sarà usato il carattere # come separatore)


- Data inizio validità (rappresenta la data di inizio validità del campo Valore)
 - Formato: AAAA-MM-DD
 - Notazione inizio validità permanente: **1900-01-01**


- Data Fine Validità (rappresenta la data di fine validità del campo Valore)
  - Formato: AAAA-MM-DD
  - Notazione fine validità permanente: **9999-12-31**

Affinchè le Business Rule che usano il dato anagrafico per effettuare controlli siano correttamente funzionanti, occorre controllare che la data di competenza del record su cui si effettua il controllo (la quale varia in base al flusso), sia compresa tra le data di validità.  Tutte le tabelle anagrafiche hanno dei dati con validità permanente ad eccezione delle seguenti per le quali sono previste date di validità specifiche:

- Tabella di raccordo regioni-province-comuni-asl
- Comuni Istat
- Codifica Alpha2
- Anagrafiche: ASL
- Anagrafiche: REGIONE

Di seguito viene mostrato un caso limite di anagrafica in cui sono presenti delle sovrapposizioni temporali e contraddizioni di validità permanente/specifico range:


|ID|VALUE|VALID\_FROM|VALID\_TO|
| - | - | - | - |
|1|VALORE 1|1900-01-01|9999-12-31|
|2|VALORE 1|2015-01-01|2015-12-31|
|3|VALORE 1|2018-01-01|2023-12-31|
|4|VALORE 1|2022-01-01|2024-12-31|


Diremo che il dato presente sul tracciato di input è valido se e solo se:

∃ VALUE\_R = VALUE\_A “tale che” VALID\_FROM<= **DATA\_COMPETENZA** <= VALID\_TO

(Esiste almeno un valore compreso tra le date di validità)

Dove:

- VALUE\_R rappresenta i campi del tracciato di input coinvolti nei controlli della specifica BR

- VALUE\_A rappresenta i campi dell’anagrafica coinvolti nei controlli della specifica BR

- VALID\_FROM/VALID\_TO rappresentano le colonne dell’anagrafica

- DATA\_COMPETENZA data da utilizzare per il filtraggio del dato anagrafico specifica per flusso.


### **3.4.1 Flusso VSX**

La DATA\_COMPETENZA da utilizzare per filtrare il dato anagrafico per il flusso VSX sulle tabelle con date di validità specifiche riportate in precedenza è l’anno della data somministrazione del record.

## Istruzioni per l'installazione

Per l'installazione e l'avvio dell'engine seguire la documentazione tecnica dettagliata disponibile all'url [`INSTALL.md`](https://github.com/ministero-salute/sdk-utilities-regole-properties/blob/main/INSTALL.md).

## 📝 Licenza
Questo progetto è rilasciato sotto licenza BSD 3-Clause License così come definita [BSD 3-Clause License](./LICENSE).

## 🤝 Contributi
I contributi sono benvenuti. Si prega di consultare il file [`CONTRIBUTING.md`](CONTRIBUTING.md) per le linee guida su come contribuire al progetto.

## 📞 Contatti
Per ulteriori informazioni, contattare:

- **Service Desk - Ministero della Salute**: servicedesk.mds@medilifegroupspa.com
- **Amministrazione titolare**: [Ministero della Salute](https://www.salute.gov.it)

## mantainer:
 Accenture SpA until January 2026

