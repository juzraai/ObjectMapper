package hu.juranyi.zsolt.objectmapper;

import java.lang.reflect.Field;

/**
 * 
 * @since 1.1.0
 * @author Zsolt Jurányi
 * 
 */
class Getter {

	static Object get(Object object, String property) {
		Property p = getProperty(object, property);
		return (null == p) ? null : p.value();
	}

	static Field getField(Object object, String property) {
		Property p = getProperty(object, property);
		return (null == p) ? null : p.field;
	}

	static Property getProperty(Object object, String property) {
		try {
			// start in the root object
			Object owner = object;
			Field field = null;
			Object value = null;

			// go step by step on the property path
			for (String p : property.split("\\.")) {

				// previous value is the current owner
				if (null != value) {
					owner = value;
				}

				// get the field under current property
				field = owner.getClass().getDeclaredField(p);

				// read its value and move on
				field.setAccessible(true);
				value = field.get(owner);
			}

			// return the property
			return new Property(owner, field);

		} catch (Exception ex) {
			return null;
		}
	}
}
