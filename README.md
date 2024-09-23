- [ ] Mail server gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza
- [ ] Il mail Server ha un'interfaccia grafica sulla quale viene visualizzato il log delle azioni
- [ ] casella di posta
  - [ ] nome account di mail associata alla casella posta
  - [ ] Lista di messaggi
- [ ] Il mail Client ha un interfaccia grafica tale che:
  - [ ] Crea e invia un messaggio a uno o più destinatari
  - [ ] legge i messaggi
  - [ ] risponde in Reply o Reply all
  - [ ] gira un messaggio a uno o più destinatari
  - [ ] rimuove il messaggio della casella di posta
  - [ ] L'interfaccia mostra sempre la lista aggiornata dei messaggi in casella e quando arriva un messaggio notifica l'utente con una finestra di dialogo

ATTENZIONE AD OGNI AZIONE CORRISPONDE L'APERTURA E LA CHIUSURA DELLA SOCKET DA PARTE DEL CLIENT, NEL MOMENTO IN CUI IL SERVER CHIUDE LA SOCKET CI DEV'ESSERE UN ALLERT.
