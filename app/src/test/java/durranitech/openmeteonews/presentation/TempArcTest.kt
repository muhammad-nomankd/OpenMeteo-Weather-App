package durranitech.openmeteonews.presentation
import org.junit.Assert.assertEquals
import org.junit.Test

class TempArcTest {

	@Test
	fun `current at minimum gives 0 degree sweep`() {
		val angle = tempArcSweepAngle(tempMin = 10.0, tempMax = 40.0, tempCurrent = 10.0)
		assertEquals(0f, angle, 0.001f)
	}

	@Test
	fun `current at maximum gives 180 degree sweep`() {
		val angle = tempArcSweepAngle(tempMin = 10.0, tempMax = 40.0, tempCurrent = 40.0)
		assertEquals(180f, angle, 0.001f)
	}

	@Test
	fun `current at exact midpoint gives 90 degree sweep`() {
		val angle = tempArcSweepAngle(tempMin = 0.0, tempMax = 40.0, tempCurrent = 20.0)
		assertEquals(90f, angle, 0.001f)
	}

	@Test
	fun `current at quarter range gives 45 degree sweep`() {
		val angle = tempArcSweepAngle(tempMin = 0.0, tempMax = 40.0, tempCurrent = 10.0)
		assertEquals(45f, angle, 0.001f)
	}

	@Test
	fun `current at three quarters range gives 135 degree sweep`() {
		val angle = tempArcSweepAngle(tempMin = 0.0, tempMax = 40.0, tempCurrent = 30.0)
		assertEquals(135f, angle, 0.001f)
	}

	// ── Real-world temperatures (Nowshera summer) ──────────────────────────

	@Test
	fun `typical summer day in Nowshera calculates correctly`() {
		val angle = tempArcSweepAngle(tempMin = 28.0, tempMax = 42.0, tempCurrent = 35.0)
		assertEquals(90f, angle, 0.001f)
	}

	@Test
	fun `early morning temperature gives low sweep angle`() {
		val angle = tempArcSweepAngle(tempMin = 22.0, tempMax = 38.0, tempCurrent = 23.0)
		assertEquals(11.25f, angle, 0.001f)
	}

	@Test
	fun `negative temperatures calculate correctly`() {
		val angle = tempArcSweepAngle(tempMin = -10.0, tempMax = 10.0, tempCurrent = 0.0)
		assertEquals(90f, angle, 0.001f)
	}

	@Test
	fun `all negative temperatures calculate correctly`() {
		val angle = tempArcSweepAngle(tempMin = -20.0, tempMax = -10.0, tempCurrent = -15.0)
		assertEquals(90f, angle, 0.001f)
	}


	@Test
	fun `current above maximum is clamped to 180 degrees`() {
		val angle = tempArcSweepAngle(tempMin = 10.0, tempMax = 30.0, tempCurrent = 99.0)
		assertEquals(180f, angle, 0.001f)
	}

	@Test
	fun `current below minimum is clamped to 0 degrees`() {
		val angle = tempArcSweepAngle(tempMin = 20.0, tempMax = 40.0, tempCurrent = 5.0)
		assertEquals(0f, angle, 0.001f)
	}

	@Test
	fun `equal min and max does not divide by zero`() {
		val angle = tempArcSweepAngle(tempMin = 25.0, tempMax = 25.0, tempCurrent = 25.0)
		assertEquals(0f, angle, 0.001f)
	}

	@Test
	fun `equal min and max with current above does not crash`() {
		val angle = tempArcSweepAngle(tempMin = 25.0, tempMax = 25.0, tempCurrent = 30.0)
		assertEquals(180f, angle, 0.001f)
	}
}