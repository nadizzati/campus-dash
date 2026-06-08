package com.nadia.caslab.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

// ApiClient, HTTP client ke Spring Boot backend.

public class ApiClient {

    // false = itch.io offline mode dan true = pakai Spring Boot backend
    private static final boolean BACKEND_ENABLED = false;
    private static final String  BASE_URL        = "http://localhost:8080/api";

    public interface ApiCallback {
        void onSuccess(String responseBody);
        void onFailure(String errorMessage);
    }

    public static void submitGameResult(long idStudent, String status,
                                        int waktuTersisa, int koinDikumpulkan,
                                        int level, ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            if (callback != null) callback.onSuccess("{\"success\":true}");
            return;
        }
        String json = String.format(
            "{\"idStudent\":%d,\"statusTugas\":\"%s\",\"waktuTersisa\":%d,\"koinDikumpulkan\":%d,\"level\":%d}",
            idStudent, status, waktuTersisa, koinDikumpulkan, level);
        postJson(BASE_URL + "/sessions/submit", json, callback);
    }

    public static void startSession(long idStudent, ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            if (callback != null) callback.onSuccess("{\"success\":true}");
            return;
        }
        postJson(BASE_URL + "/sessions/start/" + idStudent, "", callback);
    }

    public static void getLeaderboard(ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            String dummy = "[" +
                "{\"rank\":1,\"username\":\"TopPlayer\",\"totalKoin\":200,\"totalSesiCompleted\":10}," +
                "{\"rank\":2,\"username\":\"Mahasiswa01\",\"totalKoin\":150,\"totalSesiCompleted\":7}," +
                "{\"rank\":3,\"username\":\"CampusDasher\",\"totalKoin\":100,\"totalSesiCompleted\":5}" +
                "]";
            if (callback != null) callback.onSuccess(dummy);
            return;
        }
        getJson(BASE_URL + "/students/leaderboard", callback);
    }

    public static void getStudentData(long idStudent, ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            if (callback != null) callback.onSuccess("{\"idStudent\":1,\"username\":\"Player\",\"totalKoinTerkumpul\":0}");
            return;
        }
        getJson(BASE_URL + "/students/" + idStudent, callback);
    }

    private static void postJson(String url, String json, ApiCallback callback) {
        try {
            Net.HttpRequest req = new HttpRequestBuilder()
                    .newRequest().method(Net.HttpMethods.POST).url(url)
                    .header("Content-Type","application/json").content(json).build();
            Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                @Override public void handleHttpResponse(Net.HttpResponse r) {
                    String body = r.getResultAsString();
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onSuccess(body); });
                }
                @Override public void failed(Throwable t) {
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onFailure(t.getMessage()); });
                }
                @Override public void cancelled() {
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onFailure("Cancelled"); });
                }
            });
        } catch (Exception e) { if(callback!=null) callback.onFailure(e.getMessage()); }
    }

    private static void getJson(String url, ApiCallback callback) {
        try {
            Net.HttpRequest req = new HttpRequestBuilder()
                    .newRequest().method(Net.HttpMethods.GET).url(url)
                    .header("Content-Type","application/json").build();
            Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
                @Override public void handleHttpResponse(Net.HttpResponse r) {
                    String body = r.getResultAsString();
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onSuccess(body); });
                }
                @Override public void failed(Throwable t) {
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onFailure(t.getMessage()); });
                }
                @Override public void cancelled() {
                    Gdx.app.postRunnable(() -> { if(callback!=null) callback.onFailure("Cancelled"); });
                }
            });
        } catch (Exception e) { if(callback!=null) callback.onFailure(e.getMessage()); }
    }

    public static void loginUser(String username, String password, ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            String dummy = "{\"idStudent\":1,\"username\":\"" + username + "\",\"totalKoinTerkumpul\":0}";
            if (callback != null) callback.onSuccess(dummy);
            return;
        }
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        postJson(BASE_URL + "/students/login", json, callback);
    }

    public static void registerUser(String username, String password, ApiCallback callback) {
        if (!BACKEND_ENABLED) {
            String dummy = "{\"idStudent\":1,\"username\":\"" + username + "\",\"totalKoinTerkumpul\":0}";
            if (callback != null) callback.onSuccess(dummy);
            return;
        }
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        postJson(BASE_URL + "/students/register", json, callback);
    }

}
