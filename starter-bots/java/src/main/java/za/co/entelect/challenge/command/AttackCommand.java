package za.co.entelect.challenge.command;

import za.co.entelect.challenge.entities.Position;

public class AttackCommand {

    public Position position;
    public String command;
    public int damage;

    public AttackCommand(Position position, String command, int damage) {
        this.position = position;
        this.command = command;
        this.damage = damage;
    }

}
