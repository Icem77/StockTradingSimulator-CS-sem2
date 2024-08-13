package testy;

import elementyGieldy.zlecenia.TypZlecenia;
import elementyGieldy.zlecenia.Zlecenie;
import elementyGieldy.zlecenia.ZlecenieDoNtej;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import systemTransakcji.Main;
import systemTransakcji.SystemTransakcji;
import systemTransakcji.wyjatki.NiepoprawnyFormatDanych;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {

    // Testy niezmiennika
    @Test
    @DisplayName("niezmiennik1")
    public void niezmiennik1() throws NiepoprawnyFormatDanych {
        String[] args = {"inputs/danePoprawne/dane1.txt", "13"};
        int akcjeApple = 400;
        int akcjeGoogle = 80;
        int pieniadzWObrocie = 4000;
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);
        system.rozpocznijSymulacje();

        assertEquals(akcjeApple, system.obliczAkcjeWObrocie("APPLE"));
        assertEquals(akcjeGoogle, system.obliczAkcjeWObrocie("GOOGL"));
        assertEquals(pieniadzWObrocie, system.obliczPieniadzWObrocie());
    }

    @Test
    @DisplayName("niezmiennik2")
    public void niezmiennik2() throws NiepoprawnyFormatDanych {
        String[] args = {"inputs/danePoprawne/dane2.txt", "100"};
        int akcjeApple = 678;
        int pieniadzWObrocie = 60000;
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);
        system.rozpocznijSymulacje();

        assertEquals(akcjeApple, system.obliczAkcjeWObrocie("APPLE"));
        assertEquals(pieniadzWObrocie, system.obliczPieniadzWObrocie());
    }

    @Test
    @DisplayName("niezmiennik3")
    public void niezmiennik3() throws NiepoprawnyFormatDanych {
        String[] args = {"inputs/danePoprawne/dane3.txt", "1001"};
        int akcjeA = 7 * 120;
        int akcjeB = 7 * 33;
        int akcjeC = 7 * 70;
        int pieniadzWObrocie = 1700 * 7;
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);
        system.rozpocznijSymulacje();

        assertEquals(akcjeA, system.obliczAkcjeWObrocie("A"));
        assertEquals(akcjeB, system.obliczAkcjeWObrocie("B"));
        assertEquals(akcjeC, system.obliczAkcjeWObrocie("C"));

        assertEquals(pieniadzWObrocie, system.obliczPieniadzWObrocie());
    }

    @Test
    @DisplayName("niezmiennik4")
    public void niezmiennik4() throws NiepoprawnyFormatDanych {
        String[] args = {"inputs/danePoprawne/dane4.txt", "1001"};
        int akcjeNVIDI = 50 * 12;
        int akcjeFB = 1000 * 12;
        int pieniadzWObrocie = 1000 * 12;
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);
        system.rozpocznijSymulacje();

        assertEquals(akcjeNVIDI, system.obliczAkcjeWObrocie("NVIDI"));
        assertEquals(akcjeFB, system.obliczAkcjeWObrocie("FB"));

        assertEquals(pieniadzWObrocie, system.obliczPieniadzWObrocie());
    }

    @Test
    @DisplayName("niezmiennik5")
    public void niezmiennik5() throws NiepoprawnyFormatDanych {
        String[] args = {"inputs/danePoprawne/dane5.txt", "1001"};
        int akcjeNVIDI = 50;
        int akcjeFB = 1000;
        int pieniadzWObrocie = 1000;
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);
        system.rozpocznijSymulacje();

        assertEquals(akcjeNVIDI, system.obliczAkcjeWObrocie("NVIDI"));
        assertEquals(akcjeFB, system.obliczAkcjeWObrocie("FB"));

        assertEquals(pieniadzWObrocie, system.obliczPieniadzWObrocie());
    }


    // Testy aktualnosci/poprawnosci/sortowania zlecen
    @Test
    @DisplayName("sortowanieZlecen1")
    public void sortowanieZlecen1() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane3.txt", "100"};

        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        for (int k = 0; k < 50; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaKupna = system.pokazListZlecen("A", TypZlecenia.KUPNO);

            for (int j = 0; j < zleceniaKupna.size() - 1; j++) {
                assertTrue(zleceniaKupna.get(j).getLimitCeny() <= zleceniaKupna.get(j + 1).getLimitCeny());
                if (zleceniaKupna.get(j).getLimitCeny() == zleceniaKupna.get(j + 1).getLimitCeny()) {
                    assertTrue(zleceniaKupna.get(j).dodanePrzed(zleceniaKupna.get(j + 1)) < 0);
                }
            }
        }
    }

    @Test
    @DisplayName("sortowanieZlecen2")
    public void sortowanieZlecen2() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane3.txt", "1000"};
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        // przeprowadz kilka probnych tur
        for (int k = 0; k < 250; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaSprzedazy = system.pokazListZlecen("C", TypZlecenia.SPRZEDAZ);

            for (int j = 0; j < zleceniaSprzedazy.size() - 1; j++) {
                assertTrue(zleceniaSprzedazy.get(j).getLimitCeny() <= zleceniaSprzedazy.get(j + 1).getLimitCeny());
                if (zleceniaSprzedazy.get(j).getLimitCeny() == zleceniaSprzedazy.get(j + 1).getLimitCeny()) {
                    assertTrue(zleceniaSprzedazy.get(j).dodanePrzed(zleceniaSprzedazy.get(j + 1)) > 0);
                }
            }
        }
    }

    @Test
    @DisplayName("sortowanieZlecen3")
    public void sortowanieZlecen3() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane4.txt", "1000"};
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        // przeprowadz kilka probnych tur
        for (int k = 0; k < 250; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaSprzedazy = system.pokazListZlecen("FB", TypZlecenia.SPRZEDAZ);

            for (int j = 0; j < zleceniaSprzedazy.size() - 1; j++) {
                assertTrue(zleceniaSprzedazy.get(j).getLimitCeny() <= zleceniaSprzedazy.get(j + 1).getLimitCeny());
                if (zleceniaSprzedazy.get(j).getLimitCeny() == zleceniaSprzedazy.get(j + 1).getLimitCeny()) {
                    assertTrue(zleceniaSprzedazy.get(j).dodanePrzed(zleceniaSprzedazy.get(j + 1)) > 0);
                }
            }
        }
    }

    @Test
    @DisplayName("aktualnoscZlecen1")
    public void aktualnoscZlecen1() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane3.txt", "100"};
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        // przeprowadz kilka probnych tur
        for (int k = 0; k < 50; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaSprzedazy = system.pokazListZlecen("C", TypZlecenia.SPRZEDAZ);

            for (int j = 0; j < zleceniaSprzedazy.size(); j++) {
                if (zleceniaSprzedazy.get(j) instanceof ZlecenieDoNtej) {
                    assertTrue(((ZlecenieDoNtej) zleceniaSprzedazy.get(j)).getTerminWaznosci() >= system.getObecnaTura());
                }
            }
        }
    }

    @Test
    @DisplayName("aktualnoscZlecen2")
    public void aktualnoscZlecen2() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane1.txt", "100"};
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        // przeprowadz kilka probnych tur
        for (int k = 0; k < 50; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaSprzedazy = system.pokazListZlecen("GOOGL", TypZlecenia.KUPNO);

            for (int j = 0; j < zleceniaSprzedazy.size(); j++) {
                if (zleceniaSprzedazy.get(j) instanceof ZlecenieDoNtej) {
                    assertTrue(((ZlecenieDoNtej) zleceniaSprzedazy.get(j)).getTerminWaznosci() >= system.getObecnaTura());
                }
            }
        }
    }

    @Test
    @DisplayName("aktualnoscZlecen3")
    public void aktualnoscZlecen3() throws NiepoprawnyFormatDanych{
        String[] args = {"inputs/danePoprawne/dane4.txt", "100"};
        SystemTransakcji system = SystemTransakcji.utworzNowySytem(args);

        // przeprowadz kilka probnych tur
        for (int k = 0; k < 50; k++) {
            system.przeprowadzTure();

            List<Zlecenie> zleceniaSprzedazy = system.pokazListZlecen("NVIDI", TypZlecenia.KUPNO);

            for (int j = 0; j < zleceniaSprzedazy.size(); j++) {
                if (zleceniaSprzedazy.get(j) instanceof ZlecenieDoNtej) {
                    assertTrue(((ZlecenieDoNtej) zleceniaSprzedazy.get(j)).getTerminWaznosci() >= system.getObecnaTura());
                }
            }
        }
    }

    // Testy niepoprawnych danych wejsciowych
    @Test
    @DisplayName("brakInwestorow")
    public void odczytDanych1() {
        String[] args = {"inputs/niepoprawneDane/brakInwestorow.txt", "10"};
        Exception exception = assertThrows(NiepoprawnyFormatDanych.class, () -> { Main.main(args); });
    }

    @Test
    @DisplayName("niepoprawnaLiczbaArgumentow")
    public void zaMaloArgumentow() {
        String[] args = {"inputs/niepoprawneDane/brakInwestorow.txt"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        }
    }

    @Test
    @DisplayName("nieznanyInwestor")
    public void nieznanyInwestor() {
        String[] args = {"inputs/niepoprawneDane/nieznanyInwestor.txt", "10"};
        Exception exception = assertThrows(NiepoprawnyFormatDanych.class, () -> { Main.main(args); });
    }

    @Test
    @DisplayName("bladWFormacieSpolek")
    public void niepoprawnyFormat1() {
        String[] args = {"inputs/niepoprawneDane/niepoprawnyFormat1.txt", "101"};
        Exception exception = assertThrows(NiepoprawnyFormatDanych.class, () -> { Main.main(args); });
    }

    @Test
    @DisplayName("bladWFormaciePortfela")
    public void niepoprawnyFormat2() {
        String[] args = {"inputs/niepoprawneDane/niepoprawnyFormat2.txt", "101"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        };
    }

    @Test
    @DisplayName("niepoprawnaNazwaSpolki")
    public void niepoprawnaSpolka() {
        String[] args = {"inputs/niepoprawneDane/zbytDlugaNazwaSpolki.txt"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        }
    }

    @Test
    @DisplayName("niepoprawnaCenaSpolki")
    public void niepoprawnaCena() {
        String[] args = {"inputs/niepoprawneDane/ujemnaCena.txt"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        }
    }

    @Test
    @DisplayName("niepoprawnyPortfel")
    public void niepoprawnyPortfel() {
        String[] args = {"inputs/niepoprawneDane/niepoprawnyPortfel.txt"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        }
    }

    @Test
    @DisplayName("inneSpolkiWPortfelu")
    public void inneSpolkiWPortfelu() {
        String[] args = {"inputs/niepoprawneDane/inneSpolkiWPortfelu.txt"};
        try {
            Main.main(args);
        } catch (AssertionError e) {
            assertNotNull(e);
        } catch (NiepoprawnyFormatDanych e) {
            fail("Asercja powinna sie nie udac");
        }
    }
}
