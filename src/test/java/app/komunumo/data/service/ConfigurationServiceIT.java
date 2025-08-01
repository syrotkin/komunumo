/*
 * Komunumo - Open Source Community Manager
 * Copyright (C) Marcus Fihlon and the individual contributors to Komunumo.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.komunumo.data.service;

import app.komunumo.data.dto.ConfigurationSetting;
import app.komunumo.ui.IntegrationTest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static app.komunumo.data.db.tables.Config.CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationServiceIT extends IntegrationTest {

    @Autowired
    private DSLContext dsl;

    private ConfigurationService configurationService;

    @BeforeEach
    void cleanSetUp() {
        dsl.deleteFrom(CONFIG).execute();
        configurationService = new ConfigurationService(dsl);
    }

    @Test
    void shouldReturnDefaultValueIfNoValueExists() {
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_NAME);
        assertThat(value).isEqualTo("Your Instance Name");
    }

    @Test
    void shouldReturnValueFromDatabaseForLanguageSpecificSetting() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.GERMAN, "Offener Community-Manager");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.GERMAN);
        assertThat(value).isEqualTo("Offener Community-Manager");
    }

    @Test
    void shouldFallbackToEnglishIfLanguageSpecificSettingIsMissing() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH, "English Slogan");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.FRENCH);
        assertThat(value).isEqualTo("English Slogan");
    }

    @Test
    void shouldResolveLanguageFromRegionalLocale() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.GERMAN, "Deutscher Slogan");

        final var swissGerman = Locale.of("de", "CH");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, swissGerman);

        assertThat(value).isEqualTo("Deutscher Slogan");
    }

    @Test
    void shouldReturnDefaultIfNothingIsSet() {
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ITALIAN);
        assertThat(value).isEqualTo("Your Instance Slogan");
    }

    @Test
    void shouldOverrideExistingValue() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH, "Old");
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH, "New");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH);
        assertThat(value).isEqualTo("New");
    }

    @Test
    void shouldInvalidateCacheWhenSettingIsChanged() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH, "Initial");
        final var before = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH);
        assertThat(before).isEqualTo("Initial");

        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH, "Updated");
        final var after = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_SLOGAN, Locale.ENGLISH);
        assertThat(after).isEqualTo("Updated");
    }

    @Test
    void shouldStoreAndRetrieveValueWithoutLocale() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_NAME, "Custom Komunumo");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_NAME);
        assertThat(value).isEqualTo("Custom Komunumo");
    }

    @Test
    void shouldReturnValueWithoutLocaleWhenLocaleIsNull() {
        configurationService.setConfiguration(ConfigurationSetting.INSTANCE_NAME, "Neutral Name");
        final var value = configurationService.getConfiguration(ConfigurationSetting.INSTANCE_NAME, null);
        assertThat(value).isEqualTo("Neutral Name");
    }

}
