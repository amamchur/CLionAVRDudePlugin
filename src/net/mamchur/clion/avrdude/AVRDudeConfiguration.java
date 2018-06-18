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
    private static final String NODE_AVRDUDE = "avrdude";
    private static final String ATTRIBUTE_PORT = "port";
    private static final String ATTRIBUTE_PROGRAMMER = "programmer";
    private static final String ATTRIBUTE_DEVICE = "device";

    private String programmer = "usbasp";
    private String port = "";
    private String device = "";

    public String getProgrammer() {
        return programmer;
    }

    public void setProgrammer(String programmer) {
        this.programmer = programmer;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

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

        Element scriptElement = new Element(NODE_AVRDUDE);
        element.addContent(scriptElement);

        if (port != null) {
            scriptElement.setAttribute(ATTRIBUTE_PORT, port);
        }

        if (programmer != null) {
            scriptElement.setAttribute(ATTRIBUTE_PROGRAMMER, programmer);
        }

        if (device != null) {
            scriptElement.setAttribute(ATTRIBUTE_DEVICE, device);
        }
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

        Element scriptElement = element.getChild(NODE_AVRDUDE);
        if (scriptElement == null) {
            return;
        }

        port = scriptElement.getAttributeValue(ATTRIBUTE_PORT);
        programmer = scriptElement.getAttributeValue(ATTRIBUTE_PROGRAMMER);
        device = scriptElement.getAttributeValue(ATTRIBUTE_DEVICE);
    }
}