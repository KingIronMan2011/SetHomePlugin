package com.kingironman.sethome.utilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

public class BackupUtils {
    private static final String BACKUP_DIR = "plugins/SetHome/backups";

    public static String createBackup(String storageType) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFileName = "backup_" + storageType + "_" + timestamp + ".zip";
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) backupDir.mkdirs();
        File backupFile = new File(backupDir, backupFileName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile))) {
            if (storageType.equalsIgnoreCase("yaml")) {
                File homesDir = new File("plugins/SetHome/homes");
                if (homesDir.exists()) zipDirectory(homesDir, homesDir.getName(), zos);
            } else if (storageType.equalsIgnoreCase("sqlite")) {
                File dbFile = new File("plugins/SetHome/homes.db");
                if (dbFile.exists()) zipFile(dbFile, dbFile.getName(), zos);
            } else if (storageType.equalsIgnoreCase("mysql")) {
                File config = new File("plugins/SetHome/config.yml");
                if (config.exists()) zipFile(config, config.getName(), zos);
            }
        }
        return backupFile.getName();
    }

    private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            } else {
                zipFile(file, parentFolder + "/" + file.getName(), zos);
            }
        }
    }

    private static void zipFile(File file, String entryName, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        }
        zos.closeEntry();
    }

    public static boolean restoreBackup(String storageType, String backupFileName) throws IOException {
        File backupFile = new File(BACKUP_DIR, backupFileName);
        if (!backupFile.exists()) return false;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                File outFile = null;
                if (storageType.equalsIgnoreCase("yaml")) {
                    if (entryName.startsWith("homes/")) {
                        outFile = new File("plugins/SetHome/" + entryName);
                    }
                } else if (storageType.equalsIgnoreCase("sqlite")) {
                    if (entryName.equals("homes.db")) {
                        outFile = new File("plugins/SetHome/homes.db");
                    }
                } else if (storageType.equalsIgnoreCase("mysql")) {
                    if (entryName.equals("config.yml")) {
                        outFile = new File("plugins/SetHome/config.yml");
                    }
                }
                if (outFile != null) {
                    // Ensure parent directories exist
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        return true;
    }
}
