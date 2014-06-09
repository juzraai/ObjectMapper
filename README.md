# ObjectMapper
**Properties in Java! ;-)**

---

* [Whats' this?](#whats-this)
* [How to use it?](#how-to-use-it)
* [How does it work?](#how-does-it-work)
* [Features](#features)
* [Limitations](#limitations)
* [Usage examples](#usage-examples)

---

## What's this?

*ObjectMapper* **helps you** simplify the handling of nested beans, by providing
getter and setter methods which **work with property names**. So it is basically
a lightweight alternative to [Spring's BeanWrapper](http://docs.spring.io/spring/docs/4.0.5.RELEASE/spring-framework-reference/htmlsingle/#beans-beans).
*ObjectMapper* has much less functions, but it's **small** and **fast**. :-)



## How to use it?

Assume you have these bean classes:

```java
public class Organization {
	private String name;
    private Address address;
}

public class Address {
	private String city;
	private String street;
    private String zip;
    public Address() { }
}
```

You can easily fill up an organization like this:

```java
import static hu.juranyi.zsolt.objectmapper.ObjectMapper.*;

// ...

Organization org = new Organization();
set(org, "name", "CJ Holding");
set(org, "address.city", "Los Santos");
set(org, "address.street", "Grove Street 2.");
set(org, "address.zip", "WTF-42");

assertEquals("Los Santos", get(org, "address.city"));

// if you have proper getters and setters:
assertNotNull(org.getAddress());
assertEquals("Los Santos", org.getAddress().getCity());
```

You don't have to:

* define getter/setter methods
* set fields public
* get the address object
* create the address object

You just need to:

* define a no-parameter constructor in *Address*
* import `ObjectMapper.*`
* call `get()`/`set()` methods

Let's see a more interesting example! Assume you have an extended address class 
too:

```java
public class AddressEx extends Address {
	private String phone;
}

```

Even when the static type of `address` is *Address*, you can do this:

```java
set(org, "address", new AddressEx());
set(org, "address.phone", "123456789");

assertEquals("123456789", get(org, "address.phone"));
```



## How does it work?

*ObjectMapper* does not store anything, it simply uses [Java Reflection API](http://docs.oracle.com/javase/tutorial/reflect/)
to access the declared fields, retrieve their type and value or set them.

This call:

```java
set(org, "address.city", "Los Santos");
```

has the same effect as:

```java
Address a = org.getAddress();
if (null == a) {
	a = new Address();
	org.setAddress(a);
}
a.setCity("Los Santos");
```

And this one:

```java
Object o = get(org, "address.city");
```

has the same effect as:

```
Object o = org.getAddress();
if (null != o) {
	o = ((Address) o).getCity();
}
```

So when a property is not readable (e.g. `address` is `null` so `address.city` 
is not accessible) it drops back `null`.

If any error occurs when setting a property (e.g. there's no proper constructor
or field), `set()` methods will return `false`.



## Features

`get()` and `set()` methods have various parameter lists, and there are also
`list()` methods which list all properties from an *Object* or *Class*:

* getters
	* `Object get(Object, String)` - retrieve a single property
	* `Map<String, Object> get(Object)` - retrieve all property = translates 
	your *Object* into a *Map*
	* `Map<String, Object> get(Object, List<String>)` - retrueve a bunch of 
	properties
* setters
	* `boolean set(Object, String, Object)` - sets a single property
	* `boolean set(Object, Map<String, Object>)` - sets a bunch of properties
* listers
	* `List<String> list(Object)` - lists all properties based on actual or 
	static types (static when *Object* is a *Class*)
	* `List<String> list(Object, String)` - you can add prefix
	* `List<String> list(Object, List<String>)` - you can define your own ignore
	list for class names
	* `List<String> list(Object, String, List<String>)` - or both



## Limitations

You need to **define explicitly a no-parameter constructor** in every class that
appear as type of properties OR build up your property paths manually.



## Usage examples

With this tool you can easily turn your *Object* into a *Map* and back, this can
be useful for exporting an *Object* or many of them into a readable format, e.g.
CSV.

I use this tool also for building up an *Object* from a text matching a regexp,
which contains named groups which are actually property names. :-)



## Future plans

**Handling collections** would be awesome. Property names would contain brackets and
can specify an index or key in a collection. Also it would be great if there
were a solution to add an element to a list, then reference it in further
property setters without knowing its index.

And maybe a **caching** feature for Class based listing would be useful: it would
increase the speed of further queries.