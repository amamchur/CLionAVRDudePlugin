package net.mamchur.clion.avrdude;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.execution.CidrCommandLineState;
import com.jetbrains.cidr.execution.CidrExecutableDataHolder;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AVRDudeConfiguration extends CMakeAppRunConfiguration implements CidrExecutableDataHolder {
    String test = "avrdude-test def value";


    @SuppressWarnings("WeakerAccess")
    public AVRDudeConfiguration(Project project, ConfigurationFactory configurationFactory, String targetName) {
        super(project, configurationFactory, targetName);
    }

    @Nullable
    @Override
    public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new CidrCommandLineState(environment, new AVRDudeLauncher(this));
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute("avrdude-test", String.valueOf(test));
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

        test = element.getAttributeValue("avrdude-test");
    }
}