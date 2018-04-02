package net.mamchur.clion.avrdude;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class AVRDudeConfigurationType extends CMakeRunConfigurationType {
    private static final String FACTORY_ID = "net.mamchur.clion.avrdude.conf.factory";
    private static final String TYPE_ID = "net.mamchur.clion.avrdude.conf.type";
    private static final Icon icon = IconLoader.findIcon("run.png", AVRDudeConfigurationType.class);

    private final ConfigurationFactory factory;

    public AVRDudeConfigurationType() {
        super(TYPE_ID, FACTORY_ID, "AVRDude", "ABC", Objects.requireNonNull(icon));

        factory = new ConfigurationFactoryEx(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new AVRDudeConfiguration(project, factory, "");
            }

            @Override
            public boolean isConfigurationSingletonByDefault() {
                return true;
            }

            @Override
            public String getId() {
                return FACTORY_ID;
            }
        };
    }

    @NotNull
    @Override
    protected CMakeAppRunConfiguration createRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory configurationFactory) {
        return new AVRDudeConfiguration(project, factory, "");
    }

    @Override
    public SettingsEditor<? extends CMakeAppRunConfiguration> createEditor(@NotNull Project project) {
        return new AVRDudeSettingsEditor(project, getHelper(project));
    }
}
