package com.mctb.autoreply.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberValidatorTest {

    @Test
    fun `valid number passes validation`() {
        assertTrue(PhoneNumberValidator.isValid("+1 (305) 555-0101"))
    }

    @Test
    fun `unknown-like number is rejected`() {
        assertFalse(PhoneNumberValidator.isValid("UNKNOWN"))
        assertFalse(PhoneNumberValidator.isValid("Private Number"))
    }

    @Test
    fun `too few digits is rejected`() {
        assertFalse(PhoneNumberValidator.isValid("ab1"))
    }
}
