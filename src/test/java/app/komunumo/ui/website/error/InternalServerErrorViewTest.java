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
package app.komunumo.ui.website.error;

import app.komunumo.data.service.ConfigurationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalServerErrorViewTest {

    private static ConfigurationService configurationService;

    @BeforeAll
    static void setUp() {
        configurationService = mock(ConfigurationService.class);
        when(configurationService.getConfiguration(any(), any())).thenReturn("Komunumo Test");
    }

    @Test
    void showsDefaultMessageWhenNoCustomMessageGiven() {
        // Arrange
        var view = new InternalServerErrorView(configurationService);

        var beforeEnterEvent = mock(BeforeEnterEvent.class);
        var ui = InternalServerErrorViewTest.TestUIProvider.mockUiWithTranslation(
                "error.page.internal-server-error", "Default Error Message");
        when(beforeEnterEvent.getUI()).thenReturn(ui);

        var errorParameter = new ErrorParameter<>(Exception.class,
                new Exception());

        // Act
        var status = view.setErrorParameter(beforeEnterEvent, errorParameter);

        // Assert
        var h2 = (H2) view.getChildren().findFirst().orElseThrow();
        assertThat(h2.getText()).isEqualTo("Default Error Message");
        assertThat(status).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void showsDefaultMessageWhenEmptyCustomMessageProvided() {
        // Arrange
        var view = new InternalServerErrorView(configurationService);

        var beforeEnterEvent = mock(BeforeEnterEvent.class);
        var ui = InternalServerErrorViewTest.TestUIProvider.mockUiWithTranslation(
                "error.page.internal-server-error", "Default Error Message");
        when(beforeEnterEvent.getUI()).thenReturn(ui);

        var errorParameter = new ErrorParameter<>(Exception.class,
                new Exception(""));

        // Act
        var status = view.setErrorParameter(beforeEnterEvent, errorParameter);

        // Assert
        var h2 = (H2) view.getChildren().findFirst().orElseThrow();
        assertThat(h2.getText()).isEqualTo("Default Error Message");
        assertThat(status).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void showsCustomMessageWhenProvided() {
        // Arrange
        var view = new InternalServerErrorView(configurationService);

        var beforeEnterEvent = mock(BeforeEnterEvent.class);
        var ui = InternalServerErrorViewTest.TestUIProvider.mockUiWithTranslation(
                "error.page.internal-server-error", "Default Error Message");
        when(beforeEnterEvent.getUI()).thenReturn(ui);

        var errorParameter = new ErrorParameter<>(Exception.class,
                new Exception("Custom Error Message"));

        // Act
        var status = view.setErrorParameter(beforeEnterEvent, errorParameter);

        // Assert
        var h2 = (H2) view.getChildren().findFirst().orElseThrow();
        assertThat(h2.getText()).isEqualTo("Custom Error Message");
        assertThat(status).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private static class TestUIProvider {
        public static UI mockUiWithTranslation(final @NotNull String key, final @NotNull String value) {
            var ui = mock(UI.class);
            when(ui.getTranslation(key)).thenReturn(value);
            return ui;
        }
    }
}
