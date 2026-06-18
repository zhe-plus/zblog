package com.zblog.service;

import com.zblog.entity.Setting;
import com.zblog.enums.SettingKey;
import com.zblog.repository.SettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SettingService {

    private static final Logger log = LoggerFactory.getLogger(SettingService.class);
    private final SettingRepository settingRepository;
    private final ObjectMapper objectMapper;

    public SettingService(SettingRepository settingRepository, ObjectMapper objectMapper) {
        this.settingRepository = settingRepository;
        this.objectMapper = objectMapper;
    }

    public String getString(SettingKey key) {
        return getOrDefault(key, key.getDefaultValue());
    }

    public int getInteger(SettingKey key) {
        String val = getOrDefault(key, key.getDefaultValue());
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer for key {}: {}", key.getKey(), val);
            return Integer.parseInt(key.getDefaultValue());
        }
    }

    public boolean getBoolean(SettingKey key) {
        String val = getOrDefault(key, key.getDefaultValue());
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    public <T> T getJson(SettingKey key, Class<T> clazz) {
        String val = getOrDefault(key, key.getDefaultValue());
        try {
            return objectMapper.readValue(val, clazz);
        } catch (Exception e) {
            log.warn("Failed to parse JSON for key {}: {}", key.getKey(), val);
            try {
                return objectMapper.readValue(key.getDefaultValue(), clazz);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    @Transactional
    public void set(SettingKey key, String value) {
        validate(key, value);
        Setting setting = settingRepository.findById(key.getKey())
            .orElse(new Setting(key.getKey(), value));
        setting.setValue(value);
        setting.setUpdatedAt(LocalDateTime.now());
        settingRepository.save(setting);
    }

    @Transactional
    public void batchUpdate(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SettingKey sk = SettingKey.fromKey(entry.getKey());
            if (sk != null) {
                set(sk, entry.getValue());
            }
        }
    }

    public Map<String, Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Setting> all = settingRepository.findAll();
        Map<String, String> dbMap = new HashMap<>();
        for (Setting s : all) {
            dbMap.put(s.getKey(), s.getValue());
        }
        for (SettingKey sk : SettingKey.values()) {
            String val = dbMap.getOrDefault(sk.getKey(), sk.getDefaultValue());
            result.put(sk.getKey(), deserializeValue(sk, val));
        }
        return result;
    }

    private Object deserializeValue(SettingKey key, String val) {
        try {
            switch (key.getType()) {
                case INTEGER: return Integer.parseInt(val);
                case BOOLEAN: return "true".equalsIgnoreCase(val) || "1".equals(val);
                case JSON: return objectMapper.readTree(val);
                default: return val;
            }
        } catch (Exception e) {
            log.warn("Failed to deserialize value for key {}: {}", key.getKey(), val);
            return val;
        }
    }

    private String getOrDefault(SettingKey key, String defaultValue) {
        return settingRepository.findById(key.getKey())
            .map(Setting::getValue)
            .orElse(defaultValue);
    }

    private void validate(SettingKey key, String value) {
        if (key.getType() == SettingKey.ValueType.JSON) {
            try {
                objectMapper.readTree(value);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON for key: " + key.getKey());
            }
        }
    }
}
