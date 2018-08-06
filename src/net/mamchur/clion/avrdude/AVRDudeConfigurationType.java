package net.mamchur.clion.avrdude;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType;
import kotlin.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AVRDudeConfigurationType extends CMakeRunConfigurationType {
    private static final String FACTORY_ID = "net.mamchur.clion.avrdude.conf.factory";
    private static final String TYPE_ID = "net.mamchur.clion.avrdude.conf.type";
    private final ConfigurationFactory factory;

    public AVRDudeConfigurationType() {
        super(TYPE_ID, FACTORY_ID, "AVRDude", "Upload app using avrdude", new Lazy<Icon>() {
                    Icon icon;

                    @Override
                    public Icon getValue() {
                        if (icon == null) {
                            icon = IconLoader.findIcon("run.png", AVRDudeConfigurationType.class);
                        }
                        return icon;
                    }

                    @Override
                    public boolean isInitialized() {
                        return icon != null;
                    }
                }
        );

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
