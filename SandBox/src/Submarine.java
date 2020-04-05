import java.util.List;

class Submarine {
    private int id;
    private int positionX;
    private int positionY;
    private int mySector;
    private int life;
    private int lifeLoopBefore;
    private int torpedoCooldown;
    private int sonarCooldown;
    private int silenceCooldown;
    private int mineCooldown;
    private String sonarResult;
    private String opponentOrders;
    private List<Cell> safeListOfCellAroundMe;
    private List<Cell> listTorpedoRange;
    private List<Cell> listOpponentPositionAfterTorpedo;
    private Cell opponentTorpedoExplosion;
    private List<Cell> torpedoFireList;
    private Cell myNextMove;
    private int destinationSector;


    // constructor
    public Submarine() {
    }

    public Submarine(int id, int positionX, int positionY, int mySector, int life, int lifeLoopBefore, int torpedoCooldown, int sonarCooldown, int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders, List<Cell> safeListOfCellAroundMe, List<Cell> listTorpedoRange, List<Cell> listOpponentPositionAfterTorpedo, Cell opponentTorpedoExplosion, List<Cell> torpedoFireList, Cell myNextMove, int destinationSector) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.mySector = mySector;
        this.life = life;
        this.lifeLoopBefore = lifeLoopBefore;
        this.torpedoCooldown = torpedoCooldown;
        this.sonarCooldown = sonarCooldown;
        this.silenceCooldown = silenceCooldown;
        this.mineCooldown = mineCooldown;
        this.sonarResult = sonarResult;
        this.opponentOrders = opponentOrders;
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
        this.listTorpedoRange = listTorpedoRange;
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
        this.torpedoFireList = torpedoFireList;
        this.myNextMove = myNextMove;
        this.destinationSector = destinationSector;
    }

    // getters setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPositionX() {
        return positionX;
    }
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }
    public int getPositionY() {
        return positionY;
    }
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
    public int getMySector() {
        return mySector;
    }
    public void setMySector(int mySector) {
        this.mySector = mySector;
    }
    public int getLife() {
        return life;
    }
    public void setLife(int life) {
        this.life = life;
    }
    public int getLifeLoopBefore() {
        return lifeLoopBefore;
    }
    public void setLifeLoopBefore(int lifeLoopBefore) {
        this.lifeLoopBefore = lifeLoopBefore;
    }
    public int getTorpedoCooldown() {
        return torpedoCooldown;
    }
    public void setTorpedoCooldown(int torpedoCooldown) {
        this.torpedoCooldown = torpedoCooldown;
    }
    public int getSonarCooldown() {
        return sonarCooldown;
    }
    public void setSonarCooldown(int sonarCooldown) {
        this.sonarCooldown = sonarCooldown;
    }
    public int getSilenceCooldown() {
        return silenceCooldown;
    }
    public void setSilenceCooldown(int silenceCooldown) {
        this.silenceCooldown = silenceCooldown;
    }
    public int getMineCooldown() {
        return mineCooldown;
    }
    public void setMineCooldown(int mineCooldown) {
        this.mineCooldown = mineCooldown;
    }
    public String getSonarResult() {
        return sonarResult;
    }
    public void setSonarResult(String sonarResult) {
        this.sonarResult = sonarResult;
    }
    public String getOpponentOrders() {
        return opponentOrders;
    }
    public void setOpponentOrders(String opponentOrders) {
        this.opponentOrders = opponentOrders;
    }
    public List<Cell> getSafeListOfCellAroundMe() {
        return safeListOfCellAroundMe;
    }
    public void setSafeListOfCellAroundMe(List<Cell> safeListOfCellAroundMe) {
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
    }
    public List<Cell> getListTorpedoRange() {
        return listTorpedoRange;
    }
    public void setListTorpedoRange(List<Cell> listTorpedoRange) {
        this.listTorpedoRange = listTorpedoRange;
    }
    public List<Cell> getListOpponentPositionAfterTorpedo() {
        return listOpponentPositionAfterTorpedo;
    }
    public void setListOpponentPositionAfterTorpedo(List<Cell> listOpponentPositionAfterTorpedo) {
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
    }
    public Cell getOpponentTorpedoExplosion() {
        return opponentTorpedoExplosion;
    }
    public void setOpponentTorpedoExplosion(Cell opponentTorpedoExplosion) {
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
    }
    public List<Cell> getTorpedoFireList() {
        return torpedoFireList;
    }
    public void setTorpedoFireList(List<Cell> torpedoFireList) {
        this.torpedoFireList = torpedoFireList;
    }
    public Cell getMyNextMove() {
        return myNextMove;
    }
    public void setMyNextMove(Cell myNextMove) {
        this.myNextMove = myNextMove;
    }
    public int getDestinationSector() {
        return destinationSector;
    }
    public void setDestinationSector(int destinationSector) {
        this.destinationSector = destinationSector;
    }

    // to string
    @Override
    public String toString() {
        return "Submarine{" +
                "id=" + id +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", mySector=" + mySector +
                ", life=" + life +
                ", lifeLoopBefore=" + lifeLoopBefore +
                ", torpedoCooldown=" + torpedoCooldown +
                ", sonarCooldown=" + sonarCooldown +
                ", silenceCooldown=" + silenceCooldown +
                ", mineCooldown=" + mineCooldown +
                ", sonarResult='" + sonarResult + '\'' +
                ", opponentOrders='" + opponentOrders + '\'' +
                ", safeListOfCellAroundMe=" + safeListOfCellAroundMe +
                ", listTorpedoRange=" + listTorpedoRange +
                ", listOpponentPositionAfterTorpedo=" + listOpponentPositionAfterTorpedo +
                ", opponentTorpedoExplosion=" + opponentTorpedoExplosion +
                ", torpedoFireList=" + torpedoFireList +
                ", myNextMove=" + myNextMove +
                ", destinationSector=" + destinationSector +
                '}';
    }

    public void updateSubmarine(int id, int positionX, int positionY, int life, int torpedoCooldown,
                                int sonarCooldown, int silenceCooldown, int mineCooldown,
                                String sonarResult, String opponentOrders ) {
        this.setId(id);
        this.setPositionX(positionX);
        this.setPositionY(positionY);
        this.setLife(life);
        this.setTorpedoCooldown(torpedoCooldown);
        this.setSonarCooldown(sonarCooldown);
        this.setSilenceCooldown(silenceCooldown);
        this.setMineCooldown(mineCooldown);
        this.setSonarResult(sonarResult);
        this.setOpponentOrders(opponentOrders);
    }
}