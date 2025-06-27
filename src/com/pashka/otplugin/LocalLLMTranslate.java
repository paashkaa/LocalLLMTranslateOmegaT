package com.pashka.otplugin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omegat.core.Core;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.machinetranslators.BaseCachedTranslate;
import org.omegat.gui.exttrans.MTConfigDialog;
import org.omegat.gui.glossary.GlossaryEntry;
import org.omegat.gui.glossary.GlossarySearcher;
import org.omegat.util.Language;
import org.omegat.util.Preferences;
import org.omegat.util.WikiGet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class LocalLLMTranslate extends BaseCachedTranslate {

    private static final String BASE_PROMPT =
            "You are a translation tool integrated in a CAT (Computer-Assisted Translation) tool. Translate the following text from %s to %s. Preserve the tags in the text and keep any segmentations intact.\n\n";

    private static final String PARAM_MODEL = "localllm.model";
    private static final String PARAM_TEMPERATURE = "localllm.temperature";
    private static final String PARAM_CUSTOM_PROMPT = "custom.prompt";
    private static final String PARAM_USE_GLOSSARY = "localllm.use.glossary";
    private static final String PARAM_API_URL = "localllm.api.url";
    private static final String PARAM_COMBINE_PROMPT = "localllm.combine.prompt";

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final String DEFAULT_TEMPERATURE = "0";
    private static final String DEFAULT_CUSTOM_PROMPT = "";
    private static final String DEFAULT_API_URL = "http://localhost:1234/v1/chat/completions";
    private static final String DEFAULT_USE_GLOSSARY = "true";
    private static final String DEFAULT_COMBINE_PROMPT = "false";

    private final ResourceBundle bundle = ResourceBundle.getBundle("com.pashka.otplugin.Bundle", Locale.getDefault(),
            ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES));

    private JTextField apiUrlField;
    private JTextField modelField;
    private JTextField temperatureField;
    private JTextArea promptField;
    private JCheckBox glossaryCheckbox;
    private JCheckBox combinePromptCheckbox;

    @Override
    protected String getPreferenceName() {
        return "allow_localllm_translate";
    }

    @Override
    public String getName() {
        return "LocalAI Translate (LM Studio)";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void showConfigurationUI(Window parent) {
        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c;
        int row = 0;

        // API URL
        JLabel apiUrlLabel = new JLabel(bundle.getString("label.apiurl"));
        apiUrlLabel.setToolTipText(bundle.getString("tooltip.apiurl"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        configPanel.add(apiUrlLabel, c);

        apiUrlField = new JTextField(Preferences.getPreferenceDefault(PARAM_API_URL, DEFAULT_API_URL));
        apiUrlField.setToolTipText(bundle.getString("tooltip.apiurl"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        configPanel.add(apiUrlField, c);

        // Model
        JLabel modelLabel = new JLabel(bundle.getString("label.model"));
        modelLabel.setToolTipText(bundle.getString("tooltip.model"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        configPanel.add(modelLabel, c);

        modelField = new JTextField(Preferences.getPreferenceDefault(PARAM_MODEL, DEFAULT_MODEL));
        modelField.setToolTipText(bundle.getString("tooltip.model"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        configPanel.add(modelField, c);

        // Temperature
        JLabel tempLabel = new JLabel(bundle.getString("label.temperature"));
        tempLabel.setToolTipText(bundle.getString("tooltip.temperature"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        configPanel.add(tempLabel, c);

        temperatureField = new JTextField(Preferences.getPreferenceDefault(PARAM_TEMPERATURE, DEFAULT_TEMPERATURE));
        temperatureField.setToolTipText(bundle.getString("tooltip.temperature"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        configPanel.add(temperatureField, c);

        // Custom Prompt
        JLabel promptLabel = new JLabel(bundle.getString("label.prompt"));
        promptLabel.setToolTipText(bundle.getString("tooltip.prompt"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.NORTHWEST;
        configPanel.add(promptLabel, c);

        promptField = new JTextArea(5, 20);
        promptField.setText(Preferences.getPreferenceDefault(PARAM_CUSTOM_PROMPT, DEFAULT_CUSTOM_PROMPT));
        promptField.setToolTipText(bundle.getString("tooltip.prompt"));
        promptField.setLineWrap(true);
        promptField.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(promptField);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        configPanel.add(scroll, c);

        // Use glossary
        glossaryCheckbox = new JCheckBox(bundle.getString("label.glossary"));
        glossaryCheckbox.setToolTipText(bundle.getString("tooltip.glossary"));
        glossaryCheckbox.setSelected(Boolean.parseBoolean(Preferences.getPreferenceDefault(PARAM_USE_GLOSSARY, DEFAULT_USE_GLOSSARY)));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        configPanel.add(glossaryCheckbox, c);

        // Combine Prompt
        combinePromptCheckbox = new JCheckBox(bundle.getString("label.combineprompt"));
        combinePromptCheckbox.setToolTipText(bundle.getString("tooltip.combineprompt"));
        combinePromptCheckbox.setSelected(Boolean.parseBoolean(Preferences.getPreferenceDefault(PARAM_COMBINE_PROMPT, DEFAULT_COMBINE_PROMPT)));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        configPanel.add(combinePromptCheckbox, c);

        MTConfigDialog dialog = new MTConfigDialog(parent, getName()) {
            @Override
            protected void onConfirm() {
                Preferences.setPreference(PARAM_MODEL, modelField.getText());
                Preferences.setPreference(PARAM_TEMPERATURE, temperatureField.getText());
                Preferences.setPreference(PARAM_CUSTOM_PROMPT, promptField.getText());
                Preferences.setPreference(PARAM_API_URL, apiUrlField.getText());
                Preferences.setPreference(PARAM_USE_GLOSSARY, glossaryCheckbox.isSelected());
                Preferences.setPreference(PARAM_COMBINE_PROMPT, combinePromptCheckbox.isSelected());
            }
        };

        dialog.panel.add(configPanel);
        dialog.show();
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String model = Preferences.getPreferenceDefault(PARAM_MODEL, DEFAULT_MODEL);
        float temperature = Float.parseFloat(Preferences.getPreferenceDefault(PARAM_TEMPERATURE, DEFAULT_TEMPERATURE));
        boolean combinePrompt = Boolean.parseBoolean(Preferences.getPreferenceDefault(PARAM_COMBINE_PROMPT, DEFAULT_COMBINE_PROMPT));

        List<GlossaryEntry> glossaryEntries = new ArrayList<>();
        List<SourceTextEntry> entries = Core.getProject().getAllEntries();
        SourceTextEntry matchingEntry = entries.stream().filter(e -> e.getSrcText().equals(text)).findFirst().orElse(null);

        if (matchingEntry != null) {
            GlossarySearcher glossarySearcher = new GlossarySearcher(Core.getProject().getSourceTokenizer(), sLang, true);
            glossaryEntries = glossarySearcher.searchSourceMatches(matchingEntry, Core.getGlossaryManager().getGlossaryEntries(text));
        }

        String systemPrompt = createSystemPrompt(sLang, tLang, glossaryEntries);

        return combinePrompt
                ? requestTranslationCombined(systemPrompt, text, model, temperature)
                : requestTranslation(systemPrompt, text, model, temperature);
    }

    private String createSystemPrompt(Language sLang, Language tLang, List<GlossaryEntry> glossaryEntries) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format(BASE_PROMPT, sLang.getLanguage(), tLang.getLanguage()));

        boolean useGlossary = Boolean.parseBoolean(Preferences.getPreferenceDefault(PARAM_USE_GLOSSARY, DEFAULT_USE_GLOSSARY));
        String customPrompt = Preferences.getPreferenceDefault(PARAM_CUSTOM_PROMPT, DEFAULT_CUSTOM_PROMPT);

        if (useGlossary && !glossaryEntries.isEmpty()) {
            promptBuilder.append("Glossary:\n");
            for (GlossaryEntry entry : glossaryEntries) {
                String[] locTerms = entry.getLocTerms(false);
                promptBuilder.append(entry.getSrcText()).append("\t").append(locTerms.length > 0 ? locTerms[0] : "").append("\n");
            }
        }

        if (!customPrompt.isEmpty()) {
            promptBuilder.append("\n").append(customPrompt);
        }

        return promptBuilder.toString();
    }

    private String requestTranslation(String systemPrompt, String userPrompt, String model, float temperature) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
        messages.put(new JSONObject().put("role", "user").put("content", userPrompt));

        return sendRequest(messages, model, temperature);
    }

    private String requestTranslationCombined(String systemPrompt, String userPrompt, String model, float temperature) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", systemPrompt + "\n\n" + userPrompt));
        return sendRequest(messages, model, temperature);
    }

    private String sendRequest(JSONArray messages, String model, float temperature) throws Exception {
        Map<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "application/json");

        String body = new JSONObject()
                .put("model", model)
                .put("messages", messages)
                .put("temperature", temperature)
                .toString();

        try {
            String apiUrl = Preferences.getPreferenceDefault(PARAM_API_URL, DEFAULT_API_URL);
            String response = WikiGet.postJSON(apiUrl, body, headers);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            }
            return "Translation failed";
        } catch (Exception e) {
            return "Error contacting local model: " + e.getMessage();
        }
    }
}