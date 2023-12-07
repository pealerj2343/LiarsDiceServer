package Server;


public class Dice {
    private int number;
    public Dice(){
        roll();
    }
    public Dice(int num){
        number = num;
    }
    public void roll(){
        number = (int)((Math.random()*6) + 1);
    }

    public int getNumber() {
        return number;
    }
    @Override
    public String toString(){
        return "Dice value: " + number;
    }
}
