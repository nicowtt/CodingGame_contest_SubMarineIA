import java.util.List;

class Sector {

    private int id;
    private List<Cell> listCell;
    private Cell minCell;
    private Cell maxCell;

    // constructeur
    public Sector() {
    }

    public Sector(int id, List<Cell> listCell, Cell minCell, Cell maxCell) {
        this.id = id;
        this.listCell = listCell;
        this.minCell = minCell;
        this.maxCell = maxCell;
    }

    // getters ad setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public List<Cell> getListCell() {
        return listCell;
    }
    public void setListCell(List<Cell> listCell) {
        this.listCell = listCell;
    }
    public Cell getMinCell() {
        return minCell;
    }
    public void setMinCell(Cell minCell) {
        this.minCell = minCell;
    }
    public Cell getMaxCell() {
        return maxCell;
    }
    public void setMaxCell(Cell maxCell) {
        this.maxCell = maxCell;
    }

    // to string
    @Override
    public String toString() {
        return "Sector{" +
                "id=" + id +
                ", listCell=" + listCell +
                ", minCell=" + minCell +
                ", maxCell=" + maxCell +
                '}';
    }
}