package com.nadia.caslab.command;

import com.nadia.caslab.entity.Player;
import com.nadia.caslab.game.GameConstants;

// Perintah gerakan player.

public class MoveCommands {

    // MoveUpCommand (W)
    public static class MoveUpCommand implements Command {
        private final Player player;
        private float prevY;

        public MoveUpCommand(Player player) { this.player = player; }

        @Override
        public void execute() {
            prevY = player.getPixelY();
            player.setTargetY(player.getTileY() + 1);
            player.setFacingDirection(Player.Direction.UP);
        }

        @Override
        public void undo() {
            player.setPixelY(prevY);
        }
    }

    // MoveDownCommand (S)
    public static class MoveDownCommand implements Command {
        private final Player player;
        private float prevY;

        public MoveDownCommand(Player player) { this.player = player; }

        @Override
        public void execute() {
            prevY = player.getPixelY();
            player.setTargetY(player.getTileY() - 1);
            player.setFacingDirection(Player.Direction.DOWN);
        }

        @Override
        public void undo() {
            player.setPixelY(prevY);
        }
    }

    // MoveLeftCommand (A)
    public static class MoveLeftCommand implements Command {
        private final Player player;
        private float prevX;

        public MoveLeftCommand(Player player) { this.player = player; }

        @Override
        public void execute() {
            prevX = player.getPixelX();
            player.setTargetX(player.getTileX() - 1);
            player.setFacingDirection(Player.Direction.LEFT);
        }

        @Override
        public void undo() {
            player.setPixelX(prevX);
        }
    }

    // MoveRightCommand (D)
    public static class MoveRightCommand implements Command {
        private final Player player;
        private float prevX;

        public MoveRightCommand(Player player) { this.player = player; }

        @Override
        public void execute() {
            prevX = player.getPixelX();
            player.setTargetX(player.getTileX() + 1);
            player.setFacingDirection(Player.Direction.RIGHT);
        }

        @Override
        public void undo() {
            player.setPixelX(prevX);
        }
    }
}
