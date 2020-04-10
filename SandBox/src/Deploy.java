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

        if (map[14][14] == EMPTY) {
            return "14 14";
        }
        if (map[14][0] == EMPTY) {
            return "14 0";
        }
        if (map[0][0] == EMPTY) {
            return "7 14";
        }
        else {
            return "0 0";
        }
    }
}