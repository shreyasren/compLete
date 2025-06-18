package com.complete.plugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.ModelElementsManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.StereotypesHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SysMLModelService {
    private final Project project;
    private final HttpClientHelper http;

    public SysMLModelService() {
        this.project = Application.getInstance().getProject();
        // load config
        String url = ConfigLoader.load("middleware.url");
        String key = ConfigLoader.load("middleware.apiKey");
        int ct = Integer.parseInt(ConfigLoader.load("middleware.connectTimeout"));
        int rt = Integer.parseInt(ConfigLoader.load("middleware.readTimeout"));
        this.http = new HttpClientHelper(url, key, ct, rt);
    }

    public String extractModelContext() {
        StringBuilder sb = new StringBuilder();
        for(Element e: project.getModel().getOwnedElement()) {
            if(e instanceof Requirement) {
                Requirement r = (Requirement)e;
                boolean sat = !r.getSatisfaction().isEmpty();
                sb.append(r.getName()).append(sat?" (satisfied)":" (UNSAT)").append("\n");
            }
        }
        return sb.toString();
    }

    public String requestCompletion(String context, String req) throws Exception {
        JSONObject body = new JSONObject();
        body.put("model_context", context);
        body.put("request", req);
        return http.post("/complete_model", body);
    }

    public void applySuggestions(String json) throws Exception {
        JSONObject s = new JSONObject(json);
        SessionManager.getInstance().createSession(project, "AI Completion");
        try {
            Map<String, NamedElement> created = new HashMap<>();

        // Blocks
        JSONArray newBlocks = s.optJSONArray("new_blocks");
        if (newBlocks != null) {
            for (Object ob : newBlocks) {
                JSONObject b = (JSONObject) ob;
                Class block = ModelElementsManager.getInstance()
                    .createElement(Class.class, project.getModel());
                StereotypesHelper.addStereotypeByString(block, "Block");
                block.setName(b.getString("name"));
                created.put(b.getString("name"), block);
            }
        }

        // Connections
        JSONArray newConnections = s.optJSONArray("new_connections");
        if (newConnections != null) {
            for (Object oc : newConnections) {
                JSONObject c = (JSONObject) oc;
                NamedElement from = created.getOrDefault(c.getString("from"), null);
                NamedElement to = created.getOrDefault(c.getString("to"), null);
                String type = c.getString("type");
                if ("satisfy".equalsIgnoreCase(type) && from instanceof Requirement && to != null) {
                    Dependency d = ModelElementsManager.getInstance()
                        .createDependency(project.getModel());
                    StereotypesHelper.addStereotypeByString(d, "satisfy");
                    d.getClients().add(from);
                    d.getSuppliers().add(to);
                } else if ("connector".equalsIgnoreCase(type)) {
                    // simple association
                    if (from instanceof Class && to instanceof Class) {
                        ModelElementsManager.getInstance()
                            .createAssociation((Class) from, (Class) to);
                    }
                }
            }
        }
        }
        finally {
            SessionManager.getInstance().closeSession(project);
        }
    }
}
