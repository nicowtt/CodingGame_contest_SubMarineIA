import java.util.Scanner;

import static java.lang.StrictMath.abs;

public class CodinGame {

    /**
     * Le binaire avec des 0 et des 1 c'est bien. Mais le binaire avec que des 0, ou presque, c'est encore mieux.
     * A l'origine, c'est un concept inventé par Chuck Norris pour envoyer des messages dits unaires.
     * <p>
     * Voici le principe d'encodage :
     * <p>
     * - Le message en entrée est constitué de caractères ASCII (7 bits)
     * - Le message encodé en sortie est constitué de blocs de 0
     * - Un bloc est séparé d'un autre bloc par un espace
     * - Deux blocs consécutifs servent à produire une série de bits de même valeur (que des 1 ou que des 0) :
     * - Premier bloc : il vaut toujours 0 ou 00. S'il vaut 0 la série contient des 1, sinon elle contient des 0
     * - Deuxième bloc : le nombre de 0 dans ce bloc correspond au nombre de bits dans la série
     */
    public void messageEncodeur() {
        Scanner in = new Scanner(System.in);
        String m = in.nextLine();
        char[] MESSAGE = m.toCharArray();
        StringBuilder binary = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        int count1 = 0;
        int count0 = 0;

        for (char c : MESSAGE) {
            String bin = Integer.toBinaryString(c);
            int miss0 = 7 - bin.length();
            StringBuilder temp = new StringBuilder(Integer.toBinaryString(c));
            temp.insert(0, "0".repeat(Math.max(0, miss0)));
            binary.append(temp);
        }

        char[] binaryA = binary.toString().toCharArray();

        for (char c : binaryA) {
            if (c == '1') {
                count1++;
                if (count0 > 0) {
                    answer.append("00 ");
                    answer.append("0".repeat(Math.max(0, count0)));
                    answer.append(" ");
                    count0 = 0;
                }
            } else {
                count0++;
                if (count1 > 0) {
                    answer.append("0 ");
                    answer.append("0".repeat(Math.max(0, count1)));
                    answer.append(" ");
                    count1 = 0;
                }
            }
        }

        if (count1 > 0) {
            answer.append("0 ");
            answer.append("0".repeat(Math.max(0, count1)));
        }
        if (count0 > 0) {
            answer.append("00 ");
            answer.append("0".repeat(Math.max(0, count0)));
        }

        System.out.println(answer);
    }

    public void batmanJump() {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int X0 = in.nextInt();
        int Y0 = in.nextInt();
        int minX = 0;
        int maxX = W;
        int minY = 0;
        int maxY = H;

        // game loop
        while (true) {
            String bombDir = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)

            for (char c : bombDir.toCharArray()){
                switch (c){
                    case 'U':
                        if (Y0 < maxY) maxY = Y0;
                        Y0 -= (maxY - minY) / 2;
                        if ((maxY - minY) % 2 != 0) Y0 --;
                        break;
                    case 'D':
                        if (Y0 > minY) minY = Y0;
                        Y0 += (maxY - minY) / 2;
                        if ((maxY - minY) % 2 != 0) Y0 ++;
                        break;
                    case 'R':
                        if (X0 > minX) minX = X0;
                        X0 += (maxX - minX) / 2;
                        if ((maxX - minX) % 2 != 0) X0 ++;
                        break;
                    case 'L':
                        if (X0 < maxX) maxX = X0;
                        X0 -= (maxX - minX) / 2;
                        if ((maxX - minX) % 2 != 0) X0 --;
                        break;
                }
            }

            // the location of the next window Batman should jump to.
            System.out.println(X0 + " " + Y0);
        }
    }
}
