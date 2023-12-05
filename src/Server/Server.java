package Server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {
    private static int numberOfPlayers = 0;
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8000);
            while(true) {

                Socket player1 = server.accept();
                DataOutputStream player1Out = new DataOutputStream(player1.getOutputStream());
                player1Out.writeChars("Player 1");
                player1Out.close();
                numberOfPlayers++;

                Socket player2 = server.accept();
                DataOutputStream player2Out = new DataOutputStream(player2.getOutputStream());
                player2Out.writeChars("Player 2");
                player2Out.close();
                numberOfPlayers++;

                new Thread(new RunGame(player1,player2)).start();
            }

        }catch (IOException e){
            System.out.println("Server Failed");
        }
    }

}
class RunGame implements Runnable{
    private Socket p1;
    private Socket p2;

    //default is 2 players
    private int numOfPlayers = 2;

    private DataOutputStream p1Out;
    private DataInputStream p1In;

    private DataOutputStream p2Out;
    private DataInputStream p2In;

    ArrayList<ArrayList<Dice>> Hands = new ArrayList<>();

    int player1HandSize = 5;
    int player2HandSize = 5;

    ArrayList<Dice> poolOfDice = new ArrayList<>();

    public RunGame(Socket p1, Socket p2){
        this.p1 = p1;
        this.p2 = p2;
        numOfPlayers = 2;
    }

    @Override
    public void run() {
        try{
            p1Out = new DataOutputStream(p1.getOutputStream());
            p1In = new DataInputStream(p1.getInputStream());

            p2Out = new DataOutputStream(p2.getOutputStream());
            p2In = new DataInputStream(p2.getInputStream());

            createPool();

            //keeps game playing
            while(player1HandSize > 0 || player2HandSize > 0){
                rollDice();
                Boolean accuse = false;
                int lastFace = 0;
                int lastNum = 0;
                while(!accuse){
                    int face = p1In.readInt();
                    if(face == -1){
                        accuse = true;
                    }
                }
            }

        }catch (IOException ex){

        }
    }
    private void rollDice(){
        Random rand = new Random();
        for(int i = 0 ; i < player1HandSize; i++){
            Hands.get(0).add(poolOfDice.get(rand.nextInt(poolOfDice.size()+1)));
            poolOfDice.trimToSize();
        }
        for(int i = 0 ; i < player2HandSize; i++){
            Hands.get(1).add(poolOfDice.get(rand.nextInt(poolOfDice.size()+1)));
            poolOfDice.trimToSize();
        }
    }
    private void createPool(){
        for(int i = 0 ; i < 5 * numOfPlayers; i++){
            poolOfDice.add(new Dice());
        }
    }
}

