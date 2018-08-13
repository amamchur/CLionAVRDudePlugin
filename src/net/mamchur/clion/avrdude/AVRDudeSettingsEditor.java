package net.mamchur.clion.avrdude;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AVRDudeSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private JBTextField portTextField;
    private ComboBox<String> programmersComboBox;
    private ComboBox<String> deviceComboBox;
    private DefaultComboBoxModel<String> programmersModel;
    private DefaultComboBoxModel<String> devicesModel;

    AVRDudeSettingsEditor(Project project, @NotNull CMakeBuildConfigurationHelper helper) {
        super(project, helper);
    }

    private void initComboModels() {
        if (programmersModel != null && devicesModel != null) {
            return;
        }


        String[] array = readAvrdudeConfig("-c?");
        programmersModel = new DefaultComboBoxModel<>(array);

        array = readAvrdudeConfig("-p?");
        devicesModel = new DefaultComboBoxModel<>(array);
    }

    private String[] readAvrdudeConfig(String param) {
        Map<String, String> env = System.getenv();
        String pathEvn = env.getOrDefault("PATH", null);
        if (pathEvn == null) {
            return new String[]{};
        }

        String[] paths = pathEvn.split("[:;]", 0);
        File avrdudeBin = null;
        for (String str : paths) {
            VirtualFile folder = LocalFileSystem.getInstance().findFileByPath(str);
            if (folder == null) {
                continue;
            }

            VirtualFile avrdudeBinary = folder.findFileByRelativePath("avrdude");
            if (avrdudeBinary == null) {
                continue;
            }

            avrdudeBin = VfsUtil.virtualToIoFile(avrdudeBinary);
            break;
        }

        if (avrdudeBin == null) {
            return new String[]{};
        }

        String[] result;
        try {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add(avrdudeBin.getAbsolutePath());
            arguments.add(param);

            ProcessBuilder pb = new ProcessBuilder(arguments);
            pb.redirectErrorStream(true);

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
            result = list.toArray(new String[0]);
        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
            result = new String[]{e1.getMessage()};
        }

        return result;
    }

    @Override
    protected void applyEditorTo(@NotNull CMakeAppRunConfiguration runConfiguration) throws ConfigurationException {
        super.applyEditorTo(runConfiguration);
        AVRDudeConfiguration cfg = (AVRDudeConfiguration) runConfiguration;
        cfg.setPort(portTextField.getText());
        cfg.setProgrammer((String) programmersComboBox.getSelectedItem());
        cfg.setDevice((String) deviceComboBox.getSelectedItem());
    }

    @Override
    protected void resetEditorFrom(@NotNull CMakeAppRunConfiguration runConfiguration) {
        super.resetEditorFrom(runConfiguration);

        AVRDudeConfiguration cfg = (AVRDudeConfiguration) runConfiguration;
        portTextField.setText(cfg.getPort());
        programmersComboBox.setSelectedItem(cfg.getProgrammer());
        deviceComboBox.setSelectedItem(cfg.getDevice());
    }

    @Override
    protected void createAdditionalControls(JPanel panel, GridBag gridBag) {
        super.createAdditionalControls(panel, gridBag);

        initComboModels();

        programmersComboBox = new ComboBox<>();
        programmersComboBox.setModel(programmersModel);
        programmersComboBox.setEditable(true);

        panel.add(new JBLabel("Programmer"), gridBag.nextLine().next());
        panel.add(programmersComboBox, gridBag.next().coverLine());

        deviceComboBox = new ComboBox<>();
        deviceComboBox.setModel(devicesModel);
        deviceComboBox.setEditable(true);

        panel.add(new JBLabel("Device"), gridBag.nextLine().next());
        panel.add(deviceComboBox, gridBag.next().coverLine());

        panel.add(new JBLabel("Port"), gridBag.nextLine().next());
        panel.add(portTextField = new JBTextField(""), gridBag.next().coverLine());
    }
}
