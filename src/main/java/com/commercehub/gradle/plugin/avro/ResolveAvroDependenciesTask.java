package com.commercehub.gradle.plugin.avro;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.avro.Schema;
import org.gradle.api.GradleException;
import org.gradle.api.file.FileCollection;
import org.gradle.api.specs.NotSpec;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;

import static com.commercehub.gradle.plugin.avro.Constants.SCHEMA_EXTENSION;

/**
 * Task to read Avro schema files, resolve their dependencies, and write out dependency-free Avro schema files.
 */
@CacheableTask
public class ResolveAvroDependenciesTask extends OutputDirTask {
    private final SchemaResolver resolver = new SchemaResolver(getProject(), getLogger());

    @TaskAction
    protected void process() {
        getLogger().info("Found {} files", getInputs().getSourceFiles().getFiles().size());
        failOnUnsupportedFiles();
        processFiles();
    }

    private void failOnUnsupportedFiles() {
        FileCollection unsupportedFiles = filterSources(new NotSpec<>(new FileExtensionSpec(SCHEMA_EXTENSION)));
        if (!unsupportedFiles.isEmpty()) {
            throw new GradleException(
                String.format("Unsupported file extension for the following files: %s", unsupportedFiles));
        }
    }

    private void processFiles() {
        int processedFileCount = processSchemaFiles();
        setDidWork(processedFileCount > 0);
    }

    private int processSchemaFiles() {
        Set<File> inputFiles = filterSources(new FileExtensionSpec(SCHEMA_EXTENSION)).getFiles();
        ProcessingState processingState = resolver.resolve(inputFiles);
        for (Schema schema : processingState.getSchemas()) {
            try {
                String outputPath = schema.getNamespace().replaceAll(Pattern.quote("."), "/")
                    + "/" + schema.getName() + "." + SCHEMA_EXTENSION;
                File outputFile = new File(getOutputDir().get().getAsFile(), outputPath);
                String schemaJson = schema.toString(true);
                FileUtils.writeJsonFile(outputFile, schemaJson);
                getLogger().debug("Wrote {}", outputFile.getPath());
            } catch (IOException ex) {
                throw new GradleException(String.format("Failed to write resolved schema definition for %s", schema.getFullName()), ex);
            }
        }
        return processingState.getProcessedTotal();
    }
}
