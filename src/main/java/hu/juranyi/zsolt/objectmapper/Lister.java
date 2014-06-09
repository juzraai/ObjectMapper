package hu.juranyi.zsolt.objectmapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @since 1.1.0
 * @author Zsolt Jurányi
 * 
 */
class Lister {

	private static final List<String> DEFAULT_IGNORED_CLASSES = new ArrayList<String>();

	static {
		DEFAULT_IGNORED_CLASSES.add("java\\..*");
	}

	static List<String> defaultIgnoreList() {
		return new ArrayList<String>(DEFAULT_IGNORED_CLASSES);
	}

	static boolean ignoredClass(Class<?> c, List<String> i) {
		boolean r = false;
		for (int k = 0; k < i.size() && !r; k++) {
			r = c.getName().matches(i.get(k));
		}
		return r;
	}

	static List<String> list(Object object, String rootName,
			List<String> ignoredClasses) {
		List<String> props = new ArrayList<String>();

		if (null == object) {
			return props;
		}
		boolean objectIsAClass = object instanceof Class<?>;

		// TODO if (objectIsAClass) -> use cache ;-) but be aware of prefix!

		if (null == ignoredClasses) {
			ignoredClasses = DEFAULT_IGNORED_CLASSES;
		}

		// go thru fields

		Class<?> clazz = (Class<?>) ((objectIsAClass) ? object : object
				.getClass());
		for (Field field : clazz.getDeclaredFields()) {

			// get, build and store property name
			String name = field.getName();
			if (null != rootName && !rootName.isEmpty()) {
				name = String.format("%s.%s", rootName, name);
			}
			props.add(name);

			// get STATIC type
			Class<?> type = field.getType();

			// get ACTUAL type if possible
			Object value = null;
			if (!objectIsAClass) {
				field.setAccessible(true);
				try {
					value = field.get(object);
					type = value.getClass();
				} catch (Exception e) {
				}
			}

			// map recursively
			if (!ignoredClass(type, ignoredClasses)) {
				if (null == value || objectIsAClass) {

					// by Class
					props.addAll(list(type, name, ignoredClasses));
				} else {

					// by Object
					props.addAll(list(value, name, ignoredClasses));
				}

				// map superclass
				props.addAll(list(type.getSuperclass(), name, ignoredClasses));
			}
		}
		return props;
	}
}
