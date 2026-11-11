
package com.yapm.android;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CredentialRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yapm.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "credentials";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_SERVICE = "service";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NOTES = "notes";

    public CredentialRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_SERVICE + " TEXT NOT NULL, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_NOTES + " TEXT, " +
                "UNIQUE (" + COLUMN_TYPE + ", " +
                COLUMN_SERVICE + ", " +
                COLUMN_USERNAME + "))";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long add(Credential credential) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, getType(credential));
        values.put(COLUMN_SERVICE, getService(credential));
        values.put(COLUMN_USERNAME, credential.getUsername());
        values.put(COLUMN_PASSWORD, credential.getPassword());
        values.put(COLUMN_NOTES, credential.getNotes());

        return db.insert(TABLE_NAME, null, values);
    }

    public Credential get(String type, String service, String username) {

        long id = getID(type, service, username);

        if (id == -1) {
            return null;
        }

        return getByID(id);
    }

    public ArrayList<Credential> search(String service, String type) {

        ArrayList<Credential> credentials = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection;
        String[] selectionArgs;

        if (type == null || type.equals("ALL")) {

            selection = COLUMN_SERVICE + " LIKE ?";
            selectionArgs = new String[]{"%" + service + "%"};

        } else {

            selection = COLUMN_TYPE + " = ? AND " +
                    COLUMN_SERVICE + " LIKE ?";

            selectionArgs = new String[]{
                    type,
                    "%" + service + "%"
            };
        }

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                COLUMN_SERVICE + " COLLATE NOCASE ASC"
        );

        try {

            while (cursor.moveToNext()) {

                Credential credential = cursorToCredential(cursor);

                if (credential != null) {
                    credentials.add(credential);
                }
            }

        } finally {
            cursor.close();
        }

        return credentials;
    }

    public int update(Credential oldCredential, Credential newCredential) {

        long id = getID(oldCredential);

        if (id == -1) {
            return 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, getType(newCredential));
        values.put(COLUMN_SERVICE, getService(newCredential));
        values.put(COLUMN_USERNAME, newCredential.getUsername());
        values.put(COLUMN_PASSWORD, newCredential.getPassword());
        values.put(COLUMN_NOTES, newCredential.getNotes());

        return db.update(
                TABLE_NAME,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int remove(Credential credential) {

        long id = getID(credential);

        if (id == -1) {
            return 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private long getID(Credential credential) {

        return getID(
                getType(credential),
                getService(credential),
                credential.getUsername()
        );
    }

    private long getID(String type, String service, String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_TYPE + " = ? AND " +
                        COLUMN_SERVICE + " = ? AND " +
                        COLUMN_USERNAME + " = ?",
                new String[]{type, service, username},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }

        return -1;
    }

    private Credential getByID(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursorToCredential(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    private String getType(Credential credential) {

        if (credential instanceof SiteCredential) {
            return "SITE";
        }

        if (credential instanceof SocialMediaCredential) {
            return "SOCIAL_MEDIA";
        }

        if (credential instanceof WifiCredential) {
            return "WIFI";
        }

        throw new IllegalArgumentException("Unknown credential type");
    }

    private String getService(Credential credential) {

        if (credential instanceof SiteCredential) {
            return ((SiteCredential) credential).getSite();
        }

        if (credential instanceof SocialMediaCredential) {
            return ((SocialMediaCredential) credential).getPlatform();
        }

        if (credential instanceof WifiCredential) {
            return ((WifiCredential) credential).getSsid();
        }

        throw new IllegalArgumentException("Unknown credential type");
    }

    private Credential cursorToCredential(Cursor cursor) {

        String type = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_TYPE)
        );

        String service = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_SERVICE)
        );

        String username = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_USERNAME)
        );

        String password = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)
        );

        String notes = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_NOTES)
        );

        if (type.equals("SITE")) {
            return new SiteCredential(
                    service, username, password, notes
            );
        }

        if (type.equals("SOCIAL_MEDIA")) {
            return new SocialMediaCredential(
                    service, username, password, notes
            );
        }

        if (type.equals("WIFI")) {
            return new WifiCredential(
                    service, username, password, notes
            );
        }

        return null;
    }
}
