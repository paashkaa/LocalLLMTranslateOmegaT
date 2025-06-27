# LocalLLMTranslate Plug-in for OmegaT

The LocalLLMTranslate plugin is designed for use with the OmegaT CAT tool. It allows you to leverage a locally (or remotely) hosted LLM running in LM Studio for translation.

Originally based on the [OpenAI Plug-in for OmegaT](https://github.com/ychoi-kr/omegat-plugin-openai-translate)

### 1. Install the Plug-in

1. Download the plug-in file.
2. Copy the plug-in file to the appropriate directory based on your operating system:

    - **Windows**: Copy the plug-in file into `%SystemDrive%%ProgramFiles%\OmegaT\plugins` directory.
    - **macOS**: Copy the plug-in file into `/Applications/OmegaT.app/Contents/Java/plugins` directory.
    - **GNU/Linux**: Copy the plug-in file under the directory where OmegaT is installed.

### 2. Configure and Settings via the OmegaT UI

1. **Open the Preferences Window**:
    - Open OmegaT.
    - Go to `Options > Preferences`.

2. **Navigate to Machine Translation**:
    - In the Preferences window, select `Machine Translation` from the left-hand menu.

3. **Select LocalAI Translate (LM Studio)**:
    - In the list of available machine translation services, click on `LocalAI Translate (LM Studio)`.

4. **Configure the Plug-in**:
    - Click on the "Configure" button to open the configuration window.
    - Tooltips provide additional help and instructions.

5. **Save Settings**:
    - After entering your settings, click "OK" to save your preferences. These settings will be stored and automatically applied each time you use the `LocalAI Translate (LM Studio)` service within OmegaT.
    - 
### 3. Start Translating

1. Open your project in OmegaT.
2. Use the `LocalAI Translate (LM Studio)` service by selecting it from the `Options > Machine Translation` menu.
3. If desired, you may want to uncheck "Enable Sentence-level Segmenting" in the Options menu to achieve more fluent translations.
