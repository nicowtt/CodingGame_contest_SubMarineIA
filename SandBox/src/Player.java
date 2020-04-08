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
    private Action action;

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
        action = new Action();

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
        // create new board
        board = new Board(width, height,null, null, utils.makeSectors(0, 0));
        board.createBiDirectionalTable(mapLines);

        // order of deploiement
        System.out.println(deploy.deploy0(board));

        // initial state of mySubmarine
        mySubmarine.startInitialState(mySubmarine);

        // ***************************************** 2 **** Game loop**************************************************
        while (true) {
            // initial state for each loop
            mySubmarine.startInitialStateGameLoop(mySubmarine);
            int x = in.nextInt();
            int y = in.nextInt();
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

            mySubmarine.updateSubmarine(myId,x,y,myLife,torpedoCooldown,sonarCooldown,silenceCooldown,mineCooldown,
                    sonarResult, opponentOrders);
            mySubmarine.setSafeListOfCellAroundMe(utils.createSafeCellListAroundMe(mySubmarine, board));

            // ******************************* 3   **** Locate opponent ***********************************************
            locateOpponent.locateOpponent(mySubmarine, opponentSubmarine, board);

            // ******************************* 4 ****** MySubmarine Check *********************************************
            mySubmarine.check(mySubmarine,opponentSubmarine, board);

            // ******************************* 5 ****** MOVE AND ACTION ***********************************************
            // IA1 -> random cell ; IA2 -> scanning ; IA3 -> random cell(each move) on random sector
            mySubmarine.setNextMoveString(move.moveIA3(mySubmarine, board));
            // ----------------------------------------------------------------
            action.whenMySubmarineToSurface(mySubmarine, board);
            action.forChargingSubmarineTools(mySubmarine);
            action.whenMySubmarineIs2PointsDamaged(mySubmarine);

            // ----------- FIRE IF YOU CAN  ---------- FIRE -------------- FIRE -----------
            action.fireChoiceWhenTorpediFeedbackAndSonarFeedback(mySubmarine);
            action.fireWhenOppSendTorpedo(mySubmarine);
            action.fireWhenSonarFeedbackOnMySector(mySubmarine, opponentSubmarine, board);

            // -----------OR JUST MOVE  ---------- JUST MOVE -------------- JUST MOVE -----------
            action.justMoveIfICanFire(mySubmarine, opponentSubmarine);

            // print submarines info
//            System.err.println("My submarine: " + mySubmarine.toString());
//            System.err.println("Opponent submarine: " + opponentSubmarine.toString());
        }
    }
}