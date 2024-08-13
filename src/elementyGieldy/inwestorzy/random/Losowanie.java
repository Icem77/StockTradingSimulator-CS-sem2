package elementyGieldy.inwestorzy.random;

import java.util.Random;

// klasa zapobiegajaca tworzeniu nowych obiektow klasy Random podczas losowan inwestorow
public class Losowanie {
    private static Random random = null;

    public static int losuj(int zakresDolny, int zakresGorny) {
        if (zakresDolny > zakresGorny) {
            return 0;
        } else {
            if (random == null) {
                Losowanie.random = new Random();
            }
            return random.nextInt(zakresGorny - zakresDolny + 1) + zakresDolny;
        }
    }
}
