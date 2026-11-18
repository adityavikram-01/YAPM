
package com.yapm.android;

import android.net.Uri;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import androidx.appcompat.app.AlertDialog;

import java.util.HashSet;
import java.util.Set;

public class ImportExportFragment extends Fragment {

    private ActivityResultLauncher<String> exportLauncher;
    private ActivityResultLauncher<String[]> importLauncher;

    public ImportExportFragment() {
        super(R.layout.fragment_import_export);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/octet-stream"),
                this::exportDatabase
        );

        importLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                this::prepareImport
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button exportButton = view.findViewById(R.id.btn_export_database);

        exportButton.setOnClickListener(v -> {
            exportLauncher.launch("yapm_backup.db");
        });

        Button importButton = view.findViewById(R.id.btn_import_database);

        importButton.setOnClickListener(v -> {
            importLauncher.launch(new String[]{
                    "application/octet-stream",
                    "*/*"
            });
        });
    }

    private void exportDatabase(Uri uri) {

        if (uri == null) {
            return;
        }

        CredentialRepository repository =
                new CredentialRepository(requireContext());

        try {
            SQLiteDatabase db = repository.getWritableDatabase();

            try (Cursor cursor = db.rawQuery(
                    "PRAGMA wal_checkpoint(FULL)", null)) {

                if (cursor.moveToFirst() && cursor.getInt(0) != 0) {
                    throw new IOException("Could not checkpoint the database.");
                }
            }

        } catch (Exception e) {

            Toast.makeText(requireContext(),
                    "Could not prepare database: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            return;

        } finally {
            repository.close();
        }

        File databaseFile = requireContext().getDatabasePath("yapm.db");

        try {

            if (!databaseFile.exists()) {
                throw new IOException("Database file not found.");
            }

            try (
                    InputStream input = new FileInputStream(databaseFile);
                    OutputStream output = requireContext()
                            .getContentResolver()
                            .openOutputStream(uri)
            ) {

                if (output == null) {
                    throw new IOException("Could not open destination.");
                }

                byte[] buffer = new byte[8192];
                int length;

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                output.flush();
            }

            Toast.makeText(requireContext(),
                    "Database exported successfully!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Toast.makeText(requireContext(),
                    "Export failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    private void prepareImport(Uri uri) {

        if (uri == null) {
            return;
        }

        File databaseFile = requireContext().getDatabasePath("yapm.db");
        File databaseDirectory = databaseFile.getParentFile();

        File tempFile = new File(
                databaseDirectory,
                "yapm_import_temp.db"
        );

        try {

            if (!databaseDirectory.exists() && !databaseDirectory.mkdirs()) {
                throw new IOException("Could not access database directory.");
            }

            try (
                    InputStream input = requireContext()
                            .getContentResolver()
                            .openInputStream(uri);

                    OutputStream output = new FileOutputStream(tempFile)
            ) {

                if (input == null) {
                    throw new IOException("Could not read selected file.");
                }

                byte[] buffer = new byte[8192];
                int length;

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
            }

            validateDatabase(tempFile);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Replace Database?")
                    .setMessage(
                            "Importing this database will replace ALL your " +
                                    "existing credentials. A backup of your current " +
                                    "database will be retained internally.\n\n" +
                                    "Are you sure you want to continue?"
                    )
                    .setPositiveButton("Replace", (dialog, which) -> {

                        try {
                            replaceDatabase(tempFile);

                            Toast.makeText(requireContext(),
                                    "Database imported successfully!",
                                    Toast.LENGTH_LONG).show();

                        } catch (Exception e) {

                            Toast.makeText(requireContext(),
                                    "Import failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        } finally {
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        tempFile.delete();
                    })
                    .setOnCancelListener(dialog -> tempFile.delete())
                    .show();

        } catch (Exception e) {

            if (tempFile.exists()) {
                tempFile.delete();
            }

            Toast.makeText(requireContext(),
                    "Invalid database: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    private void validateDatabase(File file) throws Exception {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                file.getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY
        );

        try {

            try (Cursor cursor = db.rawQuery(
                    "PRAGMA integrity_check", null)) {

                if (!cursor.moveToFirst() ||
                        !"ok".equalsIgnoreCase(cursor.getString(0))) {

                    throw new IOException("Database integrity check failed.");
                }
            }

            try (Cursor cursor = db.rawQuery(
                    "PRAGMA user_version", null)) {

                if (!cursor.moveToFirst() || cursor.getInt(0) != 1) {
                    throw new IOException("Incompatible database version.");
                }
            }

            Set<String> columns = new HashSet<>();

            try (Cursor cursor = db.rawQuery(
                    "PRAGMA table_info(credentials)", null)) {

                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(1));
                }
            }

            String[] requiredColumns = {
                    "id",
                    "type",
                    "service",
                    "username",
                    "password",
                    "notes"
            };

            for (String column : requiredColumns) {
                if (!columns.contains(column)) {
                    throw new IOException(
                            "Missing database column: " + column
                    );
                }
            }

            try (Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM credentials " +
                            "WHERE type NOT IN ('SITE', 'SOCIAL_MEDIA', 'WIFI')",
                    null)) {

                if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                    throw new IOException("Unknown credential type found.");
                }
            }

        } finally {
            db.close();
        }
    }


    private void checkpointCurrentDatabase() throws Exception {

        CredentialRepository repository =
                new CredentialRepository(requireContext());

        try {

            SQLiteDatabase db = repository.getWritableDatabase();

            try (Cursor cursor = db.rawQuery(
                    "PRAGMA wal_checkpoint(FULL)", null)) {

                if (cursor.moveToFirst() && cursor.getInt(0) != 0) {
                    throw new IOException(
                            "Could not safely close the current database."
                    );
                }
            }

        } finally {
            repository.close();
        }
    }


    private void replaceDatabase(File tempFile) throws Exception {

        File databaseFile =
                requireContext().getDatabasePath("yapm.db");

        File directory = databaseFile.getParentFile();

        File backupFile = new File(
                directory,
                "yapm_before_import.db"
        );

        File walFile = new File(databaseFile.getPath() + "-wal");
        File shmFile = new File(databaseFile.getPath() + "-shm");

        checkpointCurrentDatabase();

        if (backupFile.exists() && !backupFile.delete()) {
            throw new IOException("Could not remove previous backup.");
        }

        boolean originalMoved = false;

        if (databaseFile.exists()) {

            if (!databaseFile.renameTo(backupFile)) {
                throw new IOException("Could not back up current database.");
            }

            originalMoved = true;
        }

        try {

            if (walFile.exists() && !walFile.delete()) {
                throw new IOException("Could not remove old WAL file.");
            }

            if (shmFile.exists() && !shmFile.delete()) {
                throw new IOException("Could not remove old SHM file.");
            }

            if (!tempFile.renameTo(databaseFile)) {
                throw new IOException("Could not install imported database.");
            }

        } catch (Exception e) {

            if (databaseFile.exists()) {
                databaseFile.delete();
            }

            if (originalMoved) {
                backupFile.renameTo(databaseFile);
            }

            throw e;
        }
    }

}
