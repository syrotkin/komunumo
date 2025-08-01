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
package app.komunumo.ui.website;

import app.komunumo.ui.component.InfoBanner;
import app.komunumo.ui.component.PageFooter;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.RouterLink;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.komunumo.ui.IntegrationTest;
import app.komunumo.ui.component.NavigationBar;
import app.komunumo.ui.component.PageHeader;
import app.komunumo.ui.website.home.HomeView;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static app.komunumo.util.TestUtil.assertContainsExactlyOneInstanceOf;
import static app.komunumo.util.TestUtil.assertContainsExactlyOneRouterLinkOf;
import static app.komunumo.util.TestUtil.findComponent;
import static app.komunumo.util.TestUtil.findComponents;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebsiteLayoutIT extends IntegrationTest {

    private @NotNull WebsiteLayout websiteLayout;

    @BeforeEach
    void setUp() {
        UI.getCurrent().navigate(HomeView.class);
        final var uiParent = UI.getCurrent()
                .getCurrentView()
                .getParent().orElseThrow()
                .getParent().orElseThrow();
        websiteLayout = (WebsiteLayout) uiParent;
    }

    @Test
    void testLayoutContent() {
        final var components = websiteLayout.getChildren().toList();
        assertContainsExactlyOneInstanceOf(components,
                InfoBanner.class, PageHeader.class, NavigationBar.class, Main.class, PageFooter.class);
    }

    @Test
    void testBanner()  {
        final var infoBanner = findComponent(websiteLayout, InfoBanner.class);
        assertThat(infoBanner).isNotNull();

        final var markdown = findComponent(infoBanner, Markdown.class);
        assertThat(markdown).isNotNull();
        assertThat(markdown.getContent())
                .isEqualTo("**Demo Mode:** All data will be reset at the top of every hour!");
    }

    @Test
    void testPageHeader() {
        final var header = findComponent(websiteLayout, Header.class);
        assertThat(header).isNotNull();

        final var h1 = findComponent(header, H1.class);
        assertThat(h1).isNotNull();
        assertThat(h1.getText()).isEqualTo("Your Instance Name");

        final var h2 = findComponent(header, H2.class);
        assertThat(h2).isNotNull();
        assertThat(h2.getText()).isEqualTo("Your Instance Slogan");
    }

    @Test
    void testPageFooter() {
        final var footer = findComponent(websiteLayout, Footer.class);
        assertThat(footer).isNotNull();

        final var anchor = findComponent(footer, Anchor.class);
        assertThat(anchor).isNotNull();
        assertThat(anchor.getText()).contains("Komunumo · Version");
    }

    @Test
    void testNavigationBar() {
        final var navigationBar = findComponent(websiteLayout, NavigationBar.class);
        assertThat(navigationBar).isNotNull();

        final var routerLinks = findComponents(navigationBar, RouterLink.class);
        assertContainsExactlyOneRouterLinkOf(routerLinks,
                new Anchor("events", "Events"),
                new Anchor("communities", "Communities"));
    }

    @Test
    void testRouterLayoutContent() {
        final var main = findComponent(websiteLayout, Main.class);
        assertThat(main).isNotNull();
        assertThat(main.getElement().getChildCount()).isEqualTo(1);

        websiteLayout.showRouterLayoutContent(new Paragraph("foo"));
        assertThat(main.getElement().getChildCount()).isEqualTo(1);
        assertThat(main.getElement().getChild(0).getText()).isEqualTo("foo");

        websiteLayout.showRouterLayoutContent(new Paragraph("bar"));
        assertThat(main.getElement().getChildCount()).isEqualTo(1);
        assertThat(main.getElement().getChild(0).getText()).isEqualTo("bar");

        websiteLayout.removeRouterLayoutContent(null);
        assertThat(main.getElement().getChildCount()).isZero();
    }

    @Test
    void testRouterLayoutContentException() {
        final var content = mock(HasElement.class);
        final var element = mock(Element.class);
        when(content.getElement()).thenReturn(element);
        when(element.getComponent()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> websiteLayout.showRouterLayoutContent(content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WebsiteLayout content must be a Component");
    }

}
