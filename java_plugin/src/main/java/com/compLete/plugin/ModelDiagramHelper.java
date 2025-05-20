package com.compLete.plugin;

import com.nomagic.magicdraw.uml.DiagramManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

public class ModelDiagramHelper {
    public static void createInternalBlockDiagram(Element context) {
        DiagramManager.getInstance()
            .createDiagram("Internal Block Diagram", context);
    }
}