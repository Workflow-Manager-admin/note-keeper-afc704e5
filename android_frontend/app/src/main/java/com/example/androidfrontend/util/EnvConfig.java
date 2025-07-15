package com.example.androidfrontend.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * PUBLIC_INTERFACE
 * Reads .env variables from assets for Supabase config.
 */
public class EnvConfig {
    private static final String ENV_FILE = "env";
    private static final HashMap<String, String> envVars = new HashMap<>();

    public static void load(Context context) {
        if (!envVars.isEmpty()) return;
        try {
            InputStream is = context.getAssets().open(ENV_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) continue;
                int idx = line.indexOf("=");
                if (idx != -1) {
                    String key = line.substring(0, idx).trim();
                    String val = line.substring(idx + 1).trim();
                    envVars.put(key, val);
                }
            }
            reader.close();
        } catch (IOException e) {
            // fallback: Just leave envVars empty
        }
    }

    public static String get(String key) {
        return envVars.get(key);
    }
}
