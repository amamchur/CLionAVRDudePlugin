package net.mamchur.clion.avrdude;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected void onTargetSelected(@Nullable CMakeTarget target) {
//        this.myConfigHelper.get
        CMakeConfiguration cfg = this.myConfigHelper.getDefaultConfiguration(target);
        String f = cfg.getProductFile().toString();
        System.out.println(f);
    }

    @Override
    protected void createAdditionalControls(JPanel var1, GridBag var2) {
        super.createAdditionalControls(var1, var2);
    }

    @Override
    protected void createEditorInner(JPanel panel, GridBag gridBag) {
        super.createEditorInner(panel, gridBag);

        panel.setBackground(Color.YELLOW);
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
