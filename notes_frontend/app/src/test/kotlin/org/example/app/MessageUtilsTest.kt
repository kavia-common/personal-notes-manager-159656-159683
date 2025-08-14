package org.example.app

import org.junit.Test
import org.junit.Assert.assertEquals

class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        assertEquals("Hello      World!", MessageUtils.message())
    }
}
