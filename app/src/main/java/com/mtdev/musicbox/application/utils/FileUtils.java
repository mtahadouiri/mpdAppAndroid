/*
 *  Copyright (C) 2018 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://github.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mtdev.musicbox.application.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    /**
     * The directory name for all artwork images and subfolders
     */
    private static final String ARTWORK_DIR = "artworks";

    /**
     * Create a SHA256 Hash for the given input strings.
     *
     * @param inputStrings The input that will be used as a concatenated string to create the hashed value.
     * @return The result as a hex string.
     * @throws NoSuchAlgorithmException If SHA-256 is not available.
     */
    public static String createSHA256HashForString(final String... inputStrings) throws NoSuchAlgorithmException {
        final StringBuilder input = new StringBuilder();

        for (String string : inputStrings) {
            if (string != null) {
                input.append(string);
            }
        }

        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input.toString().getBytes());

        final byte bytes[] = md.digest();

        final StringBuilder hexString = new StringBuilder();
        for (byte oneByte : bytes) {
            final String hex = Integer.toHexString(0xff & oneByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Saves an image byte array in a file in the given directory.
     *
     * @param context  The application context to get the files directory of the app.
     * @param fileName The name that will be used to save the file.
     * @param dirName  The directory name in which the file is saved.
     * @param image    The image byte array that will be saved in a file.
     * @throws IOException If the file couldn't be written.
     */
    public static void saveArtworkFile(final Context context, final String fileName, final String dirName, final byte[] image) throws IOException {
        final File artworkDir = new File(context.getFilesDir() + "/" + ARTWORK_DIR + "/" + dirName + "/");
        artworkDir.mkdirs();

        final File imageFile = new File(artworkDir, fileName);

        final FileOutputStream outputStream = new FileOutputStream(imageFile);
        outputStream.write(image);
        outputStream.close();
    }


    /**
     * Generates the full absolute file path for an artwork image
     * @param context Context used for directory resolving
     * @param fileName Filename used as a basis
     * @param dirName Directory suffix
     * @return Full absolute file path
     */
    public static String getFullArtworkFilePath(final Context context, final String fileName, final String dirName) {
        return context.getFilesDir() + "/" + ARTWORK_DIR + "/" + dirName + "/" + fileName;
    }

    /**
     * Removes a file from the given directory.
     *
     * @param context  The application context to get the files directory of the app.
     * @param fileName The name of the file.
     * @param dirName  The name of the parent directory of the file.
     */
    public static void removeArtworkFile(final Context context, final String fileName, final String dirName) {
        final File artworkFile = new File(context.getFilesDir() + "/" + ARTWORK_DIR + "/" + dirName + "/" + fileName);
        artworkFile.delete();
    }

    /**
     * Removes the given directory.
     *
     * @param context The application context to get the files directory of the app.
     * @param dirName The name of the directory that should be removed.
     */
    public static void removeArtworkDirectory(final Context context, final String dirName) {
        final File artworkDir = new File(context.getFilesDir() + "/" + ARTWORK_DIR + "/" + dirName + "/");

        if (artworkDir.listFiles() != null) {
            for (File child : artworkDir.listFiles()) {
                child.delete();
            }
            artworkDir.delete();
        }
    }
}
