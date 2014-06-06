package hu.juranyi.zsolt.objectmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helps you simplify the handling of nested beans, by providing getter and
 * setter methods which work with property names.
 * 
 * @version 1.0.0
 * @author Zsolt Jurányi
 * 
 */
public class ObjectMapper {

	/**
	 * Gets all the properties with its values from the given object.
	 * Technically it's the same as calling get(o, list(o)).
	 * 
	 * @param rootObject
	 *            Object to map.
	 * @return All properties with values from the object.
	 */
	public static Map<String, Object> get(Object rootObject) {
		return get(rootObject, list(rootObject));
	}

	/**
	 * Gets the values of the desired properties from the root object.
	 * 
	 * @param rootObject
	 *            Object to map.
	 * @param properties
	 *            List of properties you want to query.
	 * @return Properties with values from the object.
	 */
	public static Map<String, Object> get(Object rootObject,
			List<String> properties) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		for (String p : properties) {
			m.put(p, get(rootObject, p));
		}
		return m;
	}

	/**
	 * Gets the property value of the given root object. If the property does
	 * not exist, will return null. <br />
	 * <br />
	 * Example:
	 * 
	 * <pre style="margin-bottom:0px">
	 * Object o = getProperty(root, &quot;a.b.c&quot;);
	 * </pre>
	 * 
	 * Does the same as:
	 * 
	 * <pre style="margin-bottom:0px">
	 * Object o = root.getA();
	 * if (null != o)
	 * 	o = ((TypeOfA) o).getB();
	 * if (null != o)
	 * 	o = ((TypeOfB) o).getC();
	 * </pre>
	 * 
	 * @param rootObject
	 *            The object to read from.
	 * @param property
	 *            The name of the property you need, e.g. "a.b.c".
	 * @return The value of the property in the root object or null if the
	 *         property does not exist.
	 */
	public static Object get(Object rootObject, String property) {
		try {
			// start in the root object
			Object o = rootObject;

			// go step by step on the property path
			for (String p : property.split("\\.")) {

				// get the field under current property
				Field f = o.getClass().getDeclaredField(p);

				// read its value and move on
				f.setAccessible(true);
				o = f.get(o);
			}
			return o;
		} catch (Exception ex) {
			System.out.println("Could not get property: " + property);
			return null;
		}
	}

	/**
	 * Lists the properties of a class recursively. "java.*" classes will not be
	 * mapped.<br />
	 * <br />
	 * Example:
	 * 
	 * <pre style="margin-bottom:0px">
	 * class A {
	 * 	B b;
	 * 	int i;
	 * }
	 * 
	 * class B {
	 * 	int j;
	 * }
	 * </pre>
	 * 
	 * <code>listProperties(A.class)</code> will return:
	 * 
	 * <pre style="margin-bottom:0px">
	 * b
	 * b.j
	 * i
	 * </pre>
	 * 
	 * @param c
	 *            Source class to map.
	 * @return List of all properties in the class.
	 */
	public static List<String> list(Class<?> c) {
		return list(c, null);
	}

	/**
	 * Same as list(Class), except you can add a prefix for all the propery
	 * names.
	 * 
	 * @param c
	 *            Source class to map.
	 * @param rootName
	 *            Prefix of property names, without the dot!
	 * @return List of all properties in the class.
	 */
	public static List<String> list(Class<?> c, String rootName) {
		List<String> p = new ArrayList<String>();

		// go thru fields
		for (Field f : c.getDeclaredFields()) {

			// get name and type
			String n = f.getName();
			Class<?> t = f.getType();

			// add prefix
			if (null != rootName && !rootName.isEmpty()) {
				n = String.format("%s.%s", rootName, n);
			}

			// add to list
			p.add(n);

			// map recursively
			if (!t.getName().startsWith("java.")) {
				p.addAll(list(t, n));
			}
		}
		return p;
	}

	/**
	 * Lists the properties of an object recursively. Technically it calls
	 * listProperties(o.getClass()). More info at listProperties(Class).
	 * 
	 * @param o
	 *            Source object to map.
	 * @return List of all properties in the object.
	 */
	public static List<String> list(Object o) {
		return list(o.getClass());
	}

	/**
	 * Sets the given properties of the root object and returns true if all of
	 * them was successful.
	 * 
	 * @param rootObject
	 *            The object to modify.
	 * @param properties
	 *            The property name-value pairs to be set.
	 * @return True if all set operations was successful, false otherwise.
	 */
	public static boolean set(Object rootObject, Map<String, Object> properties) {
		boolean r = true;
		for (String property : properties.keySet()) {
			Object value = properties.get(property);
			r = r & set(rootObject, property, value);
		}
		return r;
	}

	/**
	 * Sets the property of the given root object and returns true if the
	 * operation was successful. If a property inside the property path does not
	 * exist, setProperty will call its no-parameter constructor to build it.
	 * This constructor must be explicitly defined.<br />
	 * <br />
	 * Example:
	 * 
	 * <pre style="margin-bottom:0px">
	 * setProperty(root, &quot;a.b.c&quot;, value);
	 * </pre>
	 * 
	 * Doest the same as:
	 * 
	 * <pre style="margin-bottom:0px">
	 * Object o = root.getA();
	 * if (null == o)
	 * 	o = new TypeOfA();
	 * o = ((TypeOfA) o).getB();
	 * if (null == o)
	 * 	o = new TypeOfB();
	 * o = ((TypeOfB) o).getC();
	 * </pre>
	 * 
	 * 
	 * @param rootObject
	 *            The object to modify.
	 * @param property
	 *            The name of the property you need to modify, e.g. "a.b.c.".
	 * @param value
	 *            The new value of the property.
	 * @return True if the operations was successful, false otherwise.
	 */
	public static boolean set(Object rootObject, String property, Object value) {
		try {
			// start in the root object
			Object o = rootObject;

			// go step by step on the property path
			String[] pp = property.split("\\.");
			for (int i = 0; i < pp.length - 1; i++) {

				// get field under current property
				Field f = o.getClass().getDeclaredField(pp[i]);

				// read its value
				f.setAccessible(true);
				Object v = f.get(o);

				// if null
				if (null == v) {

					// get its no-parameter constructor
					Constructor<?> c = f.getType().getConstructor();
					c.setAccessible(true);

					// create it
					v = c.newInstance();

					// set it
					f.set(o, v);
				}

				// move on
				o = v;
			}

			// get the destination field
			Field f = o.getClass().getDeclaredField(pp[pp.length - 1]);

			// write the value
			f.setAccessible(true);
			f.set(o, value);

			return true;
		} catch (Exception ex) {
			System.out.println("Could not set property: " + property);
			return false;
		}
	}
}
