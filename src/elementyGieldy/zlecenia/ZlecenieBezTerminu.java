package elementyGieldy.zlecenia;

import elementyGieldy.inwestorzy.Inwestor;
import systemTransakcji.SystemTransakcji;

public class ZlecenieBezTerminu extends ZlecenieDoNtej{

    public ZlecenieBezTerminu(Inwestor wlasciciel, TypZlecenia typ, String id, int wolumen, int limitCeny,
                              SystemTransakcji system) {
        // ustaw automatycznie termin waznosci na nieosiagalna ture
        super(wlasciciel,typ, id, wolumen, limitCeny, system.getDlugoscSymulacji(), system);
    }

}
