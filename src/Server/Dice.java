package Server;


public class Dice {
    private int number;
    public Dice(){
        roll();
    }
    public void roll(){
        number = (int)((Math.random()*5) + 1);
    }

    public int getNumber() {
        return number;
    }
}
