package com.example.androidfrontend.supabase;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.util.EnvConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Handles all Supabase networking for Notes CRUD.
 */
public class SupabaseClient {
    private static SupabaseClient instance;
    private static final String TABLE_NAME = "notes";
    private final OkHttpClient httpClient;
    private final String supabaseUrl;
    private final String supabaseKey;

    // Singleton
    public static synchronized SupabaseClient getInstance(Context ctx) {
        if (instance == null) {
            EnvConfig.load(ctx);
            instance = new SupabaseClient(EnvConfig.get("SUPABASE_URL"), EnvConfig.get("SUPABASE_KEY"));
        }
        return instance;
    }

    private SupabaseClient(String url, String key) {
        this.supabaseUrl = url;
        this.supabaseKey = key;
        this.httpClient = new OkHttpClient();
    }

    private Headers getDefaultHeaders() {
        return new Headers.Builder()
            .add("apikey", supabaseKey)
            .add("Authorization", "Bearer " + supabaseKey)
            .add("Content-Type", "application/json")
            .build();
    }

    // PUBLIC_INTERFACE
    public interface NotesCallback {
        void onSuccess(List<Note> notes);
        void onError(String error);
    }

    // PUBLIC_INTERFACE
    public interface NoteCallback {
        void onSuccess(@Nullable Note note);
        void onError(String error);
    }

    // PUBLIC_INTERFACE
    public interface VoidCallback {
        void onSuccess();
        void onError(String error);
    }

    // PUBLIC_INTERFACE
    public void getAllNotes(final NotesCallback callback) {
        String url = supabaseUrl + "/rest/v1/" + TABLE_NAME + "?select=*";
        Request request = new Request.Builder()
            .url(url)
            .headers(getDefaultHeaders())
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Failed: " + response.message());
                    return;
                }
                try {
                    JSONArray arr = new JSONArray(response.body().string());
                    List<Note> notes = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Note n = new Note(
                            obj.optString("id", ""),
                            obj.optString("title", "(Untitled)"),
                            obj.optString("content", ""),
                            obj.optString("created_at", ""),
                            obj.optString("updated_at", "")
                        );
                        notes.add(n);
                    }
                    callback.onSuccess(notes);
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // PUBLIC_INTERFACE
    public void getNoteById(String id, final NoteCallback callback) {
        String url = supabaseUrl + "/rest/v1/" + TABLE_NAME + "?id=eq." + id + "&select=*";
        Request request = new Request.Builder()
            .url(url)
            .headers(getDefaultHeaders())
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Failed: " + response.message());
                    return;
                }
                try {
                    JSONArray arr = new JSONArray(response.body().string());
                    if (arr.length() > 0) {
                        JSONObject obj = arr.getJSONObject(0);
                        Note n = new Note(
                            obj.optString("id", ""),
                            obj.optString("title", "(Untitled)"),
                            obj.optString("content", ""),
                            obj.optString("created_at", ""),
                            obj.optString("updated_at", "")
                        );
                        callback.onSuccess(n);
                    } else {
                        callback.onSuccess(null);
                    }
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // PUBLIC_INTERFACE
    public void createNote(String title, String content, final NoteCallback callback) {
        String url = supabaseUrl + "/rest/v1/" + TABLE_NAME;
        String json = "{\"title\":\"" + escape(title) + "\",\"content\":\"" + escape(content) + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Headers headers = getDefaultHeaders().newBuilder().add("Prefer", "return=representation").build();

        Request request = new Request.Builder()
            .url(url)
            .headers(headers)
            .post(body)
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Failed: " + response.message());
                    return;
                }
                try {
                    JSONArray arr = new JSONArray(response.body().string());
                    if (arr.length() > 0) {
                        JSONObject obj = arr.getJSONObject(0);
                        Note n = new Note(
                            obj.optString("id", ""),
                            obj.optString("title", "(Untitled)"),
                            obj.optString("content", ""),
                            obj.optString("created_at", ""),
                            obj.optString("updated_at", "")
                        );
                        callback.onSuccess(n);
                    } else {
                        callback.onSuccess(null);
                    }
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // PUBLIC_INTERFACE
    public void updateNote(String id, String title, String content, final VoidCallback callback) {
        String url = supabaseUrl + "/rest/v1/" + TABLE_NAME + "?id=eq." + id;
        String json = "{\"title\":\"" + escape(title) + "\",\"content\":\"" + escape(content) + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Headers headers = getDefaultHeaders().newBuilder().add("Prefer", "return=minimal").build();

        Request request = new Request.Builder()
            .url(url)
            .headers(headers)
            .patch(body)
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Failed: " + response.message());
                } else {
                    callback.onSuccess();
                }
            }
        });
    }

    // PUBLIC_INTERFACE
    public void deleteNote(String id, final VoidCallback callback) {
        String url = supabaseUrl + "/rest/v1/" + TABLE_NAME + "?id=eq." + id;
        Headers headers = getDefaultHeaders().newBuilder().add("Prefer", "return=minimal").build();

        Request request = new Request.Builder()
            .url(url)
            .headers(headers)
            .delete()
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Failed: " + response.message());
                } else {
                    callback.onSuccess();
                }
            }
        });
    }

    // For safety, escape quotes in user's text
    private static String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
