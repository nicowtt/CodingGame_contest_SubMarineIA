import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    private Deploy deploy;
    private Move move;
    private Submarine mySubmarine;
    private Submarine opponentSubmarine;
    private Board board;
    private Utils utils;
    private Torpedo torpedo;
    private LocateOpponent locateOpponent;
    private List<Cell> listCellAlreadyVisited;

    public static void main(String args[]) {
      new Player().run();
    }

    public void run() {
        deploy = new Deploy();
        move = new Move();
        mySubmarine = new Submarine();
        opponentSubmarine = new Submarine();
        board = new Board();
        utils = new Utils();
        torpedo = new Torpedo();
        locateOpponent = new LocateOpponent();
        listCellAlreadyVisited = new ArrayList<>();

        String chargeSonar = "SONAR";
        String chargeTorpedo = "TORPEDO";
        String chargeSilence = "SILENCE";
        String sentSonar = "SONAR ";
        String silence = "SILENCE ";
        boolean isOpponentSentTorpedo = false;
        boolean loadedTorpedo = false;
        boolean loadedSonar = false;
        boolean loadedSilence = false;

        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        // create biDirectional table for map
        List<String> mapLines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            mapLines.add(line);
        }

        board = new Board(width, height,null, null, utils.makeSectors(0, 0));
        board.createBiDirectionalTable(mapLines);

        // order of deploiement
        System.out.println(deploy.deploy0(board));


        // ********* 2 **** Game loop********************************************************************************************:
        while (true) {
            boolean fireTorpedoFollowingOppTorp = false;
            boolean fireFollowingTorpedoFeedback = false;
            boolean lunchSonar = false;
            boolean fireFollowingSonarFeedback = false;
            boolean moveNextOnSilence = false;
            boolean oppPresenceOnMySector = false;
            int x = in.nextInt();
            int y = in.nextInt();
            Cell myMoveCell = new Cell(x, y, null, null);
            int myLife = in.nextInt();
            int oppLife = in.nextInt();
            opponentSubmarine.setLife(oppLife);
            int torpedoCooldown = in.nextInt();
            int sonarCooldown = in.nextInt();
            int silenceCooldown = in.nextInt();
            int mineCooldown = in.nextInt();
            String sonarResult = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentOrders = in.nextLine();

            mySubmarine.updateSubmarine(myId,x,y,myLife,torpedoCooldown,sonarCooldown,silenceCooldown,mineCooldown, sonarResult, opponentOrders);
            mySubmarine.setSafeListOfCellAroundMe(utils.createSafeCellListAroundMe(mySubmarine, board));

            isOpponentSentTorpedo = locateOpponent.readIfOpponentSentTorpedo(mySubmarine.getOpponentOrders());
            List<Cell> torpedorange = torpedo.createCellListTorpedoRange(mySubmarine.getPositionX(), mySubmarine.getPositionY(), board);
            mySubmarine.setListTorpedoRange(torpedorange);

            // check count fire list in progres
            if (mySubmarine.getTorpedoFireList() != null) {
                long countFireList = mySubmarine.getTorpedoFireList().stream().count();
                // check
                System.err.println("fire list in progress: " + countFireList);
            }

            // ********** 3   **** Locate opponent ************************************************************************:
            // ----------------------------------- after torpedo -----------------------------------------------------
            if (mySubmarine.getListOpponentPositionAfterTorpedo() != null) { // update with his next move
                locateOpponent.updateOpponentPresenceListAfterTorpedoWithNewMovement(mySubmarine, board); // record result list on mySubmarine
            }
            if (isOpponentSentTorpedo) {
                locateOpponent.createRangeCellOfOpponentPositionWhenSendTorpedo(mySubmarine, board);
            }
            // --------------------------------------------------------------------------------------------------------
            // check if opponent make surface
            int opponentSurfaceSector = locateOpponent.checkIfOpponentMakeSurface(mySubmarine, board);
            if (opponentSurfaceSector != -1) {
                // compare with my sector position
                if (opponentSurfaceSector == mySubmarine.getMySector()) {
                    oppPresenceOnMySector = true;
                }
                // todo if not -> move to this sector
            }

            // check if opponent make silence move
            boolean opponentMoveSilently = locateOpponent.checkIfOpponentMakeSilence(mySubmarine, board);
            if (opponentMoveSilently) {
                // check
                System.err.println("opponent move silently ");
                mySubmarine.setOpponentTorpedoExplosion(null);
                mySubmarine.setTorpedoFireList(null);
            }
            // *********** 4 ****** MySubmarine Check **********************************************************************
            // sonar feedback
            //check
            System.err.println("retour sonar: " + mySubmarine.getSonarResult());

            // check mySector position
            int mySectorId = utils.findMyPositionSector(mySubmarine, board);
            mySubmarine.setMySector(mySectorId);

            // check if i can fire torpedo following my position (my torpedo loaded and opponent locate list)
            if (mySubmarine.getTorpedoCooldown() == 0 && mySubmarine.getListOpponentPositionAfterTorpedo() != null) {
                fireTorpedoFollowingOppTorp = torpedo.canIFireTorpedo(mySubmarine, board);
            }
            // check if my torpedo is loaded
            if (mySubmarine.getTorpedoCooldown() == 0) {
                loadedTorpedo = true;
                // check
                System.err.println("Torpedo loaded and list range ok");
            }
            // check if my sonar is loaded
            if (mySubmarine.getSonarCooldown() == 0) {
                loadedSonar = true;
                //check
                System.err.println("Sonar loaded");
            }
            // check if silence is loaded
            if (mySubmarine.getSilenceCooldown() == 0) {
                loadedSilence = true;
                //check
                System.err.println("Silence loaded");
                // lunch sonar in my sector
                if (mySubmarine.getSonarCooldown() == 0) {
                    lunchSonar = true;
                    //check
                    System.err.println("sonar can sent!");
                }
            }
            // add torpedo order if possible
            if (fireTorpedoFollowingOppTorp && loadedTorpedo) {
                fireFollowingTorpedoFeedback = true;
                loadedTorpedo = false;
            }

            // if mySubmarine life increase 2 point -> next move to silence if possible
            int lifeDown = utils.compareLifeLoopBefore(mySubmarine);
            // check
//            System.err.println("life down: " + lifeDown);
            if ((lifeDown == 2) && loadedSilence) {
                // next move to silence
                moveNextOnSilence = true;
            }


            // ********** 5   **** Action ***********************************************************************************:
            // think for next move
            // first solution (random)
//            String nextMove = move.moveIA1(mySubmarine, board);
            // Second solution (to a point)
            String nextMove = move.moveIA3(mySubmarine, board);

            // check if opponent is in my  sector with sonar feedback
            //check
            if (mySubmarine.getSonarResult().equals("Y")) {
                fireFollowingSonarFeedback = true;
            }

            // if mySubmarin make SURFACE persist last mySubmarine position on object board
            if (nextMove == "SURFACE") {
                listCellAlreadyVisited = new ArrayList<>();
            }
            listCellAlreadyVisited.add(myMoveCell);
            board.setListCellAlreadyVisited(listCellAlreadyVisited);


            // if my submarine move -> Charge torpedo first and sonar after and silence after
            if (nextMove != "SURFACE" && !loadedTorpedo && !moveNextOnSilence) {
                nextMove = nextMove + chargeTorpedo;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && !loadedSonar && !moveNextOnSilence) {
                nextMove = nextMove + chargeSonar;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && loadedSonar && loadedSilence && lunchSonar && !moveNextOnSilence) {
                //check
                System.err.println("passage sonar order");
                // lunch sonar
                nextMove = sentSonar + String.valueOf(mySubmarine.getMySector()) + "|" + nextMove;
                loadedSonar = false;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && loadedSonar && !moveNextOnSilence) {
                nextMove = nextMove + chargeSilence;
            }
            // if important damage try to move silence (for now only one move on silence)
            if (nextMove != "SURFACE" && moveNextOnSilence && loadedSilence) {
                //check
                System.err.println("next move on silence!");
                moveNextOnSilence = false;
                loadedSilence = false;
                nextMove = silence + mySubmarine.getMyNextMove().getCardinalPoint() + 1;
            }
            // check
            System.err.println("Fire Following Torpedo Feedback: " + fireFollowingTorpedoFeedback);
            System.err.println("Fire Following Sonar Feedback: " + fireFollowingSonarFeedback);
            System.err.println("Opponent presence on my sector: " + oppPresenceOnMySector);
            System.err.println("--Torpedo loaded: " + loadedTorpedo + " --");

            // ----------- FIRE ---------- FIRE -------------- FIRE -----------
            // add fire on move order following opponent fire
            if (fireFollowingTorpedoFeedback && !fireFollowingSonarFeedback && !moveNextOnSilence) {
                System.err.println("passage fire following opp torpedo");
                // get random cell on fireList
                Cell randomfireTorpedo = utils.randomCellOnList(mySubmarine.getTorpedoFireList());
                String addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                String nextMoveFire = addfireTorpedoString + "|" + nextMove;
                // order of move and fire
                loadedTorpedo = false;
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);
            }
            // add fire on move order following sonar or opponent presence on my sector
            else if (fireFollowingSonarFeedback && !fireFollowingTorpedoFeedback && !moveNextOnSilence ||
                    oppPresenceOnMySector && loadedTorpedo) {
                System.err.println("passage fire following opp on my sector (sonar or opp SURFACE)");
                // get random cell on mySector
                Cell randomfireTorpedo = torpedo.createPossibilitiCellOnSector(mySubmarine, board);
                String addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                String nextMoveFire = addfireTorpedoString + "|" + nextMove;

                // update lifeLoopBefore
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                // order of move and fire
                loadedTorpedo = false;
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);

            }
            // -----------JUST MOVE  ---------- JUST MOVE -------------- JUST MOVE -----------
            else {
                // update lifeLoopBefore
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                // order for move
                System.out.println(nextMove);
            }

            // print submarines info
//            System.err.println("My submarine: " + mySubmarine.toString());
//            System.err.println("Opponent submarine: " + opponentSubmarine.toString());
        }
    }
}