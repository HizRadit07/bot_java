package za.co.entelect.challenge.enums;

public enum Direction {

    N(0, -1),
    NE(1, -1),
    E(1, 0),
    SE(1, 1),
    S(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(-1, -1);

    public final int x;
    public final int y;
    

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    static public Direction[] getAllDirections(){
        List<Direction> allDirections = new ArrayList<>();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 & j != 0) {
                    allDirections.add(new Direction(i, j));
                }                
            }
        }

        return allDirections;
    }
}
