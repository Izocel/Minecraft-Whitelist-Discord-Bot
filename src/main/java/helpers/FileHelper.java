package helpers;

import org.apache.commons.io.FileUtils;

import main.WhitelistJe;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class to support creation of temporary files and directories with
 * initial attributes.
 */
public class FileHelper {

    public static final SecureRandom random = new SecureRandom();
    public static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    public static final WhitelistJe PLUGIN_PROVIDER = WhitelistJe.getPlugin(WhitelistJe.class);
    public static final Path TMP_DIR = Path.of(System.getProperty("java.io.tmpdir") + "\\WhitelistJe");
    public static final Path PLUGIN_DIR = Path.of(WhitelistJe.getPlugin(WhitelistJe.class).getDataFolder().toString());
    public static final String TRADUCTION_DIR_NAME = "traduction";
    public static final Path TRADUCTION_DIR = Path.of(PLUGIN_DIR + "\\" + TRADUCTION_DIR_NAME);

    public static String writeResourceToPluginDir(String srcFileName) {
        try {
            final String newFilePath = PLUGIN_DIR.toString().concat("\\" + srcFileName);
            final InputStream stream = PLUGIN_PROVIDER.getResource(srcFileName);
            final File newFile = new File(newFilePath);

            FileUtils.copyInputStreamToFile(stream, newFile);
            return newFilePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String writeResourceToPluginDir(String srcFileName, Boolean overwrite) {
        try {
            final String newFilePath = PLUGIN_DIR.toString().concat("\\" + srcFileName);
            final File newFile = new File(newFilePath);

            if (overwrite != true && newFile.exists()) {
                return null;
            }

            final InputStream stream = PLUGIN_PROVIDER.getResource(srcFileName);
            if (stream == null) {
                return null;
            }

            FileUtils.copyInputStreamToFile(stream, newFile);
            return newFilePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String writeToPluginDir(String fileName, String text) {
        try {
            final String newFilePath = PLUGIN_DIR.toString().concat("\\" + fileName);
            final File newFile = new File(newFilePath);

            FileUtils.writeStringToFile(newFile, text, StandardCharsets.UTF_8);

            return newFilePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String writeToPluginDir(String fileName, String text, Boolean overwrite) {
        try {
            final String newFilePath = PLUGIN_DIR.toString().concat("\\" + fileName);
            final File newFile = new File(newFilePath);

            if (overwrite != true) {
                if (newFile.exists()) {
                    return null;
                }
            }

            FileUtils.writeStringToFile(newFile, text, StandardCharsets.UTF_8);
            return newFilePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getResourceFileContent(String filename) {
        try {
            final InputStream inputStream = PLUGIN_PROVIDER.getResource(filename);
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            return null;
        }
    }

    public static FileInputStream getPluginFileStream(String filename) {
        try {
            return new FileInputStream(getPluginFile(filename));
        } catch (Exception e) {
            return null;
        }
    }

    public static File getPluginFile(String filename) {
        try {
            final String filePath = PLUGIN_DIR.toString().concat("\\" + filename);
            return new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getPluginTraductionFile(String filename) {
        try {
            final String filePath = TRADUCTION_DIR.toString().concat("\\" + filename);
            return new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}