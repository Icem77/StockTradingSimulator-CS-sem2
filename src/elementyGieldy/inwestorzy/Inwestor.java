package elementyGieldy.inwestorzy;

import elementyGieldy.zlecenia.Zlecenie;
import systemTransakcji.SystemTransakcji;

import java.util.Map;

public abstract class Inwestor {

    private static int nastepneId = 0;
    private int id;
    protected int saldo;
    protected Map<String, Integer> portfel;

    public Inwestor() {
        this.saldo = 0;
        this.portfel = null;
        this.id = nastepneId++;
    }

    public int getSaldo() {
        return saldo;
    }

    public abstract Zlecenie podejmijDecyzje(SystemTransakcji system);

    public void przydzielPortfel(Map<String, Integer> portfel) {
        this.portfel = portfel;
    }

    public void zmienSaldo(int roznica) {
        this.saldo += roznica;
        assert saldo >= 0 : "Saldo inwestora spadlo pozniej zera";
    }

    public void zmienIloscPosiadanychAkcji(String idSpolki, int liczbaAkcji) {
        assert portfel.containsKey(idSpolki) : "Inwestor nie posiada tej spolki w portfelu";
        portfel.put(idSpolki, portfel.get(idSpolki) + liczbaAkcji);
        assert  portfel.get(idSpolki) >= 0 : "Liczba posiadanych akcji spadla ponizej zera";
    }

    public int getIloscPosiadanychAkcji(String idSpolki) {
        assert portfel.containsKey(idSpolki) : "Inwestor nie posiada tej spolki w portfelu";
        return portfel.get(idSpolki);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public void wypiszStanKonta(SystemTransakcji system) {
        System.out.println("Inwestor " + this + ":");
        System.out.println("-> Stan konta: " + saldo);
        int netWorth = saldo;
        for (String idSpolki : portfel.keySet()) {
            System.out.println("-> " + idSpolki + ": " + portfel.get(idSpolki));
            netWorth += system.getCenaRynkowa(idSpolki) * portfel.get(idSpolki);
        }
        System.out.println("-> Net Worth: " + netWorth);
        System.out.println("----------------------------------");
    }
}
