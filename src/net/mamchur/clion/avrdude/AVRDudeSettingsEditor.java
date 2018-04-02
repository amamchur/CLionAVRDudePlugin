package net.mamchur.clion.avrdude;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class AVRDudeSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private JBTextField testTextField;

    AVRDudeSettingsEditor(Project project, @NotNull CMakeBuildConfigurationHelper cMakeBuildConfigurationHelper) {
        super(project, cMakeBuildConfigurationHelper);
    }

    @Override
    protected void applyEditorTo(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) throws com.intellij.openapi.options.ConfigurationException {
        super.applyEditorTo(cMakeAppRunConfiguration);
        AVRDudeConfiguration cfg = (AVRDudeConfiguration) cMakeAppRunConfiguration;
        cfg.test = testTextField.getText();
    }

    @Override
    protected void resetEditorFrom(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) {
        super.resetEditorFrom(cMakeAppRunConfiguration);

        AVRDudeConfiguration cfg = (AVRDudeConfiguration) cMakeAppRunConfiguration;
        testTextField.setText(cfg.test);
    }

    @Override
    protected void createEditorInner(JPanel panel, GridBag gridBag) {
        super.createEditorInner(panel, gridBag);

        panel.setBackground(JBColor.YELLOW);
        panel.add(new JBLabel("Begin AVRDude parameters"), gridBag.nextLine().next());
        panel.add(testTextField = new JBTextField(""), gridBag.next().coverLine());
        panel.add(new JBLabel("End AVRDude parameters"), gridBag.nextLine().next());

//        for (Component component : panel.getComponents()) {
//            if (component instanceof CommonProgramParametersPanel) {
//                component.setBackground(new Color(0, 0, 255, 100));
//                component.setVisible(false);
//            }
//        }
    }
}
