package net.mamchur.clion.avrdude;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AVRDudeLauncher extends CidrLauncher {
    private final AVRDudeConfiguration configuration;

    AVRDudeLauncher(AVRDudeConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {
        String targetName = commandLineState.getExecutionTarget().getDisplayName();
        CMakeAppRunConfiguration.BuildAndRunConfigurations runCfg = configuration.getBuildAndRunConfigurations(targetName);

        Project project = commandLineState.getEnvironment().getProject();
        GeneralCommandLine commandLine = new PtyCommandLine();
        String path = project.getBaseDir().getPath();
        File runFile = Objects.requireNonNull(runCfg).getRunFile();

        AVRDudeLaunchOptions options = getAVRDudeOptions(runFile);
        if (options == null) {
            throw new ExecutionException("Binary file not found");
        }

        ArrayList<String> params = new ArrayList<>();
        params.add("-p");
        params.add(options.getMcu());
        params.add("-c");
        params.add(options.getProgrammer());
        params.add(options.getUploadFlashParam());

        commandLine
                .withWorkDirectory(path)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .withParameters(params)
                .withExePath("avrdude");

        OSProcessHandler handler = new OSProcessHandler(commandLine);
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                super.processTerminated(event);
            }
        });
        return handler;
    }

    @Nullable
    private AVRDudeLaunchOptions getAVRDudeOptions(File runFile) {
        String name = Objects.requireNonNull(runFile).getName();
        String pattern = "^" + name + "-(.+)\\.bin$";
        File directory = runFile.getParentFile();
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }

            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(file.getName());
            if (m.find()) {
                return new AVRDudeLaunchOptions(file.getAbsolutePath(), m.group(1));
            }
        }

        return null;
    }

    @NotNull
    @Override
    protected CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        throw new ExecutionException("Debug is not supported by AVRDude");
    }

    @NotNull
    @Override
    protected Project getProject() {
        return configuration.getProject();
    }
}
