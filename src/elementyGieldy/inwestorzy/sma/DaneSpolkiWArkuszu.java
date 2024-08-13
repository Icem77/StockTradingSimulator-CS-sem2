package elementyGieldy.inwestorzy.sma;

import java.util.LinkedList;

// struktura przechowujaca informacje o spolce potrzebne z perspektywy inwestora SMA
public class DaneSpolkiWArkuszu {
    private LinkedList<Integer> SMA;
    private int sumaSMA5;
    private int sumaSMA10;

    public DaneSpolkiWArkuszu() {
        this.SMA = new LinkedList<>();
        this.sumaSMA10 = 0;
        this.sumaSMA5 = 0;
    }

    public int getSMA5() {
        return sumaSMA5 / 5;
    }

    public int getSMA10() {
        return sumaSMA10 / 10;
    }

    public void aktualizujDane(int cena) {
        // dodaj cene z najnowszej tury
        sumaSMA5 += cena;
        sumaSMA10 += cena;
        SMA.addLast(cena);

        if (SMA.size() > 5) {
            // usun cene sprzed 5 tur
            sumaSMA5 -= SMA.get(SMA.size() - 6);
        }
        if (SMA.size() > 10) {
            // usun cene sprzed 10 tur
            sumaSMA10 -= SMA.removeFirst();
        }

    }
}
