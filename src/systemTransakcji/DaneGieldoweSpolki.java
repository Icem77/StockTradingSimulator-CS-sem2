package systemTransakcji;

import elementyGieldy.zlecenia.TypZlecenia;
import elementyGieldy.zlecenia.Zlecenie;

import java.util.Collections;
import java.util.List;

// struktura przechowujaca informacje o spolce potrzebne z perspektywy Systemu Transakcji
public class DaneGieldoweSpolki {
    private int cenaRynkowa;
    private List<Zlecenie> zleceniaKupna;
    private List<Zlecenie> zleceniaSprzedazy;

    public DaneGieldoweSpolki(int cenaRynkowa, List<Zlecenie> zleceniaKupna, List<Zlecenie> zleceniaSprzedazy) {
        this.cenaRynkowa = cenaRynkowa;
        this.zleceniaKupna = zleceniaKupna;
        this.zleceniaSprzedazy = zleceniaSprzedazy;
    }

    public void dodajZlecenie(Zlecenie zlecenie) {
        List<Zlecenie> gdzieDodac = null;

        if (zlecenie.getTyp().equals(TypZlecenia.SPRZEDAZ)) {
            gdzieDodac = zleceniaSprzedazy;
        } else {
            gdzieDodac = zleceniaKupna;
        }

        // jezeli elementu nie ma w liscie, binarySearch() zwraca ujemna wartosc ktorej
        // inkrementacja i zanegowane pozwala nam sprytnie obliczyc pozycje na ktora powinnismy wstawic ten element
        int index = Collections.binarySearch(gdzieDodac, zlecenie);

        if (index < 0) {
            index = -(index + 1);
        }

        gdzieDodac.add(index, zlecenie);

    }

    public List<Zlecenie> getZleceniaKupna() {
        return zleceniaKupna;
    }

    public List<Zlecenie> getZleceniaSprzedazy() {
        return zleceniaSprzedazy;
    }

    public int getCenaRynkowa() {
        return cenaRynkowa;
    }

    public void setCenaRynkowa(int cenaRynkowa) {
        this.cenaRynkowa = cenaRynkowa;
    }
}
