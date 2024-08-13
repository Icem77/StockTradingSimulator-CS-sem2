package systemTransakcji;

import elementyGieldy.inwestorzy.Inwestor;
import elementyGieldy.inwestorzy.random.InwestorRANDOM;
import elementyGieldy.inwestorzy.sma.InwestorSMA;
import elementyGieldy.zlecenia.TypZlecenia;
import elementyGieldy.zlecenia.Zlecenie;
import elementyGieldy.zlecenia.ZlecenieDoNtej;
import systemTransakcji.wyjatki.NiepoprawnyFormatDanych;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SystemTransakcji {
    private List<Inwestor> inwestorzy;
    private Map<String, DaneGieldoweSpolki> spolki;
    int dlugoscSymulacji;
    int obecnaTura;
    int zleceniaWTurze;

    public SystemTransakcji(List<Inwestor> inwestorzy, Map<String, DaneGieldoweSpolki> spolki, int dlugoscSymulacji) {
        this.inwestorzy = inwestorzy;
        this.spolki = spolki;
        this.dlugoscSymulacji = dlugoscSymulacji;
        this.obecnaTura = 0;
        this.zleceniaWTurze = 0;
    }

    public int getCenaRynkowa(String idSpolki) {
        assert spolki.containsKey(idSpolki) : "Zapytanie o cene nienotowanej spolki";
        return spolki.get(idSpolki).getCenaRynkowa();
    }

    public int getDlugoscSymulacji() {
        return dlugoscSymulacji;
    }

    public int getObecnaTura() {
        return obecnaTura;
    }

    public int getZleceniaWTurze() {
        return zleceniaWTurze++;
    }

    public void rozpocznijSymulacje() {
        // dla kazdej tury symulacji
        while (obecnaTura < dlugoscSymulacji) {
           przeprowadzTure();
        }
        wypiszStanKoncowy();
    }

    public void przeprowadzTure() {
        assert obecnaTura < dlugoscSymulacji : "Symulacja przekroczyla czas trwania";
        System.out.println("**************************************************");
        System.out.println("TURA " + obecnaTura);
        System.out.println("**************************************************");

        zbierzDecyzjeInwestorow();

        przetworzZlecenia();

        sprawdzTerminyZlecen();

        obecnaTura++;
        zleceniaWTurze = 0;
    }

    public List<Zlecenie> pokazListZlecen(String idSpolki, TypZlecenia typZlecenia) {
        assert spolki.containsKey(idSpolki) : "Spolka nie jest notowana.";

        // udostepniamy na zewnatrz kopie listy zlecen w celu testowania programu
        if (typZlecenia.equals(TypZlecenia.KUPNO)) {
            return new ArrayList<>(spolki.get(idSpolki).getZleceniaKupna());
        } else {
            return new ArrayList<>(spolki.get(idSpolki).getZleceniaSprzedazy());
        }
    }

    private void przetworzZlecenia() {
        System.out.println("**************************************************");
        System.out.println("Rozpoczyna się przetwarzanie zleceń...");

        // dla kazdej notowanej spolki
        for (DaneGieldoweSpolki daneSpolki : spolki.values()) {

            List<Zlecenie> zleceniaKupna = daneSpolki.getZleceniaKupna();
            List<Zlecenie> zleceniaSprzedazy = daneSpolki.getZleceniaSprzedazy();

            // dla kazdego zlecenia zakupu spolki od zlecenia o najwyzszej cenie i najwczesniejszej dacie dodania
            for (int j = zleceniaKupna.size() - 1; j >= 0; j--) {

                Zlecenie zlecenieKupna = zleceniaKupna.get(j);

                // sprawdzamy czy kupujacego nadal stac na realizacje tego zlecenia
                if (zlecenieKupna.getWlasciciel().getSaldo() >=
                        zlecenieKupna.getLimitCeny() * zlecenieKupna.getWolumen()) {

                    // przechodzimy przez zlecenia sprzedazy od najnizszej ceny i najwczesniejszego dodania
                    for (int i = 0; i < zleceniaSprzedazy.size() && zlecenieKupna.getWolumen() > 0; i++) {
                        Zlecenie zlecenieSprzedazy = zleceniaSprzedazy.get(i);

                        // zapobiegamy transakcji z samym sobą
                        if (!zlecenieSprzedazy.getWlasciciel().equals(zlecenieKupna.getWlasciciel())) {

                            // sprawdzamy czy sprzedajacy posiada wystarczajaca liczbe akcji
                            if (zlecenieSprzedazy.getWlasciciel().getIloscPosiadanychAkcji(zlecenieKupna.getIdSpolki())
                                    < zlecenieSprzedazy.getWolumen()) {

                                // usuwamy nieaktualne zlecenie
                                System.out.println("Zlecenie " + zlecenieSprzedazy + " zostaje usuniete jako nieaktualne.");
                                zleceniaSprzedazy.remove(zlecenieSprzedazy);
                                i--;

                            } else { // oba zlecenia sa poprawne

                                // sprawdzamy czy cena zakupu jest rowna conajmniej cenie sprzedazy
                                if (zlecenieKupna.getLimitCeny() >= zleceniaSprzedazy.get(i).getLimitCeny()) {

                                    // ustalamy cene transakcji jako limit wczesniejszego zlecenia
                                    int cenaTransakcji;
                                    if (zlecenieKupna.dodanePrzed(zlecenieSprzedazy) > 0) {
                                        cenaTransakcji = zlecenieKupna.getLimitCeny();
                                    } else {
                                        cenaTransakcji = zlecenieSprzedazy.getLimitCeny();
                                    }

                                    // ustalamy wolumen transakcji jako minimimum sposrod wolumenow zlecen
                                    int wolumenTransakcji = Math.min(zlecenieKupna.getWolumen(),
                                            zlecenieSprzedazy.getWolumen());

                                    // Ponizszy warunek logiczny to zawsze prawda w przypadku pominiecia typu zlecen WA
                                    // jednak warto dodac to sprawdzenie na potrzebe przyszlego rozwoju projektu
                                    if (zlecenieKupna instanceof ZlecenieDoNtej &&
                                            zlecenieSprzedazy instanceof ZlecenieDoNtej) {

                                        System.out.print("Zlecenia " + zlecenieKupna + " i " + zlecenieSprzedazy +
                                                " zostaja dopasowane! ");
                                        System.out.println(" (Wymiana " + wolumenTransakcji +
                                                " akcji spolki " + zlecenieKupna.getIdSpolki() + " po cenie " +
                                                cenaTransakcji + ")");

                                        // realizujemy zlecenia
                                        ((ZlecenieDoNtej) zlecenieKupna).realizujCzescZlecenia(wolumenTransakcji,
                                                cenaTransakcji);
                                        ((ZlecenieDoNtej) zlecenieSprzedazy).realizujCzescZlecenia(wolumenTransakcji,
                                                cenaTransakcji);

                                        zaktualizujCeneRynkowa(zlecenieKupna.getIdSpolki(), cenaTransakcji);
                                    }

                                    // sprawdzamy ktore ze zlecen zostalo zrealizowane w calosci
                                    if (zlecenieSprzedazy.getWolumen() == 0) {
                                        System.out.println("Zlecenie " + zlecenieSprzedazy +
                                                " zostaje usuniete jako zrealizowane!");
                                        zleceniaSprzedazy.remove(zlecenieSprzedazy);
                                    }
                                    if (zlecenieKupna.getWolumen() == 0) {
                                        System.out.println("Zlecenie " + zlecenieKupna +
                                                " zostaje usuniete jako zrealizowane!");
                                        zleceniaKupna.remove(zlecenieKupna);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Zlecenie " + zlecenieKupna + " zostaje usuniete jako nieaktualne.");
                    zleceniaKupna.remove(zlecenieKupna);
                }
            }
        }
    }

    private void zaktualizujCeneRynkowa(String idSpolki, int nowaCena) {
        spolki.get(idSpolki).setCenaRynkowa(nowaCena);
    }

    private void sprawdzTerminyZlecen() {
        for (DaneGieldoweSpolki daneSpolki : spolki.values()) {

            List<Zlecenie> zleceniaKupna = daneSpolki.getZleceniaKupna();
            List<Zlecenie> zleceniaSprzedazy = daneSpolki.getZleceniaSprzedazy();

            // sprawdz terminy zlecen kupna
            for (int i = zleceniaKupna.size() - 1; i >= 0; i--) {

                // w przypadku pominiecia zlecenia WA to jest zawsze prawda
                // jednak warto dodac to sprawdzenie dla przyszlego rozwoju projektu
                if (zleceniaKupna.get(i) instanceof ZlecenieDoNtej) {
                    if (((ZlecenieDoNtej) zleceniaKupna.get(i)).getTerminWaznosci() <= obecnaTura) {
                        System.out.println("Zlecenie " + zleceniaKupna.get(i) + " zostaje usuniete ze wzgledu na termin.");
                        zleceniaKupna.remove(zleceniaKupna.get(i));
                    }
                }
            }

            // sprawdz terminy zlecen sprzedazy
            for (int i = zleceniaSprzedazy.size() - 1; i >= 0; i--) {
                // w przypadku pominiecia zlecenia WA to jest zawsze prawda
                // jednak warto dodac to sprawdzenie dla przyszlego rozwoju projektu
                if (zleceniaSprzedazy.get(i) instanceof ZlecenieDoNtej) {
                    if (((ZlecenieDoNtej) zleceniaSprzedazy.get(i)).getTerminWaznosci() <= obecnaTura) {
                        System.out.println("Zlecenie " + zleceniaSprzedazy.get(i) + " zostaje usuniete ze wzgledu na termin.");
                        zleceniaSprzedazy.remove(zleceniaSprzedazy.get(i));
                    }
                }
            }

        }
    }
    private void zweryfikujZlecenie(Zlecenie zlecenie) {
        if (zlecenie == null) {
            System.out.println("jednak powstrzymuje sie od zlozenia zlecenia w tej turze.");
        } else {
            System.out.println("i sklada nastepujace zlecenie: ");
            zlecenie.pokazOpis();

            // sprawdzamy poprawnosc parametrow zlecenia
            if (zlecenie.getWolumen() > 0 && zlecenie.getLimitCeny() > 0 &&
                    Math.abs(zlecenie.getLimitCeny() - spolki.get(zlecenie.getIdSpolki()).getCenaRynkowa()) <= 10) {

                if (zlecenie instanceof ZlecenieDoNtej) {
                    if (((ZlecenieDoNtej) zlecenie).getTerminWaznosci() < obecnaTura) {
                        System.out.println("jednak zlozone zlecenie zawiera niepoprawne dane i jest ignorowane.");
                        return;
                    }
                }

                Inwestor inwestor = zlecenie.getWlasciciel();

                // analizujemy srodki inwestora
                switch(zlecenie.getTyp()) {

                    // ilosc akcji w przypadku sprzedazy
                    case SPRZEDAZ:
                        if (zlecenie.getWolumen() <= inwestor.getIloscPosiadanychAkcji(zlecenie.getIdSpolki())) {
                            spolki.get(zlecenie.getIdSpolki()).dodajZlecenie(zlecenie);
                        } else {
                            System.out.println("ktore zostaje odrzucone z powodu" +
                                    " niewystarczajacej liczby akcji w portfelu.");
                        }
                        break;
                    // saldo konta w przypadku zakupu
                    case KUPNO:
                        int lacznyKosztZlecenia = zlecenie.getLimitCeny() * zlecenie.getWolumen();
                        if (lacznyKosztZlecenia <= inwestor.getSaldo()) {
                            spolki.get(zlecenie.getIdSpolki()).dodajZlecenie(zlecenie);
                        } else {
                            System.out.println(" ktore zostaje odrzucone z powodu" +
                                    " niewystarczającej ilości srodkow na koncie.");
                        }
                        break;
                }
            } else {
                System.out.println("jednak zlozone zlecenie zawiera niepoprawne dane i jest ignorowane.");
            }
        }
    }

    private void zbierzDecyzjeInwestorow() {
        System.out.println("Rozpoczyna się zbieranie zleceń...");
        System.out.println("**************************************************");

        // zmieniamy kolejnosc inwestorow w liscie
        Collections.shuffle(inwestorzy);

        // dla kazdego inwestora
        for (Inwestor inwestor : inwestorzy) {
            System.out.print("-> Inwestor " + inwestor + " zostaje poproszony o podjecie decyzji ");

            // zbierz decyzje inwestora
            Zlecenie zlecenie = inwestor.podejmijDecyzje(this);

            // analizuj poprawnosc zlecenia
            zweryfikujZlecenie(zlecenie);
        }
    }

    public static SystemTransakcji utworzNowySytem(String[] args) throws NiepoprawnyFormatDanych {
        assert args.length == 2 : "Niepoprawna liczba parametrow programu";

        Scanner inputReader = null;
        try {
            File input = new File(args[0]);
            inputReader = new Scanner(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputReader != null) {
            List<Inwestor> inwestorzy = new ArrayList<>();
            Map<String, DaneGieldoweSpolki> spolki = new HashMap<>();
            Map<String, Integer> portfel = new HashMap<>();
            int saldo = 0;

            for (int i = 0; i < 3 && inputReader.hasNextLine(); i++) {

                // pomijamy linijki komentarzy
                String nextLine = "#";
                while (inputReader.hasNextLine() && nextLine.startsWith("#")) {
                    nextLine = inputReader.nextLine();
                }

                String[] oddzieloneDane = nextLine.split("\\s+");

                switch (i) {
                    case 0:
                        // wczytanie typow inwestorow
                        for (String typ : oddzieloneDane) {
                            switch (typ) {
                                case "R":
                                    inwestorzy.add(new InwestorRANDOM());
                                    break;
                                case "S":
                                    inwestorzy.add(new InwestorSMA());
                                    break;
                                default:
                                    throw new NiepoprawnyFormatDanych("Nieznany typ lub brak inwestora");
                            }
                        }
                        break;
                    case 1:
                        // wczytanie spolek i ich cen poczatkowych
                        for (String akcja : oddzieloneDane) {
                            String[] IDiCena = akcja.split(":");

                            // sprawdzamy czy podano akcje w formacie 'AKCJA:CENA'
                            try {
                                String test = IDiCena[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                throw new NiepoprawnyFormatDanych("Format danych nie zgadza sie z poleceniem");
                            }

                            // sprawdzenie poprawnosci danych
                            assert (!IDiCena[0].equals("") && IDiCena[0].length() <= 5) : "Niepoprawna nazwa spolki";
                            assert (!spolki.containsKey(IDiCena[0])) : "Dwukrotnie podano te sama spolke";
                            try {
                                assert (Integer.parseInt(IDiCena[1]) > 0) : "Niepoprawna cena akcji";
                            } catch (NumberFormatException e) {
                                throw new NiepoprawnyFormatDanych("Ilosc akcji w portfelu nie jest liczba");
                            }

                            // dodanie nowej spolki na gielde
                            DaneGieldoweSpolki daneSpolki = new DaneGieldoweSpolki(
                                    Integer.parseInt(IDiCena[1]), new ArrayList<>(), new ArrayList<>());
                            spolki.put(IDiCena[0], daneSpolki);
                        }
                        break;
                    case 2:
                        // sprawdzamy czy zostalo podane saldo poczatkowe
                        try {
                            saldo = Integer.parseInt(oddzieloneDane[0]);
                        } catch (NumberFormatException e) {
                            throw new NiepoprawnyFormatDanych("Bledny stan poczatkowy portfela");
                        }

                        assert saldo >= 0 : "Niepoprawne saldo poczatkowe";

                        for (int j = 1; j < oddzieloneDane.length; j++) {
                            String[] IDiIlosc = oddzieloneDane[j].split(":");

                            // sprawdzamy czy podano akcje w formacie 'AKCJA:CENA'
                            try {
                                String test = IDiIlosc[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                throw new NiepoprawnyFormatDanych("Format stanu portfela nie " +
                                        "zgadza sie z poleceniem");
                            }

                            // sprawdzenie poprawnosci danych
                            assert (!IDiIlosc[0].equals("") && IDiIlosc[0].length() <= 5) : "Niepoprawna nazwa spolki";
                            assert (!portfel.containsKey(IDiIlosc[0])) : "Dwukrotnie podano te sama spolke";
                            try {
                                assert (Integer.parseInt(IDiIlosc[1]) >= 0) : "Niepoprawna ilosc akcji w portfelu";
                            } catch (NumberFormatException e) {
                                throw new NiepoprawnyFormatDanych("Ilosc akcji w portfelu nie jest liczba");
                            }

                            // dodanie nowej spolki do portfela poczatkowego
                            portfel.put(IDiIlosc[0], Integer.parseInt(IDiIlosc[1]));
                        }

                        // sprawdz czy spolki w portfelu zgadzaja sie z notowanymi na gieldzie
                        assert portfel.keySet().equals(spolki.keySet()) : "Notowane spolki nie zgadzaja sie z " +
                                                                        "wystepujacymi w portfelu";
                        break;
                }

            }

            // ustaw stan poczatkowy portfela i konta kazdego inwestora
            for (Inwestor inwestor : inwestorzy) {
                inwestor.przydzielPortfel(new HashMap<>(portfel));
                inwestor.zmienSaldo(saldo);
            }

            // utworz system transakcji na podstawie wczytanych danych
            return new SystemTransakcji(inwestorzy, spolki, Integer.parseInt(args[1]));
        }
        return null;
    }

    public int obliczAkcjeWObrocie(String idSPolki) {
        assert spolki.containsKey(idSPolki) : "Podana spolka nie jest notowana";
        int sumaAkcji = 0;

        for (Inwestor inwestor : inwestorzy) {
            sumaAkcji += inwestor.getIloscPosiadanychAkcji(idSPolki);
        }

        return sumaAkcji;
    }

    public int obliczPieniadzWObrocie() {
        int sumaPieniedzy = 0;
        for (Inwestor inwestor : inwestorzy) {
            sumaPieniedzy += inwestor.getSaldo();
        }

        return sumaPieniedzy;
    }

    private void wypiszStanKoncowy() {
        System.out.println("**************************************************");
        System.out.println("STAN KONCOWY SYMULACJI");
        System.out.println("**************************************************");
        for (Inwestor inwestor : inwestorzy) {
            inwestor.wypiszStanKonta(this);
        }
    }

}
