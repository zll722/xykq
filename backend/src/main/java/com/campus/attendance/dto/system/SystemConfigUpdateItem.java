package com.campus.attendance.dto.system;

import jakarta.validation.constraints.NotBlank;

public class SystemConfigUpdateItem {
    @NotBlank
    private String configKey;
    @NotBlank
    private String configValue;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
