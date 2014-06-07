package hu.juranyi.zsolt.objectmapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObjectMapperTest {

	private static class L0 {
		private int i0 = 23;
		private L1 l1 = new L1();
		private L1 l1b;
	}

	private static class L1 {
		private int i1 = 42;

		public L1() {
		}
	}

	private static class L1Ex extends L1 {
		private int e = 39;
	}

	@Test
	public void getSimpleProperty() {
		assertEquals(23, ObjectMapper.get(new L0(), "i0"));
	}

	@Test
	public void getExistingDeepProperty() {
		assertEquals(42, ObjectMapper.get(new L0(), "l1.i1"));
	}

	@Test
	public void getNonExistingDeepProperty() {
		assertNull(ObjectMapper.get(new L0(), "l1b.i1"));
	}

	@Test
	public void setSimpleProperty() {
		L0 l0 = new L0();
		assertTrue(ObjectMapper.set(l0, "i0", 13));
		assertEquals(13, ObjectMapper.get(l0, "i0"));
	}

	@Test
	public void setExistingDeepProperty() {
		L0 l0 = new L0();
		assertTrue(ObjectMapper.set(l0, "l1.i1", 24));
		assertEquals(24, ObjectMapper.get(l0, "l1.i1"));
	}

	@Test
	public void setNonExistingDeepProperty() {
		L0 l0 = new L0();
		assertTrue(ObjectMapper.set(l0, "l1b.i1", 59));
		assertEquals(59, ObjectMapper.get(l0, "l1b.i1"));
	}

	@Test
	public void setInvalidProperty() {
		assertFalse(ObjectMapper.set(new Object(), "invalid", null));
	}

	@Test
	public void handleDynamicType() {
		L0 l0 = new L0();
		ObjectMapper.set(l0, "l1", new L1Ex());
		assertEquals(39, ObjectMapper.get(l0, "l1.e"));
		ObjectMapper.set(l0, "l1.e", 73);
		assertEquals(73, ObjectMapper.get(l0, "l1.e"));
	}
}
