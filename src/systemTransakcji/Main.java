package systemTransakcji;

import systemTransakcji.wyjatki.NiepoprawnyFormatDanych;

public class Main {

    public static void main(String[] args) throws NiepoprawnyFormatDanych, AssertionError {
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        if (system != null) {
            system.rozpocznijSymulacje();
        }
    }
}
