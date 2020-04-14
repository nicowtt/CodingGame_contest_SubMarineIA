import java.util.ArrayList;
import java.util.List;

class Torpedo {
    Utils utils = new Utils();

    /**
     * Create cell list of my torpedo range or opponent case range (when he has send torpedo)
     * removing s and y (my position or explosion position)
     * @param x myPositionX or explosedOponentTorpedoX
     * @param y myPositionY or explosedOponentTorpedoY
     * @param board
     * @return
     */
    public List<Cell> createCellListTorpedoRange(int x, int y, Board board) {
        List<Cell> listCellTorpedoRange = new ArrayList<>();
        int rangeMinX = -4;
        int rangeMaxX = 4;
        int centerCellY = y;
        int countChangeSide = 0;
        //down and up triangle
        for (int j = 1; j <= 11; j++) {
            // add line
            for (int i = rangeMinX; i <= rangeMaxX ; i++) {
                // create centerCell
                Cell leftEndCell = new Cell(x + i, centerCellY, null,null);
                listCellTorpedoRange.add(leftEndCell);
                countChangeSide++;
            }
            // add down triangle
            if (countChangeSide <= 25) {
                // check
//                System.err.println("passage down" );
                rangeMaxX--;
                rangeMinX++;
                centerCellY++;
                if (countChangeSide == 25) {
                    countChangeSide++;
                }
            } else {
                if (countChangeSide == 26) {
                    rangeMinX = -4;
                    rangeMaxX = 4;
                    centerCellY = y;
                    countChangeSide++;
                }
                // add up triangle
                if (countChangeSide >= 26) {
                    // check
                    rangeMaxX--;
                    rangeMinX++;
                    centerCellY--;
                }
            }
        }
        // check all
//        System.err.println("my torpedo range list all " + listCellTorpedoRange.toString());

        // remove end of map
        List<Cell> listWithoutEndOfMap = utils.removeEndOfMap(listCellTorpedoRange, board);
        // remove earth on last list
        List<Cell> listWithoutEndOfMapAndEarth = utils.removeEarthCellTable(listWithoutEndOfMap, board);
        // remove center position
        List<Cell> listWithoutEndOfMapAndEarthAndCenterCell = listWithoutEndOfMapAndEarth;
        for (int i = 0; i < listWithoutEndOfMapAndEarth.size(); i++) {
            if (listWithoutEndOfMapAndEarth.get(i).getX() == x && listWithoutEndOfMapAndEarth.get(i).getY() == y) {
                listWithoutEndOfMapAndEarthAndCenterCell.remove(i);
            }

        }
        // check
//        System.err.println("my torpedo range list " + listWithoutEndOfMapAndEarthAndXY.toString());
//        long nbrCasePosible = listWithoutEndOfMapAndEarthAndXY.stream().count();
//        System.err.println("my taget case possible(torp): " + nbrCasePosible);
        return listWithoutEndOfMapAndEarthAndCenterCell;
    }

    public List<Cell> mixRangePossibilityAfterTorpedoWithMyRangeTorpedo (List<Cell> inputRangeListAfterTorpedo, Submarine mySubmarine) {
        List<Cell> possibilityOfTorpedoFire = new ArrayList<>();
        List<Cell> myRangeTorpedoList = mySubmarine.getListTorpedoRange();
        //check.
//        System.err.println("5 myRangeTorpedoList= " + myRangeTorpedoList.stream().count());

        for (int i = 0; i < inputRangeListAfterTorpedo.size() ; i++) { // ok this list contains cells!
            for (int j = 0; j < myRangeTorpedoList.size(); j++) {
                if (inputRangeListAfterTorpedo.get(i).getX() == myRangeTorpedoList.get(j).getX()) {
                    if (inputRangeListAfterTorpedo.get(i).getY() == myRangeTorpedoList.get(j).getY()) {
                        possibilityOfTorpedoFire.add(myRangeTorpedoList.get(j));
                    }
                }
            }
        }
        // check
//        System.err.println("Possibility of fire on: " + possibilityOfTorpedoFire.toString());
        return possibilityOfTorpedoFire;
    }

    public void canIFireTorpedo(Submarine mySubmarine, Board board) {
        List<Cell> potentialfireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySubmarine.getListOpponentPositionAfterTorpedo(), mySubmarine);
        // remove cell around me
        utils.createSafeCellListAroundMe(mySubmarine, board);
        // remove safeList of potentialFireList
        List<Cell> fireList = utils.removeCellsOnList(potentialfireList, mySubmarine.getSafeListOfCellAroundMe());
        mySubmarine.setTorpedoFireList(fireList);
        // check
        Long countFirecell = fireList.stream().count();
        System.err.println("Possibility of fire on: " + countFirecell);
        if (!fireList.isEmpty()) {
            mySubmarine.setCanIfireTorpedoFollowingOppTorp(true);
        } else {
            mySubmarine.setCanIfireTorpedoFollowingOppTorp(false);
        }
    }

    public Cell createPossibilitiCellOnSector(Submarine mySubmarine, Board board) {
        List<Cell> resultList = new ArrayList<>();
        int countFireList = 0;

        // get random cell on sector without safe list around me and only my torpedo range
        List<Cell> mySectorCellList = utils.findSectorCellWithPosition(mySubmarine, board);
        // list of safe cell (around me)
        utils.createSafeCellListAroundMe(mySubmarine, board);
        List<Cell> mySafeCellList = mySubmarine.getSafeListOfCellAroundMe();
        // remove safe cell to mysecto cells
        List<Cell> mySectorWithoutsafeList = utils.removeCellsOnList(mySectorCellList, mySafeCellList);
        // check
//        System.err.println("3 Create possibility Fire cell on sector " + mySectorWithoutsafeList.stream().count()); //21
        // get my range torpedo list
        // keep only forFireList - myrangeTorpedo -> to verify
        List<Cell> fireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySectorWithoutsafeList,mySubmarine); // 0!! bug
        // check
//        System.err.println("4 Create possibility Fire cell on sector " + fireList.stream().count());
        if (fireList.stream().count() == 0) {
            mySubmarine.setCanIfireFollowingSonarFeedback(false);
        }
        // check
//                System.err.println("potential fire following sonar: " + fireList.toString());
        System.err.println("potential fire on my sector: " + fireList.stream().count());
        for (int i = 0; i < fireList.size(); i++) {
            countFireList++;
        }
        // get random cell on fireList.
        if (countFireList > 0) {
            Cell randomfireTorpedo = utils.randomCellOnList(fireList);
            return randomfireTorpedo;
        } else {
            return new Cell(-1,-1,null,null);
        }

    }
}