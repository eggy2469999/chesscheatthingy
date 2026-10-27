package com.example.chesoverlay

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class ChessAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Empty for now, this just makes the build pass
    }

    override fun onInterrupt() {
        // Empty for now
    }
}
