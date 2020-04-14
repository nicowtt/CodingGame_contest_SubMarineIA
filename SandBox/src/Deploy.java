import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * to deploy in real water!
 */
class Deploy {
    private static int EMPTY = 0;
    private static int ISLAND = 1;

    public String deploy0(Board board) {
        int map[][] = board.getMap();

        if (map[13][13] == EMPTY) {
            return "13 13";
        }
        if (map[13][1] == EMPTY) {
            return "13 1";
        }
        if (map[7][13] == EMPTY) {
            return "7 13";
        }
        else {
            return "0 0";
        }
    }
}