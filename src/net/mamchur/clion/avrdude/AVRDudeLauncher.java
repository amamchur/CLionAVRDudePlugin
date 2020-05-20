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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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
        String path = project.getBasePath();
        File runFile = Objects.requireNonNull(runCfg).getRunFile();

        AVRDudeLaunchOptions options = new AVRDudeLaunchOptions(runFile.getAbsolutePath());

        String parameters = configuration.getProgramParameters();
        String[] args = new String[0];
        if (parameters != null) {
            args = parameters.split(" ", -1);
        }

        ArrayList<String> params = new ArrayList<>();
        params.add("-p");
        params.add(configuration.getDevice());
        params.add("-c");
        params.add(configuration.getProgrammer());

        String port = configuration.getPort();
        if (StringUtils.isNotBlank(port)) {
            params.add("-P");
            params.add(port);
        }

        Collections.addAll(params, args);
        params.add(options.getUploadFlashParam());

        GeneralCommandLine commandLine = new PtyCommandLine();
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
