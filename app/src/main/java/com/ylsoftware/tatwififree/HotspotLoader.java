package com.ylsoftware.tatwififree;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;

public class HotspotLoader {
    public static String getCacheFileName(Context ctx) {
        return ctx.getFilesDir() + "/points.json";
    }

    public static void save(Context ctx, String data) throws IOException {
        Files.write(
                Paths.get(getCacheFileName(ctx)),
                data.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
    }

    public static ArrayList<Hotspot> load(Context ctx) {
        if (Files.exists(Paths.get(getCacheFileName(ctx)))) {
            ArrayList<Hotspot> hotspots = loadFromFile(getCacheFileName(ctx));
            if (!hotspots.isEmpty()) {
                return hotspots;
            }
        }
        return loadFromResourse(R.raw.points, ctx);
    }

    public static ArrayList<Hotspot> loadFromString(String data) {
        Gson gson = new Gson();
        Hotspot[] hotspotList = gson.fromJson(data, Hotspot[].class);

        ArrayList<Hotspot> hotspots = new ArrayList<>();
        Collections.addAll(hotspots, hotspotList);

        return hotspots;
    }

    public static ArrayList<Hotspot> loadFromFile(String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            return loadFromString(content);
        }
        catch (Exception e) {
            Log.e("Can't load file", e.getMessage());
        }
        return new ArrayList<>();
    }

    public static ArrayList<Hotspot> loadFromResourse(int resId, Context ctx) {
        DataInputStream dis = null;
        try {
            InputStream is = ctx.getResources().openRawResource(resId);
            dis = new DataInputStream(is);
            byte[] buff = new byte[dis.available()];
            if (dis.read(buff, 0, dis.available()) == 0) {
                throw new Exception("Empty file!");
            }

            return loadFromString(new String(buff));
        }
        catch (Exception e) {
            Log.e("Can't load file", e.getMessage());
        }
        finally {
            try {
                dis.close();
            } catch (IOException e) {
                Log.e("Can't close file", e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}
