package depen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PredictionStore {
    private final Path filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PredictionStore(String fileName) {
        this.filePath = Path.of(fileName);
    }

    public PredictionModel.State load() {
        if (!Files.exists(filePath)) {
            return new PredictionModel.State();
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            PredictionModel.State state = gson.fromJson(reader, PredictionModel.State.class);
            return state == null ? new PredictionModel.State() : state;
        } catch (IOException e) {
            System.out.println("Error loading ML data: " + e.getMessage());
            return new PredictionModel.State();
        }
    }

    public void save(PredictionModel.State state) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(filePath)) {
                gson.toJson(state, writer);
            }
        } catch (IOException e) {
            System.out.println("Error saving ML data: " + e.getMessage());
        }
    }
}
