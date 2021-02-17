package za.co.entelect.challenge.entities;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import za.co.entelect.challenge.command.SnowballCommand;

public class Worm {
    @SerializedName("id")
    public int id;

    @SerializedName("health")
    public int health;

    @SerializedName("position")
    public Position position;

    @SerializedName("diggingRange")
    public int diggingRange;

    @SerializedName("movementRange")
    public int movementRange;

    @SerializedName("profession")
    public String profession;

    @SerializedName("bananaBombs")
    public Optional<BananaBombWeapon> bananaBombWeapon = Optional.empty();

    @SerializedName("snowballs")
    public Optional<SnowballWeapon> snowballWeapon = Optional.empty();

    public Boolean canBananaBomb() {
        if (profession == "Agent") { 
            return bananaBombWeapon.get().count > 0;
        } else {
            return false;
        }
    }

    public Boolean canSnowball() {
        if (profession == "Technologist") {
            return snowballWeapon.get().count > 0;
        } else {
            return false;
        }
    }

}
