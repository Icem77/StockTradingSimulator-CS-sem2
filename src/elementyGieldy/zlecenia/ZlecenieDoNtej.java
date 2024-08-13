package elementyGieldy.zlecenia;

import elementyGieldy.inwestorzy.Inwestor;
import systemTransakcji.SystemTransakcji;

public class ZlecenieDoNtej extends Zlecenie {
    protected int terminWaznosci;

    public ZlecenieDoNtej(Inwestor wlasciciel, TypZlecenia typ, String id, int wolumen, int limitCeny, int termin,
                          SystemTransakcji system) {
        super(wlasciciel,typ, id, wolumen, limitCeny, system);
        this.terminWaznosci = termin;
    }

    public void realizujCzescZlecenia(int wolumenTransakcji, int cenaTransakcji) {
        assert wolumenTransakcji <= wolumen : "Nieprawidlowy wolumen transakcji";

        // realizuj czesc zlecenia
        wolumen -= wolumenTransakcji;

        if (this.typ.equals(TypZlecenia.SPRZEDAZ)) {
            assert cenaTransakcji >= limitCeny : "Cena zakupu jest nizsza od ceny sprzedazy";
            wlasciciel.zmienSaldo(cenaTransakcji * wolumenTransakcji);
            wlasciciel.zmienIloscPosiadanychAkcji(idSpolki, -wolumenTransakcji);

        } else if (this.typ.equals(TypZlecenia.KUPNO)) {
            wlasciciel.zmienSaldo(-(cenaTransakcji * wolumenTransakcji));
            wlasciciel.zmienIloscPosiadanychAkcji(idSpolki, wolumenTransakcji);
        }
    }

    public int getTerminWaznosci() {
        return terminWaznosci;
    }

    @Override
    public void pokazOpis() {
        super.pokazOpis();
        System.out.println("* Termin waznosci: " + terminWaznosci);
    }
}
