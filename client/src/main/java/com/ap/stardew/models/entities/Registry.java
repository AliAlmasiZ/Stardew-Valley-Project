package com.ap.stardew.models.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class Registry <T> {

    protected final Map<String, T> registry = new HashMap<>();

    abstract protected void loadJson(JsonNode jsonRoot, ObjectMapper mapper, Path path) throws IOException;
    abstract public T getData(String name);
    public void load(String pathStr){
        Path root = Paths.get(pathStr);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (Stream<Path> files = Files.walk(root)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                String regAddress = root.relativize(path).toString().replace(File.separator, ":");
//                regAddress = regAddress.substring(0, regAddress.lastIndexOf(":"));

                try {
                    JsonNode jsonRoot;
                    try {
                        jsonRoot = mapper.readTree(path.toFile());
                    } catch (IOException e) {
                        System.err.println("there was a problem int the file " + path.toString());
                        throw new RuntimeException(e);
                    }
                    this.loadJson(jsonRoot, mapper, path);
                } catch (IOException e) {
                    System.err.println("----------------------------------------------------------------------");
                    System.err.println("Error in reading " + path);
                    System.err.println("----------------------------------------------------------------------\nlogs:");
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
