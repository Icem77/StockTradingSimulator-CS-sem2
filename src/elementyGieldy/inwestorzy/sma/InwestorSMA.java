package elementyGieldy.inwestorzy.sma;

import elementyGieldy.inwestorzy.random.InwestorRANDOM;
import elementyGieldy.inwestorzy.random.Losowanie;
import elementyGieldy.zlecenia.*;
import systemTransakcji.SystemTransakcji;

import java.util.HashMap;
import java.util.Map;

public class InwestorSMA extends InwestorRANDOM {

    Map<String, DaneSpolkiWArkuszu> arkusz;

    public InwestorSMA() {
        super();
        this.arkusz = null;
    }

    @Override
    public void przydzielPortfel(Map<String, Integer> portfel) {
        super.przydzielPortfel(portfel);

        // utworz arkusz kalkulacyjny na bazie przydzielonego portfela
        this.arkusz = new HashMap<>();
        for (String idSpolki : portfel.keySet()) {
            arkusz.put(idSpolki, new DaneSpolkiWArkuszu());
        }
    }

    @Override
    public Zlecenie podejmijDecyzje(SystemTransakcji system) {
        String idDoZainwestowania = null;
        TypZlecenia sygnalInwestycji = null;

        // dla kazdej spolki w portfelu przeanalizuj srednie
        for (String idSpolki : portfel.keySet()) {

            TypZlecenia sygnal = sprawdzSygnal(idSpolki, system.getCenaRynkowa(idSpolki));

            // Sprawdz czy inwestor otrzymal wartosciowy sygnal o podjeciu inwestycji
            if (system.getObecnaTura() >= 10 && sygnal != null) {
                switch(sygnal) {
                    case SPRZEDAZ:
                        // sprawdz czy inwestor posiada odpowiednie srodki aby
                        // sprobowac znalezc akcje spolki ktore rzeczywiscie moze sprzedac
                        if (portfel.get(idSpolki) > 0 || idDoZainwestowania == null) {
                            idDoZainwestowania = idSpolki;
                            sygnalInwestycji = sygnal;
                        }
                        break;
                    case KUPNO:
                        // sprawdz czy inwestor posiada odpowiedni srodki aby sprobowac
                        // znalezc spolke na ktorej conajmniej 1 akcje moze sobie pozwolic
                        if (saldo >= system.getCenaRynkowa(idSpolki) || idDoZainwestowania == null) {
                            idDoZainwestowania = idSpolki;
                            sygnalInwestycji = sygnal;
                        }
                }
            }
        }

        // sprawdz czy zebralismy jakis sygnal
        if (sygnalInwestycji != null) {

            // wylosuj rodzaj zlecenia
            int rodzajZlecenia = Losowanie.losuj(0, 2);

            // zakladamy ze inwestor SMA wykonuje transakcje po cenie rynkowej
            int cenaZlecenia = system.getCenaRynkowa(idDoZainwestowania);
            int wolumen;
            // wylosuj wolumen zlecenia
            if (sygnalInwestycji.equals(TypZlecenia.SPRZEDAZ)) {
                wolumen = wybierzWolumenSprzedazy(idDoZainwestowania, system);
            } else {
                wolumen = wybierzWolumenKupna(idDoZainwestowania, cenaZlecenia, system);
            }

            // tworzymy adekwatne zlecenie
            switch(rodzajZlecenia) {
                case 0: // zlecenie natychmiastowe
                    return new ZlecenieNatychmiastowe(this, sygnalInwestycji, idDoZainwestowania,
                            wolumen, cenaZlecenia, system);
                case 1: // zlecenie do n-tej tury
                    return new ZlecenieDoNtej(this, sygnalInwestycji, idDoZainwestowania,
                            wolumen, cenaZlecenia, wybierzTureWaznosciZlecenia(system), system);
                default: // zlecenie bezterminowe
                    return new ZlecenieBezTerminu(this, sygnalInwestycji, idDoZainwestowania,
                            wolumen, cenaZlecenia, system);
            }
        }

        return null;
    }

    private TypZlecenia sprawdzSygnal(String idSpolki, int nowaCena) {

        // sprawdz relacje miedzy srednimi z poprzedniej tury
        Boolean SMA5ponad10 = null;
        if (arkusz.get(idSpolki).getSMA5() > arkusz.get(idSpolki).getSMA10()) {
            SMA5ponad10 = true;
        } else if (arkusz.get(idSpolki).getSMA5() < arkusz.get(idSpolki).getSMA10()) {
            SMA5ponad10 = false;
        }

        // zaktualizuj dane w arkuszu
        arkusz.get(idSpolki).aktualizujDane(nowaCena);

        // sprawdz relacje miedzy srednimi w obecnej turze
        if (arkusz.get(idSpolki).getSMA5() > arkusz.get(idSpolki).getSMA10() &&
                (SMA5ponad10 == null || SMA5ponad10.equals(false))) {
            return TypZlecenia.KUPNO;
        } else if (arkusz.get(idSpolki).getSMA5() > arkusz.get(idSpolki).getSMA10() &&
                (SMA5ponad10 == null || SMA5ponad10.equals(true))) {
            return TypZlecenia.SPRZEDAZ;
        }

        return null;
    }
}
