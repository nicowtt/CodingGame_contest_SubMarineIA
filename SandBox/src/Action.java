import java.util.ArrayList;
import java.util.List;

class Action {
    private Utils utils = new Utils();
    private Torpedo torpedo = new Torpedo();
    private List<Cell> listCellAlreadyVisited = new ArrayList<>();


    public void whenMySubmarineToSurface(Submarine mySubmarine, Board board) {
        Cell myMoveCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);
        // if mySubmarine make SURFACE persist last mySubmarine position on object board
        if (mySubmarine.getNextMoveString() == "SURFACE") {
            listCellAlreadyVisited = new ArrayList<>();
        }
        listCellAlreadyVisited.add(myMoveCell);
        board.setListCellAlreadyVisited(listCellAlreadyVisited);
    }

    public void forChargingSubmarineTools(Submarine mySubmarine ) {
        String chargeSonar = "SONAR";
        String chargeTorpedo = "TORPEDO";
        String chargeSilence = "SILENCE";
        String sentSonar = "SONAR ";

        // if my submarine move -> Charge torpedo first and sonar after and silence after
        if (mySubmarine.getNextMoveString() != "SURFACE" && !mySubmarine.getLoadedTorpedo() && !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeTorpedo;
            mySubmarine.setNextMoveString(nextMove);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && !mySubmarine.getLoadedSonar() &&
                !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeSonar;
            mySubmarine.setNextMoveString(nextMove);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && mySubmarine.getLoadedSonar() &&
                mySubmarine.getLoadedSilence() && mySubmarine.getCanISendSonar() && !mySubmarine.getMoveNextOnSilence()) {
            //check
            System.err.println("passage sonar order");
            // lunch sonar
            String nextMove = sentSonar + String.valueOf(mySubmarine.getMySector()) + "|" + mySubmarine.getNextMoveString();
            mySubmarine.setNextMoveString(nextMove);
            mySubmarine.setLoadedSonar(false);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && mySubmarine.getLoadedSonar() &&
                !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeSilence;
            mySubmarine.setNextMoveString(nextMove);
        }
    }

    public void whenMySubmarineIs2PointsDamaged(Submarine mySubmarine) {
        String silence = "SILENCE ";
        // if important damage try to move silence (for now only one move on silence)
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getMoveNextOnSilence() && mySubmarine.getLoadedSilence()) {
            //check
            System.err.println("next move on silence!");
            mySubmarine.setMoveNextOnSilence(false);
            mySubmarine.setLoadedSilence(false);
            if (mySubmarine.getMyNextMove() != null) {
                mySubmarine.setNextMoveString(silence + mySubmarine.getMyNextMove().getCardinalPoint() + 1);
            }

        }
        // check
        System.err.println("Fire Following Torpedo Feedback: " + mySubmarine.getCanIfireFollowingTorpedoFeedback());
        System.err.println("Fire Following Sonar Feedback: " + mySubmarine.getCanIfireFollowingSonarFeedback());
        System.err.println("Opponent presence on my sector: " + mySubmarine.getOppPresenceOnMySector());
        System.err.println("--Torpedo loaded: " + mySubmarine.getLoadedTorpedo() + " --");
    }

    public void fireChoiceWhenTorpediFeedbackAndSonarFeedback(Submarine mySubmarine) {
        if (mySubmarine.getCanIfireFollowingTorpedoFeedback() && mySubmarine.getCanIfireFollowingSonarFeedback()) {
            mySubmarine.setCanIfireFollowingSonarFeedback(false);
        }
    }

    public void fireWhenOppSendTorpedo(Submarine mySubmarine) {
        String addfireTorpedoString;

        // add fire on move order following opponent fire
        if (mySubmarine.getCanIfireFollowingTorpedoFeedback() && !mySubmarine.getCanIfireFollowingSonarFeedback() &&
                !mySubmarine.getMoveNextOnSilence()) {
            System.err.println("passage fire following opp torpedo");
            // get random cell on fireList
            Cell randomfireTorpedo = utils.randomCellOnList(mySubmarine.getTorpedoFireList());
            mySubmarine.setMyFireTorpedoCell(randomfireTorpedo);
            // if opp is lock
            if (mySubmarine.getOpponentCell() != null) {
                addfireTorpedoString = "TORPEDO " + mySubmarine.getOpponentCell().getX()
                        + " " + mySubmarine.getOpponentCell().getY();
            } else {
                addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
            }
            String nextMoveFire = addfireTorpedoString + "|" + mySubmarine.getNextMoveString();
            // order of move and fire
            mySubmarine.setLoadedTorpedo(false);
            //fire
            mySubmarine.setiFireOnPrecedentLoop(true);
            System.err.println("------- FIRE  -----------");
            System.out.println(nextMoveFire);
        }
    }

    public void fireWhenSonarFeedbackOnMySector(Submarine mySubmarine, Submarine opponentSubmarine, Board board) {
        String addfireTorpedoString;

        // add fire on move order following sonar or opponent presence on my sector
        if (mySubmarine.getCanIfireFollowingSonarFeedback() && !mySubmarine.getCanIfireFollowingTorpedoFeedback() &&
                !mySubmarine.getMoveNextOnSilence() ||
                mySubmarine.getOppPresenceOnMySector() && mySubmarine.getLoadedTorpedo()) {
            System.err.println("passage fire following opp on my sector (sonar or opp SURFACE)");
            // get random cell on mySector
            Cell randomfireTorpedo = torpedo.createPossibilitiCellOnSector(mySubmarine, board);
            if (randomfireTorpedo.getX() != -1) {
                mySubmarine.setMyFireTorpedoCell(randomfireTorpedo);
                // if opp is lock
                if (mySubmarine.getOpponentCell() != null) {
                    addfireTorpedoString = "TORPEDO " + mySubmarine.getOpponentCell().getX() + " " + mySubmarine.getOpponentCell().getY();
                } else {
                    addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                }
                //check
                System.err.println("addFireTorpedoString after sonar: " + addfireTorpedoString);
                String nextMoveFire = addfireTorpedoString + "|" + mySubmarine.getNextMoveString();

                // update my lifeLoopBefore and opponent
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                opponentSubmarine.setLifeLoopBefore(opponentSubmarine.getLife());
                // order of move and fire
                mySubmarine.setLoadedTorpedo(false);
                // fire
                mySubmarine.setiFireOnPrecedentLoop(true);
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);
            }
        }
    }

    public void justMoveIfICanFire(Submarine mySubmarine, Submarine opponentSubmarine) {
        if (!mySubmarine.getCanIfireFollowingSonarFeedback() && !mySubmarine.getCanIfireFollowingTorpedoFeedback()) {
            // update my lifeLoopBefore and opponent
            mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
            opponentSubmarine.setLifeLoopBefore(opponentSubmarine.getLife());
            // no fire
            mySubmarine.setiFireOnPrecedentLoop(false);
            // order for move
            System.out.println(mySubmarine.getNextMoveString());
        }
    }
}
