import sun.util.resources.ms.CalendarData_ms_MY;

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
    private Cell opponentCell;
    private int opponentSurfaceSector;

    private List<Cell> safeListOfCellAroundMe;
    private List<Cell> listTorpedoRange;
    private List<Cell> listOpponentPositionAfterTorpedo;
    private Cell opponentTorpedoExplosion;
    private List<Cell> torpedoFireList;
    private Cell myNextMove;
    private int destinationSector;
    private Cell myFireTorpedoCell;

    private Boolean loadedTorpedo;
    private Boolean loadedSonar;
    private Boolean loadedSilence;
    private Boolean isOpponentSendTorpedo;

    private Boolean canIfireTorpedoFollowingOppTorp;
    private Boolean canIfireFollowingTorpedoFeedback;
    private Boolean canIfireFollowingSonarFeedback;
    private Boolean canISendSonar;

    private Boolean moveNextOnSilence;
    private Boolean oppPresenceOnMySector;

    private String nextMoveString;

    private Boolean iFireOnPrecedentLoop;
    private Boolean listOppCellIncrease;

    private int nbrOfCellOnTorpedoFireList;
    private int possibilityOfCellsLeak;


    // constructor
    public Submarine() {
    }

    public Submarine(int id, int positionX, int positionY, int mySector, int life, int lifeLoopBefore, int torpedoCooldown, int sonarCooldown, int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders, Cell opponentCell, int opponentSurfaceSector, List<Cell> safeListOfCellAroundMe, List<Cell> listTorpedoRange, List<Cell> listOpponentPositionAfterTorpedo, Cell opponentTorpedoExplosion, List<Cell> torpedoFireList, Cell myNextMove, int destinationSector, Cell myFireTorpedoCell, Boolean loadedTorpedo, Boolean loadedSonar, Boolean loadedSilence, Boolean isOpponentSendTorpedo, Boolean canIfireTorpedoFollowingOppTorp, Boolean canIfireFollowingTorpedoFeedback, Boolean canIfireFollowingSonarFeedback, Boolean canISendSonar, Boolean moveNextOnSilence, Boolean oppPresenceOnMySector, String nextMoveString, Boolean iFireOnPrecedentLoop, Boolean listOppCellIncrease, int nbrOfCellOnTorpedoFireList, int possibilityOfCellsLeak) {
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
        this.opponentCell = opponentCell;
        this.opponentSurfaceSector = opponentSurfaceSector;
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
        this.listTorpedoRange = listTorpedoRange;
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
        this.torpedoFireList = torpedoFireList;
        this.myNextMove = myNextMove;
        this.destinationSector = destinationSector;
        this.myFireTorpedoCell = myFireTorpedoCell;
        this.loadedTorpedo = loadedTorpedo;
        this.loadedSonar = loadedSonar;
        this.loadedSilence = loadedSilence;
        this.isOpponentSendTorpedo = isOpponentSendTorpedo;
        this.canIfireTorpedoFollowingOppTorp = canIfireTorpedoFollowingOppTorp;
        this.canIfireFollowingTorpedoFeedback = canIfireFollowingTorpedoFeedback;
        this.canIfireFollowingSonarFeedback = canIfireFollowingSonarFeedback;
        this.canISendSonar = canISendSonar;
        this.moveNextOnSilence = moveNextOnSilence;
        this.oppPresenceOnMySector = oppPresenceOnMySector;
        this.nextMoveString = nextMoveString;
        this.iFireOnPrecedentLoop = iFireOnPrecedentLoop;
        this.listOppCellIncrease = listOppCellIncrease;
        this.nbrOfCellOnTorpedoFireList = nbrOfCellOnTorpedoFireList;
        this.possibilityOfCellsLeak = possibilityOfCellsLeak;
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
    public Cell getMyFireTorpedoCell() {
        return myFireTorpedoCell;
    }
    public void setMyFireTorpedoCell(Cell myFireTorpedoCell) {
        this.myFireTorpedoCell = myFireTorpedoCell;
    }
    public Cell getOpponentCell() {
        return opponentCell;
    }
    public void setOpponentCell(Cell opponentCell) {
        this.opponentCell = opponentCell;
    }
    public Boolean getLoadedTorpedo() {
        return loadedTorpedo;
    }
    public void setLoadedTorpedo(Boolean loadedTorpedo) {
        this.loadedTorpedo = loadedTorpedo;
    }
    public Boolean getLoadedSonar() {
        return loadedSonar;
    }
    public void setLoadedSonar(Boolean loadedSonar) {
        this.loadedSonar = loadedSonar;
    }
    public Boolean getLoadedSilence() {
        return loadedSilence;
    }
    public void setLoadedSilence(Boolean loadedSilence) {
        this.loadedSilence = loadedSilence;
    }
    public Boolean getOpponentSendTorpedo() {
        return isOpponentSendTorpedo;
    }
    public void setOpponentSendTorpedo(Boolean opponentSendTorpedo) {
        isOpponentSendTorpedo = opponentSendTorpedo;
    }
    public Boolean getCanIfireTorpedoFollowingOppTorp() {
        return canIfireTorpedoFollowingOppTorp;
    }
    public void setCanIfireTorpedoFollowingOppTorp(Boolean canIfireTorpedoFollowingOppTorp) {
        this.canIfireTorpedoFollowingOppTorp = canIfireTorpedoFollowingOppTorp;
    }
    public Boolean getCanIfireFollowingTorpedoFeedback() {
        return canIfireFollowingTorpedoFeedback;
    }
    public void setCanIfireFollowingTorpedoFeedback(Boolean canIfireFollowingTorpedoFeedback) {
        this.canIfireFollowingTorpedoFeedback = canIfireFollowingTorpedoFeedback;
    }
    public Boolean getCanISendSonar() {
        return canISendSonar;
    }
    public void setCanISendSonar(Boolean canISendSonar) {
        this.canISendSonar = canISendSonar;
    }
    public Boolean getCanIfireFollowingSonarFeedback() {
        return canIfireFollowingSonarFeedback;
    }
    public void setCanIfireFollowingSonarFeedback(Boolean canIfireFollowingSonarFeedback) {
        this.canIfireFollowingSonarFeedback = canIfireFollowingSonarFeedback;
    }
    public Boolean getMoveNextOnSilence() {
        return moveNextOnSilence;
    }
    public void setMoveNextOnSilence(Boolean moveNextOnSilence) {
        this.moveNextOnSilence = moveNextOnSilence;
    }
    public Boolean getOppPresenceOnMySector() {
        return oppPresenceOnMySector;
    }
    public void setOppPresenceOnMySector(Boolean oppPresenceOnMySector) {
        this.oppPresenceOnMySector = oppPresenceOnMySector;
    }
    public int getOpponentSurfaceSector() {
        return opponentSurfaceSector;
    }
    public void setOpponentSurfaceSector(int opponentSurfaceSector) {
        this.opponentSurfaceSector = opponentSurfaceSector;
    }
    public String getNextMoveString() {
        return nextMoveString;
    }
    public void setNextMoveString(String nextMoveString) {
        this.nextMoveString = nextMoveString;
    }
    public Boolean getiFireOnPrecedentLoop() {
        return iFireOnPrecedentLoop;
    }
    public void setiFireOnPrecedentLoop(Boolean iFireOnPrecedentLoop) {
        this.iFireOnPrecedentLoop = iFireOnPrecedentLoop;
    }
    public Boolean getListOppCellIncrease() {
        return listOppCellIncrease;
    }
    public void setListOppCellIncrease(Boolean listOppCellIncrease) {
        this.listOppCellIncrease = listOppCellIncrease;
    }
    public int getNbrOfCellOnTorpedoFireList() {
        return nbrOfCellOnTorpedoFireList;
    }
    public void setNbrOfCellOnTorpedoFireList(int nbrOfCellOnTorpedoFireList) {
        this.nbrOfCellOnTorpedoFireList = nbrOfCellOnTorpedoFireList;
    }
    public int getPossibilityOfCellsLeak() {
        return possibilityOfCellsLeak;
    }
    public void setPossibilityOfCellsLeak(int possibilityOfCellsLeak) {
        this.possibilityOfCellsLeak = possibilityOfCellsLeak;
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
                ", opponentCell=" + opponentCell +
                ", safeListOfCellAroundMe=" + safeListOfCellAroundMe +
                ", listTorpedoRange=" + listTorpedoRange +
                ", listOpponentPositionAfterTorpedo=" + listOpponentPositionAfterTorpedo +
                ", opponentTorpedoExplosion=" + opponentTorpedoExplosion +
                ", torpedoFireList=" + torpedoFireList +
                ", myNextMove=" + myNextMove +
                ", destinationSector=" + destinationSector +
                ", myFireTorpedoCell=" + myFireTorpedoCell +
                ", loadedTorpedo=" + loadedTorpedo +
                ", loadedSonar=" + loadedSonar +
                ", loadedSilence=" + loadedSilence +
                ", isOpponentSendTorpedo=" + isOpponentSendTorpedo +
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

    public void startInitialState(Submarine mySubmarine, Board board) {
        mySubmarine.setLoadedTorpedo(false);
        mySubmarine.setLoadedSonar(false);
        mySubmarine.setLoadedSilence(false);
        mySubmarine.setOpponentSendTorpedo(false);
        mySubmarine.setiFireOnPrecedentLoop(false);
        mySubmarine.setListOppCellIncrease(false);
    }

    public void startInitialStateGameLoop(Submarine mySubmarine) {
        mySubmarine.canIfireTorpedoFollowingOppTorp = false;
        mySubmarine.canIfireFollowingTorpedoFeedback = false;
        mySubmarine.canISendSonar = false;
        mySubmarine.canIfireFollowingSonarFeedback = false;
        mySubmarine.moveNextOnSilence = false;
        mySubmarine.oppPresenceOnMySector = false;
    }

    public void check(Submarine mySubmarine, Submarine opponentSubmarine, Board board) {
        Utils utils = new Utils();
        Torpedo torpedo = new Torpedo();

        // sonar feedback
        System.err.println("retour sonar: " + mySubmarine.getSonarResult());

        // mySector position
        mySubmarine.setMySector(utils.findMyPositionSector(mySubmarine, board));

        // check if i can fire torpedo following my position (my torpedo loaded and opponent locate list)
        if (mySubmarine.getTorpedoCooldown() == 0 && mySubmarine.getListOpponentPositionAfterTorpedo() != null) {
            torpedo.canIFireTorpedo(mySubmarine, board);
        }
        // check if my torpedo is loaded
        if (mySubmarine.getTorpedoCooldown() == 0) {
            mySubmarine.setLoadedTorpedo(true);
            // check
            System.err.println("Torpedo loaded and list range ok");
        }
        // check if my sonar is loaded
        if (mySubmarine.getSonarCooldown() == 0) {
            mySubmarine.setLoadedSonar(true);
            //check
            System.err.println("Sonar loaded");
        }
        // check if silence is loaded
        if (mySubmarine.getSilenceCooldown() == 0) {
            mySubmarine.setLoadedSilence(true);
            //check
            System.err.println("Silence loaded");
            // lunch sonar in my sector
            if (mySubmarine.getSonarCooldown() == 0) {
                mySubmarine.setCanISendSonar(true);
                //check
                System.err.println("sonar can sent!");
            }
        }
        // add torpedo order if possible
        if (mySubmarine.getCanIfireTorpedoFollowingOppTorp() && mySubmarine.getLoadedTorpedo()) {
            mySubmarine.setCanIfireFollowingTorpedoFeedback(true);
            mySubmarine.setLoadedTorpedo(false);
        }

        // if mySubmarine life increase 2 point -> next move to silence if possible
        int lifeDown = utils.compareLifeLoopBefore(mySubmarine);
        // check
            System.err.println("my life down : " + lifeDown);
        if ((lifeDown == 2) && mySubmarine.getLoadedSilence()) {
            // next move to silence
            mySubmarine.setMoveNextOnSilence(true);
        }

        // if opponent life increase 2 point -> lock his position
        int oppLifeDown = utils.compareLifeOfOpponent(opponentSubmarine);

        // check if opponent is in my  sector with sonar feedback
        if (mySubmarine.getSonarResult().equals("Y")) {
            mySubmarine.setCanIfireFollowingSonarFeedback(true);
        }

        // check countNbrOfCellOnTorpedoFireList
        setNbrOfCellOnTorpedoFireList(countNbrOfCellOnTorpedoFireList());
    }

    public int countNbrOfCellOnTorpedoFireList() {
        if (getTorpedoFireList() != null ) {
            long nbr = getTorpedoFireList().stream().count();
            // check
            System.err.println("nbr of cell torpedo fire list: " + nbr);
            return (int)nbr;
        } else {
            return 0;
        }

    }
}