package hu.juranyi.zsolt.objectmapper;

import java.lang.reflect.Field;

/**
 * 
 * @since 1.1.0
 * @author Zsolt
 * 
 */
public class Property {

	public final Object owner;
	public final Field field;

	public Property(Object owner, Field field) {
		this.owner = owner;
		this.field = field;
	}

	public Object value() {
		try {
			field.setAccessible(true);
			return field.get(owner);
		} catch (Exception ex) {
			return null;
		}
	}
}
