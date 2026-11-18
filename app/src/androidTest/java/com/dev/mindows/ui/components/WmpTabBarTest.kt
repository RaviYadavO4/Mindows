// app/src/androidTest/java/com/dev/mindows/ui/components/WmpTabBarTest.kt
package com.dev.mindows.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class WmpTabBarTest {

    @get:Rule
    val composeTestRule = createComposeRule() // यह बिना पूरी एक्टिविटी लोड किए केवल विजेट टेस्ट करता है

    @Test
    fun wmpTabBar_displaysAllTabsAndTriggersClickCorrectly() {
        var selectedTabState = "My Music"

        composeTestRule.setContent {
            WmpTabBar(
                selectedTab = selectedTabState,
                onTabSelected = { selectedTabState = it }
            )
        }

        // कंपोनेंट्स की मौजूदगी चेक करना
        composeTestRule.onNodeWithText("My Music").assertExists()
        composeTestRule.onNodeWithText("Playlists").assertExists()

        // वर्चुअल क्लिक सिमुलेशन
        composeTestRule.onNodeWithText("Playlists").performClick()

        // स्टेट वेरिफिकेशन
        assert(selectedTabState == "Playlists")
    }
}
