package systemTransakcji.wyjatki;

public class NiepoprawnyFormatDanych extends Exception{

    public NiepoprawnyFormatDanych(String komunikat) {
        super(komunikat);
    }
}
