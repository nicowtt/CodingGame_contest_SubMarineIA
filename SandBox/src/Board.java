
import java.util.Arrays;
import java.util.List;

class Board {
    private static int EMPTY = 0;
    private static int ISLAND = 1;
    private static int ALREADY_VISITED = 2;

    private int nbrCellX;
    private int nbrCellY;
    private int map[][];
    private List<Cell> listCellAlreadyVisited;
    private List<Sector> listSecteurs;

    // constructor
    public Board() {
    }

    public Board(int nbrCellX, int nbrCellY, int[][] map, List<Cell> listCellAlreadyVisited, List<Sector> listSecteurs) {
        this.nbrCellX = nbrCellX;
        this.nbrCellY = nbrCellY;
        this.map = new int[nbrCellX][nbrCellY];
        this.listCellAlreadyVisited = listCellAlreadyVisited;
        this.listSecteurs = listSecteurs;
    }

    // getters setters
    public int getNbrCellX() {
        return nbrCellX;
    }
    public void setNbrCellX(int nbrCellX) {
        this.nbrCellX = nbrCellX;
    }
    public int getNbrCellY() {
        return nbrCellY;
    }
    public void setNbrCellY(int nbrCellY) {
        this.nbrCellY = nbrCellY;
    }
    public List<Cell> getListCellAlreadyVisited() {
        return listCellAlreadyVisited;
    }
    public void setListCellAlreadyVisited(List<Cell> listCellAlreadyVisited) {
        this.listCellAlreadyVisited = listCellAlreadyVisited;
    }
    public List<Sector> getListSecteurs() {
        return listSecteurs;
    }
    public void setListSecteurs(List<Sector> listSecteurs) {
        this.listSecteurs = listSecteurs;
    }
    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    // to string
    @Override
    public String toString() {
        return "Board{" +
                "nbrCellX=" + nbrCellX +
                ", nbrCellY=" + nbrCellY +
                ", listCellAlreadyVisited=" + listCellAlreadyVisited +
                ", listSecteurs=" + listSecteurs +
                '}';
    }

    public void createBiDirectionalTable(List<String> stringMap) {
        for (int i = 0; i < stringMap.size(); i++) {
            for (int j = 0; j < stringMap.get(i).length(); j++) {
                map[i][j] = stringMap.get(j).charAt(i) == 'x' ? ISLAND : EMPTY;
                if (map[i][j] == ISLAND) {
                    System.err.println("ISLAND = " + i + j);
                }
            }
        }



    }
}