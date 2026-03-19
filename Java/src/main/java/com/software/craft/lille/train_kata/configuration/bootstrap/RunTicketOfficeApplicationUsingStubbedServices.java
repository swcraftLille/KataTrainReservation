package com.software.craft.lille.train_kata.configuration.bootstrap;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RunTicketOfficeApplicationUsingStubbedServices implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunTicketOfficeApplicationUsingStubbedServices.class);
    private final List<Process> runningTrainServices = new ArrayList<>();
    private final List<Path> extractedFiles = new ArrayList<>();
    private Path tempDirectory;


    @Override
    public void run(String... args) {
        try {
            tempDirectory = Files.createTempDirectory("stubbed-train-services-");

            final Path trainDataServiceScript = extractToTempDirectory(Path.of("stubbed_services","train_data_service","start_service.py"));
            extractToTempDirectory(Path.of("stubbed_services","train_data_service","trains.json"));
            extractToTempDirectory(Path.of("stubbed_services", "train_data_service", "train_data_service_cherrypy.py"));
            extractToTempDirectory(Path.of("stubbed_services", "train_data_service", "train_data_service.py"));

            final Path bookingReferenceScript = extractToTempDirectory(Path.of("stubbed_services", "booking_reference_service", "booking_reference_service.py"));

            final List<Path> scripts = List.of(trainDataServiceScript, bookingReferenceScript);
            for (Path script : scripts) {
                ProcessBuilder pb = new ProcessBuilder(
                        resolvePythonCommand(),
                        script.getFileName().toString()
                );

                pb.directory(tempDirectory.toFile());
                pb.inheritIO();
                runningTrainServices.add(pb.start());

                LOGGER.info("Started {}", script);
            }

        } catch (IOException ioException) {
            LOGGER.warn("[{}] Some stubbed train services are not running.", getClass().getSimpleName(), ioException);
        }
    }

    @PreDestroy
    public void stopServices() {
        LOGGER.info("Stopping stubbed services...");

        for (Process process : runningTrainServices) {
            process.destroy();
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }

        extractedFiles.forEach(path -> {
            if (path.toFile().delete()) {
                LOGGER.info("[{}] '{}' has been deleted", getClass().getSimpleName(), path);
            } else {
                LOGGER.warn("[{}] Cannot delete file '{}'", getClass().getSimpleName(), path);
            }
        });

        LOGGER.info("Stubbed services stopped.");
    }

    private Path extractToTempDirectory(Path resourcePath) throws IOException {
        final ClassPathResource resource = new ClassPathResource(resourcePath.toString());

        if (!resource.exists()) {
            throw new IllegalArgumentException("Resource not found '%s'".formatted(resourcePath));
        }

        final Path targetFile = tempDirectory.resolve(resourcePath.getFileName().toString());

        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!targetFile.toFile().setExecutable(true)) {
            throw new IllegalArgumentException("Script '%s' cannot be executed".formatted(targetFile));
        }
        extractedFiles.add(targetFile);

        return targetFile;
    }

    private String resolvePythonCommand() {
        return Optional.ofNullable(System.getProperty("os.name"))
                .filter("windows"::equalsIgnoreCase)
                .map(windows -> "python")
                .orElse("python3");
    }
}
