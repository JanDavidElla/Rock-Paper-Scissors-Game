package depen;

import depen.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataLoader implements DataStorage {

    private final String filePath;
    private final String fileName;

    public DataLoader(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public boolean store(Gson gson, Player[] players) {
        Path resolvedPath = resolveWritePath();

        try {
            Path parent = resolvedPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            System.out.println("Error storing data: " + e.getMessage());
            return false;
        }

        try (Writer writer = new FileWriter(resolvedPath.toFile())) {
            gson.toJson(players, writer);
        } catch (Exception e) {
            System.out.println("Error storing data: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Player[] load(Gson gson) {
        Path resolvedPath = resolveReadPath();

        if (!Files.exists(resolvedPath)) {
            System.out.println("Data file not found at " + resolvedPath + ". Starting with no saved players.");
            return new Player[0];
        }

        try(FileReader reader = new FileReader(resolvedPath.toFile())) {
            Player[] players = gson.fromJson(reader, Player[].class);
            return players == null ? new Player[0] : players;
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            return new Player[0];
        }
    }

    public boolean load_example(){
        Player player = new Player("67", "Andrew");
        Player player1 = new Player("68", "Ron");
        Player player2 = new Player("69", "Jan");
    
        Player[] players = {player, player1, player2};
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(this.filePath + "/" + this.fileName)) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Path resolveReadPath() {
        for (Path candidate : getCandidatePaths()) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        return resolveWritePath();
    }

    private Path resolveWritePath() {
        Path configuredPath = getConfiguredPath();
        Path configuredDirectory = Paths.get(filePath);

        if (configuredDirectory.isAbsolute() || Files.exists(configuredPath)) {
            return configuredPath;
        }

        Path projectRoot = inferProjectRoot();
        if (projectRoot != null) {
            Path projectScopedPath = projectRoot.resolve(filePath).resolve(fileName).toAbsolutePath().normalize();
            if (Files.exists(projectScopedPath) || ".".equals(filePath) || filePath.isBlank()) {
                return projectScopedPath;
            }
        }

        return configuredPath;
    }

    private Set<Path> getCandidatePaths() {
        Set<Path> candidates = new LinkedHashSet<>();
        Path configuredPath = getConfiguredPath();
        candidates.add(configuredPath);

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        candidates.add(cwd.resolve(fileName).normalize());
        candidates.add(cwd.resolve("Rock-Paper-Scissors-Game").resolve(fileName).normalize());

        Path projectRoot = inferProjectRoot();
        if (projectRoot != null) {
            if (!Paths.get(filePath).isAbsolute()) {
                candidates.add(projectRoot.resolve(filePath).resolve(fileName).toAbsolutePath().normalize());
            }
            candidates.add(projectRoot.resolve(fileName).normalize());
        }

        return candidates;
    }

    private Path getConfiguredPath() {
        return Paths.get(filePath, fileName).toAbsolutePath().normalize();
    }

    private Path inferProjectRoot() {
        try {
            Path codeSourcePath = Paths.get(DataLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();

            if (Files.isRegularFile(codeSourcePath)) {
                Path jarDirectory = codeSourcePath.getParent();
                if (jarDirectory != null) {
                    if ("target".equals(jarDirectory.getFileName().toString()) && jarDirectory.getParent() != null) {
                        return jarDirectory.getParent();
                    }
                    return jarDirectory;
                }
                return null;
            }

            Path targetDirectory = codeSourcePath.getParent();
            if (targetDirectory != null && "target".equals(targetDirectory.getFileName().toString()) && targetDirectory.getParent() != null) {
                return targetDirectory.getParent();
            }

            return codeSourcePath;
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
