package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.network.ApiClient;

public class LoginScreen implements Screen {

    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shape;
    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private BitmapFont inputFont;
    private BitmapFont msgFont;
    private GlyphLayout layout;

    // Form state
    private StringBuilder username = new StringBuilder();
    private StringBuilder password = new StringBuilder();
    private int activeField = 0; // 0 = username, 1 = password
    private boolean isRegisterMode = false;
    private String message = "";
    private boolean messageIsError = false;
    private float animTimer = 0f;
    private boolean loading = false;

    public LoginScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);
        shape    = new ShapeRenderer();
        layout   = new GlyphLayout();

        titleFont = new BitmapFont(); titleFont.getData().setScale(3.5f);
        labelFont = new BitmapFont(); labelFont.getData().setScale(1.6f);
        inputFont = new BitmapFont(); inputFont.getData().setScale(1.8f);
        msgFont   = new BitmapFont(); msgFont.getData().setScale(1.4f);

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        animTimer += delta;
        handleKeyInput();

        Gdx.gl.glClearColor(0.12f, 0.10f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;
        float cx = W / 2f;

        float panelW = 520f;
        float panelH = 380f;
        float panelX = cx - panelW / 2f;
        float panelY = H / 2f - panelH / 2f;

        // Panel background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Background Base (Polos elegan)
        shape.setColor(new Color(0.12f, 0.10f, 0.08f, 1f));
        shape.rect(0, 0, W, H);

        // Panel
        shape.setColor(new Color(0.16f, 0.13f, 0.11f, 0.95f));
        shape.rect(panelX, panelY, panelW, panelH);

        // Accent Line (Left only, no top/bottom border)
        shape.setColor(new Color(0.85f, 0.65f, 0.25f, 0.6f));
        shape.rect(panelX, panelY, 4, panelH);

        // Border Emas Solid (tidak kelap kelip)
        Color activeColor = new Color(0.85f, 0.65f, 0.25f, 1f);

        // Username field background
        float fieldW = 420f;
        float fieldH = 44f;
        float fieldX = cx - fieldW / 2f;
        float usernameFieldY = H / 2f + 40f;
        Color usernameBorder = (activeField == 0)
            ? activeColor
            : new Color(0.4f, 0.3f, 0.2f, 0.8f);
        shape.setColor(new Color(0.08f, 0.06f, 0.05f, 1f));
        shape.rect(fieldX, usernameFieldY, fieldW, fieldH);
        shape.setColor(usernameBorder);
        shape.rect(fieldX, usernameFieldY, fieldW, 2);
        shape.rect(fieldX, usernameFieldY + fieldH - 2, fieldW, 2);

        // Password field background
        float passwordFieldY = H / 2f - 40f;
        Color passwordBorder = (activeField == 1)
            ? activeColor
            : new Color(0.4f, 0.3f, 0.2f, 0.8f);
        shape.setColor(new Color(0.08f, 0.06f, 0.05f, 1f));
        shape.rect(fieldX, passwordFieldY, fieldW, fieldH);
        shape.setColor(passwordBorder);
        shape.rect(fieldX, passwordFieldY, fieldW, 2);
        shape.rect(fieldX, passwordFieldY + fieldH - 2, fieldW, 2);


        // Floating Coins (Background)
        for(int i = 0; i < 15; i++) {
            float px = (W / 15f) * i + (float)Math.sin(animTimer * 0.5f + i * 1.5f) * 80f;
            float py = (H / 15f) * i + (float)Math.cos(animTimer * 0.8f + i) * 80f;
            float bounce = (float) Math.sin(animTimer * 3f + i) * 5f;

            // Outer ring (gold)
            shape.setColor(new Color(1f, 0.84f, 0f, 0.1f));
            shape.circle(px, py + bounce, 12f);

            // Inner shine (lighter gold)
            shape.setColor(new Color(1f, 0.95f, 0.5f, 0.1f));
            shape.circle(px - 2f, py + bounce + 2f, 6f);
        }

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Text
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Title
        titleFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        String titleText = isRegisterMode ? "REGISTER" : "LOGIN";
        layout.setText(titleFont, titleText);
        titleFont.draw(game.batch, layout, cx - layout.width / 2f, panelY + panelH - 20);

        // Username label
        labelFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        labelFont.draw(game.batch, "Username", fieldX, usernameFieldY + fieldH + 30);

        // Username input
        String usernameDisplay = username.toString();
        if (usernameDisplay.isEmpty()) {
            inputFont.setColor(new Color(0.5f, 0.4f, 0.3f, 1f));
            usernameDisplay = "Type username...";
        } else {
            inputFont.setColor(Color.WHITE);
        }
        // Cursor solid pada field aktif (tidak hilang-hilang)
        if (activeField == 0) {
            usernameDisplay += "|";
        }
        inputFont.draw(game.batch, usernameDisplay, fieldX + 10, usernameFieldY + fieldH - 10);

        // Password label
        labelFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        labelFont.draw(game.batch, "Password", fieldX, passwordFieldY + fieldH + 30);

        // Password input (hidden)
        String passwordDisplay = "*".repeat(password.length());
        if (passwordDisplay.isEmpty()) {
            inputFont.setColor(new Color(0.5f, 0.4f, 0.3f, 1f));
            passwordDisplay = "Type password...";
        } else {
            inputFont.setColor(Color.WHITE);
        }
        // Cursor solid pada field aktif (tidak hilang-hilang)
        if (activeField == 1) {
            passwordDisplay += "|";
        }
        inputFont.draw(game.batch, passwordDisplay, fieldX + 10, passwordFieldY + fieldH - 10);

        // Submit button hint
        labelFont.getData().setScale(1.6f);
        labelFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        String submitText = isRegisterMode ? "Press ENTER to Register" : "Press ENTER to Login";
        layout.setText(labelFont, submitText);
        labelFont.draw(game.batch, layout, cx - layout.width / 2f, panelY + 70);

        // Toggle hint
        labelFont.getData().setScale(1.3f);
        labelFont.setColor(new Color(0.7f, 0.6f, 0.45f, 1f));
        String toggleText = isRegisterMode
            ? "Already have account? Press TAB to Login"
            : "No account? Press TAB to Register";
        layout.setText(labelFont, toggleText);
        labelFont.draw(game.batch, layout, cx - layout.width / 2f, panelY + 30);

        // Message
        if (!message.isEmpty()) {
            msgFont.setColor(messageIsError
                ? new Color(1f, 0.4f, 0.4f, 1f)
                : new Color(0.4f, 1f, 0.5f, 1f));
            layout.setText(msgFont, message);
            msgFont.draw(game.batch, layout, cx - layout.width / 2f, passwordFieldY - 20);
        }

        // Loading
        if (loading) {
            labelFont.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
            labelFont.draw(game.batch, "Loading...", cx - 50, passwordFieldY - 20);
        }

        game.batch.end();
    }

    private void handleKeyInput() {
        // TAB, toggle mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            isRegisterMode = !isRegisterMode;
            message = "";
            username.setLength(0);
            password.setLength(0);
            activeField = 0;
        }

        // Click field, TAB between username and password
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
            Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            activeField = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            activeField = 0;
        }

        // Backspace
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (activeField == 0 && username.length() > 0) {
                username.deleteCharAt(username.length() - 1);
            } else if (activeField == 1 && password.length() > 0) {
                password.deleteCharAt(password.length() - 1);
            }
        }

        // ENTER, submit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !loading) {
            if (username.length() == 0 || password.length() == 0) {
                message = "Username and password cannot be empty!";
                messageIsError = true;
                return;
            }
            if (isRegisterMode) {
                doRegister();
            } else {
                doLogin();
            }
        }

        // Typing characters
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char)('a' + (i - Input.Keys.A));
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    c = Character.toUpperCase(c);
                }
                if (activeField == 0 && username.length() < 20) username.append(c);
                else if (activeField == 1 && password.length() < 20) password.append(c);
            }
        }

        // Numbers
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char)('0' + (i - Input.Keys.NUM_0));
                if (activeField == 0 && username.length() < 20) username.append(c);
                else if (activeField == 1 && password.length() < 20) password.append(c);
            }
        }

        // Underscore
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) &&
            (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
            if (activeField == 0 && username.length() < 20) username.append('_');
            else if (activeField == 1 && password.length() < 20) password.append('_');
        }
    }

    private void doLogin() {
        loading = true;
        message = "";
        ApiClient.loginUser(username.toString(), password.toString(),
            new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String body) {
                    loading = false;
                    // Parse id dari response JSON sederhana
                    long id = parseIdFromJson(body);
                    String uname = parseUsernameFromJson(body);
                    game.playerStudentId = id;
                    game.playerUsername  = uname;
                    message = "Login successful! Welcome, " + uname;
                    messageIsError = false;
                    Gdx.app.postRunnable(() -> {
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    });
                }
                @Override
                public void onFailure(String err) {
                    loading = false;
                    message = "Login failed: " + err;
                    messageIsError = true;
                }
            });
    }

    private void doRegister() {
        loading = true;
        message = "";
        ApiClient.registerUser(username.toString(), password.toString(),
            new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String body) {
                    loading = false;
                    message = "Register successful! Please login.";
                    messageIsError = false;
                    isRegisterMode = false;
                    username.setLength(0);
                    password.setLength(0);
                }
                @Override
                public void onFailure(String err) {
                    loading = false;
                    message = "Register failed: " + err;
                    messageIsError = true;
                }
            });
    }

    private long parseIdFromJson(String json) {
        try {
            // Cari "idStudent":NUMBER
            int idx = json.indexOf("\"idStudent\":");
            if (idx < 0) return 1L;
            int start = idx + 12;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
            return Long.parseLong(json.substring(start, end));
        } catch (Exception e) { return 1L; }
    }

    private String parseUsernameFromJson(String json) {
        try {
            // Cari "username":"VALUE"
            int idx = json.indexOf("\"username\":\"");
            if (idx < 0) return "Player";
            int start = idx + 12;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) { return "Player"; }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shape.dispose();
        titleFont.dispose();
        labelFont.dispose();
        inputFont.dispose();
        msgFont.dispose();
    }
}
