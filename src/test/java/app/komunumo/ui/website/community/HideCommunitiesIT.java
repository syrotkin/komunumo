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
package app.komunumo.ui.website.community;

import app.komunumo.ui.IntegrationTest;
import app.komunumo.ui.component.NavigationBar;
import app.komunumo.ui.website.WebsiteLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouterLink;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static app.komunumo.util.TestUtil.assertContainsExactlyOneRouterLinkOf;
import static app.komunumo.util.TestUtil.findComponent;
import static app.komunumo.util.TestUtil.findComponents;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "komunumo.instance.hide-communities=true")
class HideCommunitiesIT extends IntegrationTest {

    @Test
    void checkCommunityLinkNotVisible() {
        final var uiParent = UI.getCurrent()
                .getCurrentView()
                .getParent().orElseThrow()
                .getParent().orElseThrow();
        final var websiteLayout = (WebsiteLayout) uiParent;

        final var navigationBar = findComponent(websiteLayout, NavigationBar.class);
        assertThat(navigationBar).isNotNull();
        final var routerLinks = findComponents(navigationBar, RouterLink.class);
        assertContainsExactlyOneRouterLinkOf(routerLinks, new Anchor("events", "Events"));
    }

}
