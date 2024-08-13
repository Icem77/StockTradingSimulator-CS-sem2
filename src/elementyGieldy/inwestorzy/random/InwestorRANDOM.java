package elementyGieldy.inwestorzy.random;

import elementyGieldy.inwestorzy.Inwestor;
import elementyGieldy.zlecenia.*;
import systemTransakcji.SystemTransakcji;

import java.util.ArrayList;

public class InwestorRANDOM extends Inwestor {

    public InwestorRANDOM() {
        super();
    }

    @Override
    public Zlecenie podejmijDecyzje(SystemTransakcji system) {

        // dopasowujemy liczby do zlecen natychmiastowych/do n-tej tury/bez terminu
        int rodzajZlecenia = Losowanie.losuj(0, 2);

        // losujemy parametry zlecenia
        TypZlecenia typ = wybierzTypZlecenia();
        String idSpolki = wybierzSpolke();
        int cena = wybierzCeneZlecenia(idSpolki, system);
        int wolumen;
        if (typ.equals(TypZlecenia.SPRZEDAZ)) {
            wolumen = wybierzWolumenSprzedazy(idSpolki, system);
        } else {
            wolumen = wybierzWolumenKupna(idSpolki, cena, system);
        }

        // tworzymy adekwatne zlecenie
        switch(rodzajZlecenia) {
            case 0: // zlecenie natychmiastowe
                return new ZlecenieNatychmiastowe(this, typ, idSpolki, wolumen, cena, system);
            case 1: // zlecenie do n-tej tury
                return new ZlecenieDoNtej(this, typ, idSpolki, wolumen, cena,
                                            wybierzTureWaznosciZlecenia(system), system);
            default: // zlecenie bezterminowe
                return new ZlecenieBezTerminu(this, typ, idSpolki, wolumen, cena, system);
        }
    }

    private String wybierzSpolke() {
        // zamieniamy identyfikatory z portfela w Array Liste
        ArrayList<String> identyfikatorySpolek = new ArrayList<>(portfel.keySet());
        // zwracamy przypadkowy element
        return identyfikatorySpolek.get(Losowanie.losuj(0 , identyfikatorySpolek.size() - 1));
    }

    protected int wybierzWolumenSprzedazy(String idSpolki, SystemTransakcji system) {
        // losujemy zakres akcji od 1 do liczby posiadanych
        return Losowanie.losuj(1, portfel.get(idSpolki));
    }

    protected int wybierzWolumenKupna(String idSpolki, int cenaZakupu, SystemTransakcji system) {
        // losujemy zakres akcji od 1 do tylu na ile maksymalnie stac inwestora
        int maxDoZakupu = saldo / system.getCenaRynkowa(idSpolki);
        return Losowanie.losuj(1, maxDoZakupu);
    }

    private int wybierzCeneZlecenia(String idSpolki, SystemTransakcji system) {
        // Aby przetwarzac wiecej zlecen sprawiamy ze inwestor nie sklada zlecen odbiegajacych od ceny
        // rynkowej o wiecej niz 10 jednostek (System nadal sprawdza poprawnosc zlecen!)
        int cenaGorna = system.getCenaRynkowa(idSpolki) + 10;
        int cenaDolna = cenaGorna - 20;
        if (cenaDolna <= 0) {
            cenaDolna = 1;
        }
        return Losowanie.losuj(cenaDolna, cenaGorna);
    }

    protected int wybierzTureWaznosciZlecenia(SystemTransakcji system) {
        // losujemy pomiedzy obecna a ostatnia tura
        return Losowanie.losuj(system.getObecnaTura(), system.getDlugoscSymulacji() - 1);
    }

    protected TypZlecenia wybierzTypZlecenia() {
        if (Losowanie.losuj(0, 1) == 0) {
            return TypZlecenia.SPRZEDAZ;
        } else {
            return TypZlecenia.KUPNO;
        }
    }
}
