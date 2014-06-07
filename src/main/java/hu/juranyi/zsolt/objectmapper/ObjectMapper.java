package hu.juranyi.zsolt.objectmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <i>ObjectMapper</i> <b>helps you</b> simplify the handling of nested beans,
 * by providing getter and setter methods which <b>work with property names</b>.
 * So it is basically a lightweight alternative to Spring's BeanWrapper.
 * <i>ObjectMapper</i> has much less functions, but it's <b>small</b> and
 * <b>fast</b>. :-)
 * 
 * @version 1.0.0
 * @author Zsolt Jurányi
 * 
 */
public class ObjectMapper {

	// TODO use cache in list(Class) method, be careful with prefix param!

	/**
	 * 
	 * @return A copy of default ignore list used by lister methods.
	 */
	public static List<String> defaultIgnoreList() {
		return Lister.defaultIgnoreList();
	}

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
			// System.out.println("Could not get property: " + property);
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
	 * @param object
	 *            Source class to map.
	 * @return List of all properties in the class.
	 */
	/**
	 * Lists all properties from the given Object. If the Object is a Class,
	 * list() will map the static types the Class object holds, otherwise it
	 * queries the actual types from the Object. list() will recursively gather
	 * all properties from fields, as well as from their supertypes. It will use
	 * the default class name ignore list which contains only the "java\..*"
	 * pattern.<br />
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
	 * @param object
	 *            Source Object or Class to map.
	 * @return List of all (not ignored) properties in the given Object or
	 *         Class.
	 */
	public static List<String> list(Object object) {
		return list(object, null, null);
	}

	/**
	 * Lists all properties from the given Object. If the Object is a Class,
	 * list() will map the static types the Class object holds, otherwise it
	 * queries the actual types from the Object. list() will recursively gather
	 * all properties from fields, as well as from their supertypes.You can
	 * specify a list of class name patterns to tell list() not to map matching
	 * classes recursively.
	 * 
	 * @param object
	 *            Source Object or Class to map.
	 * 
	 * @param ignoreClasses
	 *            List of class name patterns you don't want to map recursively.
	 * @return List of all (not ignored) properties in the given Object or
	 *         Class, with the given prefix.
	 */
	public static List<String> list(Object object, List<String> ignoreClasses) {
		return Lister.list(object, null, ignoreClasses);
	}

	/**
	 * Lists all properties from the given Object. If the Object is a Class,
	 * list() will map the static types the Class object holds, otherwise it
	 * queries the actual types from the Object. list() will recursively gather
	 * all properties from fields, as well as from their supertypes. You can
	 * specify a prefix for all property names. It will use the default class
	 * name ignore list which contains only the "java\..*" pattern.
	 * 
	 * @param object
	 *            Source Object or Class to map.
	 * @param rootName
	 *            Prefix for all property names (without dot at the end).
	 * @return List of all (not ignored) properties in the given Object or
	 *         Class, with the given prefix.
	 */
	public static List<String> list(Object object, String rootName) {
		return list(object, rootName, null);
	}

	/**
	 * Lists all properties from the given Object. If the Object is a Class,
	 * list() will map the static types the Class object holds, otherwise it
	 * queries the actual types from the Object. list() will recursively gather
	 * all properties from fields, as well as from their supertypes. You can
	 * specify a prefix for all property names. Also you can specify a list of
	 * class name patterns to tell list() not to map matching classes
	 * recursively.
	 * 
	 * @param object
	 *            Source Object or Class to map.
	 * @param rootName
	 *            Prefix for all property names (without dot at the end).
	 * @param ignoreClasses
	 *            List of class name patterns you don't want to map recursively.
	 * @return List of all (not ignored) properties in the given Object or
	 *         Class, with the given prefix.
	 */
	public static List<String> list(Object object, String rootName,
			List<String> ignoreClasses) {
		return Lister.list(object, rootName, ignoreClasses);
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
			// System.out.println("Could not set property: " + property);
			return false;
		}
	}
}
