package Game;

import Game.DataStructure.Hex;
import Game.DataStructure.Hex.Border;
import Game.DataStructure.Vector3;
import Game.Helper.Layout;
import Game.Helper.Point;
import Game.Players.AbstractPlayer;
import Game.Players.HumanPlayer;
import Game.Players.RandomAI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumSet;


public class GameController {

    public AbstractPlayer currentPlayer;
    public AbstractPlayer otherPlayer;
    private int turnCounter; //Used for swap rule
    public boolean gameOver;

    private BoardView boardView;
    private Layout layout;
    private ArrayList<Vector3> visited;

    public GameController(Layout layout, BoardView boardView , int tCounter) {

        this.layout = layout;
        this.boardView = boardView;
        turnCounter = tCounter;
    }


    public void switchTurns(GameController controller) {
        AbstractPlayer temp = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = temp;
        if (currentPlayer instanceof HumanPlayer) {
            boardView.setListener(this, new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    controller.click(e.getX(), e.getY(), null);
                    //System.out.println(e.getX()+"  "+ e.getY());
                }
            });
        }
        else {
            if(currentPlayer instanceof RandomAI){
                Vector3 v = ((RandomAI) currentPlayer).ranVec(boardView.board.height, boardView.board.width);
                Hex h = boardView.board.map.get(v);
                while (h.getOwner() != null){
                    v = ((RandomAI) currentPlayer).ranVec(boardView.board.height, boardView.board.width);
                    h = boardView.board.map.get(v);
                }
                controller.click(0, 0, v);

            }
            /*
            boardView.setListener(this, new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {}
            });*/
        }
    }

    public void click(int x, int y, Vector3 v) {
        if(!gameOver) {
            Vector3 coord;
            if(v == null) {
                coord = layout.pixelToVector3(new Point(x, y));
            }else{
                coord = v;
            }
            Hex h = boardView.board.map.get(coord);

            if (h != null && h.getOwner() != null && turnCounter == 1) {
                h.setOwner(currentPlayer);

                if (checkEnd(coord)) {
                    System.out.println(currentPlayer + " WINS!");
                    gameOver = true;
                    currentPlayer = null;
                }

                turnCounter++;
                switchTurns(this);

            } else if (h != null && h.getOwner() == null) {

                turnCounter++;
                h.setOwner(currentPlayer);

                if (checkEnd(coord)) {
                    System.out.println(currentPlayer + " WINS!");
                    gameOver = true;
                }

                switchTurns(this);
            }
        }
    }

    private boolean checkEnd(Vector3 coord) {

        EnumSet<Border> toFind = currentPlayer.borders.clone();
        this.visited = new ArrayList<Vector3>();
        dfs(coord, toFind);
        return toFind.isEmpty();
    }

    private void dfs(Vector3 coord, EnumSet<Border> borders) {

        this.visited.add(coord);
        borders.removeIf(b -> boardView.board.map.get(coord).getBorders().contains(b));

        for (Vector3 dir: boardView.board.getNeighbours(coord)) {
            AbstractPlayer p = boardView.board.map.get(dir).getOwner();
            if (p != null && p.equals(currentPlayer) && !visited.contains(dir))
                dfs(dir, borders);
        }
    }
}
