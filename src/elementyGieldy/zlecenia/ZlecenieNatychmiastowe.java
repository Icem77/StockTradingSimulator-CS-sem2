package elementyGieldy.zlecenia;

import elementyGieldy.inwestorzy.Inwestor;
import systemTransakcji.SystemTransakcji;

public class ZlecenieNatychmiastowe extends ZlecenieDoNtej {

    public ZlecenieNatychmiastowe(Inwestor wlasciciel, TypZlecenia typ, String id, int wolumen, int limitCeny,
                                  SystemTransakcji system) {
        // ustaw automatycznie termin waznosci na obecna ture
        super(wlasciciel,typ, id, wolumen, limitCeny, system.getObecnaTura(), system);
    }


}
