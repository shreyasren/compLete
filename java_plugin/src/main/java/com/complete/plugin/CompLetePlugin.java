package com.complete.plugin;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.plugins.PluginDescriptor;
import javax.swing.*;
import com.complete.plugin.HttpRequestException;

public class CompLetePlugin extends Plugin {
    private SysMLModelService service;

    @Override
    public void init() {
        service = new SysMLModelService();
        PluginDescriptor pd = getDescriptor();
        JMenuItem mi = new JMenuItem("AI ▶ Complete Model");
        mi.addActionListener(e -> runCompletion());
        pd.getRibbonPanel().add(mi);
    }

    private void runCompletion() {
        try {
            String ctx = service.extractModelContext();
            String json = service.requestCompletion(ctx, "Complete the model");
            service.applySuggestions(json);
        } catch (HttpRequestException hre) {
            JOptionPane.showMessageDialog(
                null,
                "Middleware error: " + hre.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                null,
                "compLete error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public boolean close() { return true; }
    @Override
    public boolean isSupported() { return true; }
}
