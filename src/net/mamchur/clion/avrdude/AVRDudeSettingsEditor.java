package net.mamchur.clion.avrdude;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.FixedSizeButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AVRDudeSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private JBTextField portTextField;
    private ComboBox<String> programmersComboBox;
    private ComboBox<String> deviceComboBox;
    private DefaultComboBoxModel<String> programmersModel = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> devicesModel = new DefaultComboBoxModel<>();

    AVRDudeSettingsEditor(Project project, @NotNull CMakeBuildConfigurationHelper cMakeBuildConfigurationHelper) {
        super(project, cMakeBuildConfigurationHelper);

        String[] array = readAvrdudeConfig("-c?");
        programmersModel = new DefaultComboBoxModel<>(array);

        array = readAvrdudeConfig("-p?");
        devicesModel = new DefaultComboBoxModel<>(array);
    }

    private String[] readAvrdudeConfig(String param) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add("avrdude");
        arguments.add(param);

        ProcessBuilder pb = new ProcessBuilder(arguments);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            InputStream stream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            ArrayList<String> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                Pattern p = Pattern.compile("^\\s+(.+?)\\s*=\\s*(.+)$");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    list.add(m.group(1));
                }
            }
            process.waitFor();
            return list.toArray(new String[0]);
        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }

        return new String[0];
    }

    @Override
    protected void applyEditorTo(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) throws com.intellij.openapi.options.ConfigurationException {
        super.applyEditorTo(cMakeAppRunConfiguration);
        AVRDudeConfiguration cfg = (AVRDudeConfiguration) cMakeAppRunConfiguration;
        cfg.setPort(portTextField.getText());
        cfg.setProgrammer((String) programmersComboBox.getSelectedItem());
        cfg.setDevice((String) deviceComboBox.getSelectedItem());
    }

    @Override
    protected void resetEditorFrom(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) {
        super.resetEditorFrom(cMakeAppRunConfiguration);

        AVRDudeConfiguration cfg = (AVRDudeConfiguration) cMakeAppRunConfiguration;
        portTextField.setText(cfg.getPort());

        int index = programmersModel.getIndexOf(cfg.getProgrammer());
        if (index != -1) {
            programmersComboBox.setSelectedIndex(index);
        } else {
            String[] strings = {cfg.getProgrammer()};
            programmersModel = new DefaultComboBoxModel<>(strings);
            programmersComboBox.setModel(programmersModel);
            programmersComboBox.setSelectedIndex(0);
        }

        index = devicesModel.getIndexOf(cfg.getDevice());
        if (index != -1) {
            deviceComboBox.setSelectedIndex(index);
        } else {
            String[] strings = {cfg.getDevice()};
            devicesModel = new DefaultComboBoxModel<>(strings);
            deviceComboBox.setModel(devicesModel);
            deviceComboBox.setSelectedIndex(0);
        }
    }

    @Override
    protected void createAdditionalControls(JPanel panel, GridBag gridBag) {
        super.createAdditionalControls(panel, gridBag);

        programmersComboBox = new ComboBox<>();
        programmersComboBox.setModel(programmersModel);
        programmersComboBox.setEditable(true);

        panel.add(new JBLabel("Programmer"), gridBag.nextLine().next());
        panel.add(programmersComboBox,gridBag.next().coverLine());

        deviceComboBox = new ComboBox<>();
        deviceComboBox.setModel(devicesModel);
        deviceComboBox.setEditable(true);

        panel.add(new JBLabel("Device"), gridBag.nextLine().next());
        panel.add(deviceComboBox,gridBag.next().coverLine());

        panel.add(new JBLabel("Port"), gridBag.nextLine().next());
        panel.add(portTextField = new JBTextField(""), gridBag.next().coverLine());
    }
}
