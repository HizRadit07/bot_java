package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("x")
    public int x;

    @SerializedName("y")
    public int y;

    public static Boolean isEqualPosition(Position p1, Position p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    public Boolean isN() {
        return this.x == 0 && this.y > 0;
    }

    public Boolean isNE() {
        return this.x == this.y && this.x > 0 && this.y > 0;
    }

    public Boolean isE() {
        return this.x > 0 && this.y == 0;
    }

    public Boolean isSE() {
        return this.x == -this.y && this.y < 0 && this.x > 0;
    }

    public Boolean isS() {
        return this.x == 0 && this.y < 0;
    }

    public Boolean isSW() {
        return this.x == this.y && this.x < 0 && this.y < 0;
    }

    public Boolean isW() {
        return this.x < 0 && this.y == 0;
    }

    public Boolean isNW() {
        return this.y == -this.x && this.x < 0 && this.y > 0;
    }
}
