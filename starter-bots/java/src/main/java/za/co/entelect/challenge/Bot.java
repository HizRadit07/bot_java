package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.Math; 

public class Bot {

    private Random random;
    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;
    private Worm[] enemyWorms;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
        this.enemyWorms = opponent.worms;
    }

    private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    public Command run() {

        
        AttackCommand bestAttackCommand = getBestAttackCommand();

        if (bestAttackCommand.damage != 0) {
            
            if (bestAttackCommand.command == "shoot") {

                return new ShootCommand(resolveDirection(currentWorm.position, bestAttackCommand.position));

            } else if (bestAttackCommand.command == "bananabomb") {
                
                return new BananaBomb(bestAttackCommand.position.x, bestAttackCommand.position.y);

            } else {
             
                return new SnowballCommand(bestAttackCommand.position.x, bestAttackCommand.position.y);
                
            }
        }
        else {
            Position nextmove=pursuitEnemy();//get nextmove
            Cell nextCell = getCell(nextMove.x, nextmove.y);//get next cell based on next move

            if (nextCell.type==Celltype.AIR){
                return new MoveCommand(nextmove.x,nextmove.y);
            }
            else if(nextCell.type==CellType.DIRT){
                return new DigCommand(nextmove.x,nextmove.y);
            }
        }
    

        // Worm enemyWorm = getFirstWormInRange();
        // if (enemyWorm != null) {
        //     Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
        //     return new ShootCommand(direction);
        // }
        
        // List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        // int cellIdx = random.nextInt(surroundingBlocks.size());

        // Cell block = surroundingBlocks.get(cellIdx);
        // if (block.type == CellType.AIR) {
        //     return new MoveCommand(block.x, block.y);
        // } else if (block.type == CellType.DIRT) {
        //     return new DigCommand(block.x, block.y);
        // }

        return new DoNothingCommand();
    }

    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition)) {
                return enemyWorm;
            }
        }
 
        return null;
    }

    private List<List<Cell>> constructFireDirectionLines(int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = currentWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = currentWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range) {
                    break;
                }

                Cell cell = gameState.map[coordinateY][coordinateX];
                if (cell.type != CellType.AIR) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    private Cell getCell(int x, int y){
        return gameState.map[y][x];
    }

    private List<Cell> getSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Don't include the current position
                if (i != x && j != y && isValidCoordinate(i, j)) {
                    cells.add(getCell(i, j));
                }
            }
        }

        return cells;
    }
    
    

    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    private Direction resolveDirection(Position a, Position b) {
        StringBuilder builder = new StringBuilder();

        int verticalComponent = b.y - a.y;
        int horizontalComponent = b.x - a.x;

        if (verticalComponent < 0) {
            builder.append('N');
        } else if (verticalComponent > 0) {
            builder.append('S');
        }

        if (horizontalComponent < 0) {
            builder.append('W');
        } else if (horizontalComponent > 0) {
            builder.append('E');
        }

        return Direction.valueOf(builder.toString());
    }

    private Worm getClosestEnemy() { //returns closest enemy worm from enemyworm array
        int myX = currentWorm.position.x;
        int myY = currentWorm.position.y;

        int closestWormId = 0;

        int currentClosestDistance = 9999999;

        for (int i=0; i<3; i++) {
            if (enemyWorms[i].health>0) {
                int eucledianDistance = euclideanDistance(myX, myY, enemyWorms[i].position.x, enemyWorms[i].position.y);
                if (currentClosestDistance > eucledianDistance) {
                    currentClosestDistance = eucledianDistance;
                    closestWormId = i;
                }               
            }
        }

        int finalClosestWormId = closestWormId;

        return Arrays
                .stream(enemyWorms)
                .filter(worm -> finalClosest == worm.id)
                .findFirst()
                .get();        
        
    }

    private Position pursuitEnemy() {
        Worm closestEnemyWorm = getClosestEnemy();
        int myX = currentWorm.position.x;
        int myY = currentWorm.position.y;
        int closestAfterDistance  = 999999;

        Direction[] allDirections = Direction.getAllDirections(); 

        int closestAddedX;
        int closestAddedY;

        for (int i=0; i<allDirections.length; i++) {
            int addedMyX = myX + allDirections[i].x; 
            int addedMyY = myY + allDirections[i].y;
            
            if (closestAfterDistance > euclideanDistance(addedMyX, addedMyY, closestEnemyWorm.position.x, closestEnemyWorm.position.y)) {
                closestAfterDistance = euclideanDistance(addedMyX, addedMyY, closestEnemyWorm.position.x, closestEnemyWorm.position.y);
                closestAddedX = addedMyX;
                closestAddedY = addedMyY;
            }
        }

        return new Position(closestAddedX, closestAddedY);        
    }

    private Position[] getValidShootPosition(int range, Worm attackingWorm) {
        ArrayList<Position> positionArray = new ArrayList<>();
        Position currentPosition = attackingWorm.position;

        // get the position for N and S
        for (int i = -range; i <= range; i++) {
            positionArray.add(new Position(currentPosition.x, currentPosition.y));
        }

        // get the position for E and W
        for (int i = -range; i <= range; i++) {
            positionArray.add(new Position(currentPosition.x + i, currentPosition.y));
        }

        // get the position for SW and NE
        for (int i = -range; i <= range; i++) {
            positionArray.add(new Position(currentPosition.x + i, currentPosition.y + i));
        }

        // get the position for NW and SE
        for (int i = -range; i <= range; i++) {
            positionArray.add(new Position(currentPosition.x + i, currentPosition.y - i));
        }

        Position[] positionArrayResult = Arrays
                                            .stream(positionArray.toArray())
                                            .filter(position -> position.x != currentPosition.x && position.y != currentPosition.y)
                                            .toArray();

        return positionArrayResult;
    }

    private Position[] getValidSpecialAttackPosition(int range, Worm attackingWorm) {
        //. To determine if a cell is in range, calculate its euclidean distance from the worm's position, ]
        //  round it downwards to the nearest integer (floor), and check if it is less than or equal to the max range

        ArrayList<Position> positionArray = new ArrayList<>();

        for (int i=-range; i<=range; i++) {
            for (int j=-range; j<=range; j++) {                
                if (euclidianDistance(
                    attackingWorm.position.x, attackingWorm.position.y, 
                    attackingWorm.position.x + i, attackingWorm.position.y + j
                    ) <= range) {
                    positionArray.add(new Position(attackingWorm.position.x + i, attackingWorm.position.y + j));
                }
            }
        }

        return positionArray.toArray();
    }



    private Position[] getBananaBombImpactPosition(Position epicenter, int radius){

        ArrayList<Position> positionArray = new ArrayList<>();

        for (int i = -radius; i <= radius; i++) {

            int jMax = radius - Math.abs(i);
            
            for (int j = -jMax; j <= jMax; j++){
                positionArray.add(new Position(epicenter.x + i, epicenter.y + j));   
            }
        }

        return positionArray.toArray();
    }

    private Position[] getSnowballImpactPosition(Position epicenter, int radius){
        ArrayList<Position> positionArray = new ArrayList<>(); 
        
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                Position newPos = new Position(epicenter.x + i, epicenter.y + j);
                positionArray.add(newpos);
            }
        }
         
        return positionArray.toArray();  
     }

     private Boolean canAttackShoot() {
        Boolean canAttackShoot = false;
        int shootRange = currentWorm.weapon.range;
        Set<Position> validShootPositionArray = new HashSet<Position>(Arrays.asList(getValidShootPosition(shootRange, currentWorm)));

        for (Worm enemyWorm: enemyWorms) {
            canAttackShoot = canAttackShoot || validShootPositionArray.contains(enemyWorm.position);
        }

        return canAttackShoot;
     }

     private Boolean canAttackBananaBomb() {

        if (currentWorm.canBananaBomb()) {
            Boolean canAttackBananaBomb = false;
            int bananaBombRange = currentWorm.bananaBombWeapon.get().range;
            int bananaBombRadius = currentWorm.bananaBombWeapon.get().damageRadius;
            Position[] validBananaBombPosition = getValidSpecialAttackPosition(bananaBombRange, currentWorm);

            for (Position bananaBombPosition: validBananaBombPosition) {
                Set<Position> bananaBombImpactPositionArray = new HashSet<Position>(Arrays.asList(getBananaBombImpactPosition(bananaBombPosition, bananaBombRadius)));

                for (Worm enemyWorm: enemyWorms) {
                    canAttackBananaBomb = canAttackBananaBomb || bananaBombImpactPositionArray.contains(enemyWorm.position);
                }
            }

            return canAttackBananaBomb;
        
        } else {
            return false;
        }

     }

     private Boolean canAttackSnowball() {

        if (currentWorm.canSnowball()) {
            Boolean canAttackSnowball = false;
            int snowballRange = currentWorm.snowballWeapon.get().range;
            int snowballRadius = currentWorm.snowballWeapon.get().freezeRadius;
            Position[] validSnowballPosition = getValidSpecialAttackPosition(snowballRange, currentWorm);

            for (Position snowballPosition: validSnowballPosition) {
                Set<Position> snowballImpactPositionArray = new HashSet<Position>(Arrays.asList(getSnowballImpactPosition(snowballPosition, snowballRadius)));

                for (Worm enemyWorm: enemyWorms) {
                    canAttackSnowball = canAttackSnowball || snowballImpactPositionArray.contains(enemyWorm.position);
                }
            }

            return canAttackSnowball;

        } else {
            return false;
        }
     }

     private Boolean canAttack() {

         return canAttackShoot() || canAttackBananaBomb() || canAttackSnowball();         

     }

     private Boolean notObstruction(Position attackPosition) {
         
         return false;
     }

     private AttackCommand getBestShootCommand() {        
         AttackCommand attackCommand = new AttackCommand(new Position(0, 0), "shoot", 0);

         if (canAttackShoot()) {
             
             Position[] attackPositionArray = getValidShootPosition(currentWorm.weapon.range, currentWorm);

             for (Position attackPosition: attackPositionArray) {
                
                for (Worm enemyWorm: enemyWorms) {
                    Position enemyWormPosition = enemyWorm.position;

                    if (Position.isEqualPosition(attackPosition, enemyWormPosition)) {

                        int enemyDistanceX = enemyWormPosition.x - attackPosition.x;
                        int enemyDistanceY = enemyWormPosition.y - attackPosition.y;
                        Position enemyDistance = new Position(enemyDistanceX, enemyDistanceY);
                        Boolean zeroObstruct = true;                        

                        if (enemyDistance.isN()) {

                            for (Position checkPosition: attackPositionArray) {
                                
                                if (checkPosition.isN() && checkPosition.y < enemyWormPosition.y) {
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            } 

                        } else if(enemyDistance.isNE()){
                            
                            for (Position checkPosition: attackPositionArray) {

                                if (checkPosition.isNE() && checkPosition.x < enemyWormPosition.x && checkPosition.y < enemyWormPosition.y) {                                    
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }
                            
                            
                        } else if (enemyDistance.isE()) {

                            for (Position checkPosition: attackPositionArray){
                                
                                if(checkPosition.isE() && checkPosition.x < enemyWormPosition.x){
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }

                        } else if (enemyDistance.isSE()) {

                            for (Position checkPosition: attackPositionArray) {
                                    
                                if (checkPosition.isSE() && checkPosition.y > enemyWormPosition.y && checkPosition.x < enemyWormPosition.x) {
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }

                        } else if (enemyDistance.isS()) {

                            for (Position checkPosition: attackPositionArray) {
                                notObstruction = notObstruction(checkPosition);

                                if (checkPosition.isS() && checkPosition.y > enemyWormPosition.y) {                                    
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }

                        } else if (enemyDistance.isSW()) {

                            for (Position checkPosition: attackPositionArray) {
                                notObstruction = notObstruction(checkPosition);

                                if (checkPosition.isSW() && checkPosition.x > enemyWormPosition.x && checkPosition.y > enemyWormPosition.y) {                                    
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }

                        } else if (enemyDistance.isW()) {

                            for (Position checkPosition: attackPositionArray) {
                                notObstruction = notObstruction(checkPosition);

                                if (checkPosition.isW() && checkPosition.x > enemyWormPosition.x) {                                    
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }

                        } else if (enemyDistance.isNW()) {

                            for (Position checkPosition: attackPositionArray){

                                if(checkPosition.isNW() && checkPosition.x > enemyWormPosition.x && checkPosition.y < enemyWormPosition.y){
                                    zeroObstruct =  zeroObstruct && notObstruction(checkPosition);
                                }
                            }
                            
                        } 

                        if (zeroObstruct) { return new attackCommand(enemyWormPosition, "shoot", currentWorm.weapon.damage); }
                    } 
                }
             }             
         } 

         return attackCommand;
     }

     private AttackCommand getBestBananabombCommand() {
         Position attackPosition = new Position(0, 0);
         AttackCommand attackCommand = new AttackCommand(attackPosition, "bananabomb", 0);

         if (currentWorm.canBananaBomb()) {            
            int bananaBombRange = currentWorm.bananaBombWeapon.get().range;
            int bananaBombRadius = currentWorm.bananaBombWeapon.get().damageRadius;
            Position[] validBananaBombPosition = getValidSpecialAttackPosition(bananaBombRange, currentWorm);

            for (Position bananaBombPosition: validBananaBombPosition) {
                Set<Position> bananaBombImpactPositionArray = new HashSet<Position>(Arrays.asList(getBananaBombImpactPosition(bananaBombPosition, bananaBombRadius)));
                int maxDamageBananaBomb = 0;

                for (Worm enemyWorm: enemyWorms) {

                    if (bananaBombImpactPositionArray.contains(enemyWorm.position)) {                        
                        maxDamageBananaBomb += currentWorm.bananaBombWeapon.get().damage;
                    }
                }

                if (maxDamageBananaBomb > attackCommand.damage) {
                    attackCommand = new AttackCommand(bananaBombPosition, "bananabomb", maxDamageBananaBomb);
                }
            }
         } 

         return attackCommand;
     }

     private AttackCommand getBestSnowballCommand() {
        Position attackPosition = new Position(0, 0);
        AttackCommand attackCommand = new AttackCommand(attackPosition, "bananabomb", 0);

        if (currentWorm.canBananaBomb()) {            
           int snowballRange = currentWorm.snowballWeapon.get().range;
           int snowballRadius = currentWorm.snowballWeapon.get().damageRadius;
           Position[] validSnowballPosition = getValidSpecialAttackPosition(snowballRange, currentWorm);

           for (Position snowballPosition: validSnowballPosition) {
               Set<Position> snowballImpactPositionArray = new HashSet<Position>(Arrays.asList(getSnowballImpactPosition(snowballPosition, snowballRadius)));
               int maxDamageSnowball = 0;

               for (Worm enemyWorm: enemyWorms) {

                   if (snowballImpactPositionArray.contains(enemyWorm.position)) {                        
                       maxDamageSnowball += 1;
                   }
               }

               if (maxDamageSnowball > attackCommand.damage) {
                   attackCommand = new AttackCommand(snowballPosition, "snowball", maxDamageSnowball);
               }
           }
        } 

        return attackCommand;
     }

     private AttackCommand getBestAttackCommand() {

         AttackCommand bestShootCommand = getBestShootCommand();
         AttackCommand bestBananaBombCommand = getBestBananabombCommand();
         AttackCommand bestSnowBallCommand = getBestSnowballCommand();

         if (bestBananaBombCommand.damage > 0) {

             return bestBananaBombCommand;

         } else if (bestSnowBallCommand.damage > 0) { // && isNotFrozen
             // tambahin enemy is not frozen

             return bestSnowBallCommand;

         } else {

             return bestShootCommand;

         }
         
     }

    // private boolean isNotFrozen()
    // 
    // 
    // 
    // 
    // 
    // 
    // 
    // 
    //     
}
