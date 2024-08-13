package elementyGieldy.zlecenia;

import elementyGieldy.inwestorzy.Inwestor;
import systemTransakcji.SystemTransakcji;

public abstract class Zlecenie implements Comparable<Zlecenie>{

    private static int nastepneId = 0;
    private final int id;
    protected final Inwestor wlasciciel;
    protected final TypZlecenia typ;
    protected final String idSpolki;
    protected int wolumen;
    protected int limitCeny;
    private final int turaDodania;
    private final int numerWTurze;

    public Zlecenie(Inwestor wlasciciel, TypZlecenia typ, String id, int wolumen, int limitCeny,
                    SystemTransakcji system) {
        this.id = nastepneId++;
        this.wlasciciel = wlasciciel;
        this.typ = typ;
        this.idSpolki = id;
        this.wolumen = wolumen;
        this.limitCeny = limitCeny;
        this.numerWTurze = system.getZleceniaWTurze();
        this.turaDodania = system.getObecnaTura();
    }

    public int getLimitCeny() {
        return limitCeny;
    }

    public int getNumerWTurze() {
        return numerWTurze;
    }

    public int getTuraDodania() {
        return turaDodania;
    }

    public int getWolumen() {
        return wolumen;
    }

    public Inwestor getWlasciciel() {
        return wlasciciel;
    }

    public TypZlecenia getTyp() {
        return typ;
    }

    public String getIdSpolki() {
        return idSpolki;
    }

    @Override
    public int compareTo(Zlecenie zlecenie) {
        // porownujemy po cenie
        int porownanie = Integer.compare(limitCeny, zlecenie.getLimitCeny());

        if (porownanie != 0) {
            return porownanie;
        } else {
            // jezeli ceny sa rowne porownujemy po turze dodania
            porownanie = Integer.compare(turaDodania, zlecenie.getTuraDodania());

            // jezeli tura dodania jest taka sama porownujemy po kolejnosci w turze
            // ktora nigdy nie da juz rownosci miedzy roznymi zleceniami
            if (porownanie == 0) {
                porownanie = Integer.compare(numerWTurze, zlecenie.getNumerWTurze());
            }

            if (porownanie < 0) {
                // starsze zlecenia sprzedazy dajemy bardziej z lewej strony
                // ze wzgledu na konstrukcje petli przetwarzajacej zlecenia
                if (this.typ.equals(TypZlecenia.SPRZEDAZ)) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (porownanie > 0){
                // starsze zlecenia kupna dajemy bardziej z prawej strony
                if (this.typ.equals(TypZlecenia.SPRZEDAZ)) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public void pokazOpis() {
        System.out.println("Zlecenie " + id + ":");
        System.out.println("* ID wlasciciela: " + wlasciciel);
        System.out.println("* Spolka: " + idSpolki);
        System.out.println("* Typ: " + typ);
        System.out.println("* Cena: " + limitCeny);
        System.out.println("* Wolumen: " + wolumen);
    }

    public int dodanePrzed(Zlecenie zlecenie) {
        // zwraca wartosc ujemna gdy 'this' zostalo dodane po 'zleceniu'
        // zwraca wartosc dodatnia gdy 'zlecenie' zostalo dodane po 'this'
        // zwraca 0 gdy daty dodania sa identyczne (zlecenie jest tym samym co this)
        int porownanie = zlecenie.getTuraDodania() - turaDodania;
        if (porownanie != 0) {
            return porownanie;
        } else {
            porownanie = zlecenie.numerWTurze - numerWTurze;
            return porownanie;
        }
    }
}
