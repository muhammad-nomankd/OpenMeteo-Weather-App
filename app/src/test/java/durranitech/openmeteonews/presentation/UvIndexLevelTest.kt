package durranitech.openmeteonews.presentation

import junit.framework.TestCase.assertEquals
import org.junit.Test

class UvIndexLevelTest {

	@Test
	fun `uvIndex 0 returns Low`() {
		assertEquals("Low", uvIndexLevel(0.0))
	}

	@Test
	fun `uvIndex 1 returns Low`() {
		assertEquals("Low", uvIndexLevel(1.0))
	}

	@Test
	fun `uvIndex 2 point 9 returns Low`() {
		assertEquals("Low", uvIndexLevel(2.9))
	}

	@Test
	fun `uvIndex 3 returns Moderate`() {
		assertEquals("Moderate", uvIndexLevel(3.0))
	}

	@Test
	fun `uvIndex 5 point 9 returns Moderate`() {
		assertEquals("Moderate", uvIndexLevel(5.9))
	}

	@Test
	fun `uvIndex 6 returns High`() {
		assertEquals("High", uvIndexLevel(6.0))
	}

	@Test
	fun `uvIndex 7 point 9 returns High`() {
		assertEquals("High", uvIndexLevel(7.9))
	}

	@Test
	fun `uvIndex 8 returns Very High`() {
		assertEquals("Very High", uvIndexLevel(8.0))
	}

	@Test
	fun `uvIndex 10 point 9 returns Very High`() {
		assertEquals("Very High", uvIndexLevel(10.9))
	}

	@Test
	fun `uvIndex 11 returns Extreme`() {
		// Exact boundary — 11.0 hits the else branch
		assertEquals("Extreme", uvIndexLevel(11.0))
	}

	@Test
	fun `uvIndex 15 returns Extreme`() {
		// Unrealistically high but should never crash
		assertEquals("Extreme", uvIndexLevel(15.0))
	}

	// ── Edge cases ─────────────────────────────────────────────────────────

	@Test
	fun `uvIndex negative value returns Low`() {
		// Sensor error or bad data — should not crash, clamp to Low
		assertEquals("Low", uvIndexLevel(-1.0))
	}

	@Test
	fun `uvIndex exactly at every boundary returns correct level`() {
		// Batch-verify all 5 levels in one test — useful as a quick sanity check
		val expectations = mapOf(
			0.0 to "Low",
			3.0 to "Moderate",
			6.0 to "High",
			8.0 to "Very High",
			11.0 to "Extreme",
		)
		expectations.forEach { (index, expected) ->
			assertEquals(
				"uvIndex $index should be $expected",
				expected,
				uvIndexLevel(index),
			)
		}
	}
}