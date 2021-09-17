package kpersistence;

import java.util.Random;

public class RandomId {

    private static final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);
    private static final char[] chars = new char[]{
            'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m',
            'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',
            '1','2','3','4','5','6','7','8','9','0'
    };

    private static final int idLength = 12;

    public static String next() {
        StringBuilder uid = new StringBuilder();

        for (int i = 0; i < idLength; i++) {
            uid.append(chars[(random.get().nextInt(62))]);
        }

        return uid.toString();
    }
}
