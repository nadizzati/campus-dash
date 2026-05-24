package com.nadia.caslab.command;

import com.badlogic.gdx.Input;
import com.nadia.caslab.entity.Player;

// Membaca input keyboard WASD dan membuat Command yang sesuai
public class InputHandler {

    private final Player player;

    // Command objects (reusable)
    private final Command moveUp;
    private final Command moveDown;
    private final Command moveLeft;
    private final Command moveRight;

    public InputHandler(Player player) {
        this.player    = player;
        this.moveUp    = new MoveCommands.MoveUpCommand(player);
        this.moveDown  = new MoveCommands.MoveDownCommand(player);
        this.moveLeft  = new MoveCommands.MoveLeftCommand(player);
        this.moveRight = new MoveCommands.MoveRightCommand(player);
    }

    // Cek input keyboard
    public Command handleInput(boolean upPressed, boolean downPressed,
                               boolean leftPressed, boolean rightPressed) {
        if (upPressed)    return moveUp;
        if (downPressed)  return moveDown;
        if (leftPressed)  return moveLeft;
        if (rightPressed) return moveRight;
        return null;
    }

    // Mapping tombol LibGDX ke boolean
    public static boolean isUpPressed()    { return com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.W); }
    public static boolean isDownPressed()  { return com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.S); }
    public static boolean isLeftPressed()  { return com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.A); }
    public static boolean isRightPressed() { return com.badlogic.gdx.Gdx.input.isKeyPressed(Input.Keys.D); }
}
