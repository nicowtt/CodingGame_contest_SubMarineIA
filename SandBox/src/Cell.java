class Cell {
    private int x;
    private int y;
    private String cardinalPoint;
    private Integer distanceToDestinationCell;

    // constructor
    public Cell() {
    }

    public Cell(int x, int y, String cardinalPoint, Integer distanceToDestinationCell) {
        this.x = x;
        this.y = y;
        this.cardinalPoint = cardinalPoint;
        this.distanceToDestinationCell = distanceToDestinationCell;
    }

    // getters setters
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public String getCardinalPoint() {
        return cardinalPoint;
    }
    public void setCardinalPoint(String cardinalPoint) {
        this.cardinalPoint = cardinalPoint;
    }
    public Integer getDistanceToDestinationCell() {
        return distanceToDestinationCell;
    }
    public void setDistanceToDestinationCell(Integer distanceToDestinationCell) {
        this.distanceToDestinationCell = distanceToDestinationCell;
    }

    // to string
    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", cardinalPoint='" + cardinalPoint + '\'' +
                ", distanceToDestinationCell=" + distanceToDestinationCell +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Cell cell = (Cell) obj;
        return x == cell.getX() && y == cell.getY();
    }
}
