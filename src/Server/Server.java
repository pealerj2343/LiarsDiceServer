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
                //DataOutputStream player1Out = new DataOutputStream(player1.getOutputStream());
                System.out.println("Player 1 Connected");
                //player1Out.writeChars("Player 1");
                //player1Out.close();
                numberOfPlayers++;

                Socket player2 = server.accept();
                //DataOutputStream player2Out = new DataOutputStream(player2.getOutputStream());
                System.out.println("Player 2 Connected");
                //player2Out.writeChars("Player 2");
                //player2Out.close();
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

    private ArrayList<ArrayList<Dice>> Hands = new ArrayList<>();

    int player1HandSize = 5;
    int player2HandSize = 5;

    private ArrayList<Dice> poolOfDice = new ArrayList<>();

    private int lastFace = 0;
    private int lastNum = 0;

    //-1 to accuse 0 to guess face 1 to guess num
    private int choice;

    private int currentFace = 0;
    private int currentNum = 0;

    private int turn = 0;

    public RunGame(Socket p1, Socket p2){
        this.p1 = p1;
        this.p2 = p2;
        numOfPlayers = 2;
    }

    @Override
    public void run() {
        try{
            System.out.println("Game Starting");
            p1Out = new DataOutputStream(p1.getOutputStream());
            p1In = new DataInputStream(p1.getInputStream());
            System.out.println("Created p1 in and out");
            p2Out = new DataOutputStream(p2.getOutputStream());
            p2In = new DataInputStream(p2.getInputStream());
            System.out.println("Created p2 in and out");

            p1Out.writeInt(1);
            p2Out.writeInt(2);

            createPool();
            System.out.println("Pool Created");
            //keeps game playing
            while(player1HandSize > 0 || player2HandSize > 0){
                rollDice();
                System.out.println("Dice Rolled");
                Boolean accuse = false;
                while(!accuse){
                    if(turn % 2 == 0) {
                        System.out.println("Player 1 turn");
                        p1Out.writeBoolean(true);
                        p1Out.writeInt(currentFace);
                        p1Out.writeInt(currentNum);
                        choice = p1In.readInt();
                        if(choice == -1){
                            accuse = true;
                        }else if(choice == 0){
                            currentFace = p1In.readInt();
                        }else if(choice == 1){
                            currentNum = p1In.readInt();
                        }
                        turn ++;
                    }else{
                        System.out.println("Player 2 turn");
                        p2Out.writeBoolean(true);
                        p2Out.writeInt(currentFace);
                        p2Out.writeInt(currentNum);
                        choice = p2In.readInt();
                        if(choice == -1){
                            accuse = true;
                        }else if(choice == 0){
                            currentFace = p2In.readInt();
                        }else if(choice == 1){
                            currentNum = p2In.readInt();
                        }
                        turn ++;
                    }
                }
            }

        }catch (IOException ex){

        }
    }
    private void sendDice(){
        try {
            p1Out.writeInt(Hands.get(0).size());
            for (Dice dice : Hands.get(0)) {
                try {
                    p1Out.writeInt(dice.getNumber());
                } catch (IOException e) {
                    System.out.println("Failed to send p1 dice");
                }
            }
            p2Out.writeInt(Hands.get(1).size());
            for (Dice dice : Hands.get(1)) {
                try {
                    p2Out.writeInt(dice.getNumber());
                } catch (IOException e) {
                    System.out.println("Failed to send p2 dice");
                }
            }
        }catch (IOException ex){
            System.out.println("Failed to send dice");
        }
    }
    private void rollDice(){
        Random rand = new Random();
        Hands.add(new ArrayList<Dice>());
        for(int i = 0 ; i < player1HandSize; i++){
            int num = rand.nextInt(poolOfDice.size());
            Hands.get(0).add(poolOfDice.get(num));
            poolOfDice.remove(num);
            System.out.println("Player 1: " + Hands.get(0).get(i));
            poolOfDice.trimToSize();
        }
        Hands.add(new ArrayList<Dice>());
        for(int i = 0 ; i < player2HandSize; i++){
            int num = rand.nextInt(poolOfDice.size());
            Hands.get(1).add(poolOfDice.get(num));
            poolOfDice.remove(num);
            System.out.println("Player 2: " + Hands.get(1).get(i));
            poolOfDice.trimToSize();
        }
        sendDice();
    }
    private void createPool(){
        for(int i = 0 ; i < 5 * numOfPlayers; i++){
            poolOfDice.add(new Dice());
            System.out.println(poolOfDice.get(i));
        }
        System.out.println("Pool Created");
    }
}

