# ObjectMapper
**Properties in Java! ;-)**

---



## 1. What's this?

*ObjectMapper* helps you simplify the handling of nested beans, by providing getter and setter methods which work with property names.



## 2. How to use it?

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

System.out.println(get(org, "address.city")); // Los Santos
```

You don't have to:

* define getter/setter methods
* get the address object
* create the address object

You just need to:

* define a no-parameter constructor in Address
* import ObjectMapper.*
* call get/set methods

`get()` and `set()` methods have various parameter list, just check the [code](https://github.com/juzraai/ObjectMapper/blob/master/src/main/java/hu/juranyi/zsolt/objectmapper/ObjectMapper.java)! :-) There are also `list()` methods which list all properties from an *Object* or *Class*.



## 3. Limitations

*ObjectMapper* queries the static type of fields, so if you have a *MyAddress* class which extends *Address* and has an additional field, e.g. "phone", this will not work:
```java
set(org, "address", new MyAddress()); // it's okay
set(org, "address.phone", "12345678"); // invalid property, because type of "address" is Address
```

And also you cannot handle elements of collections this way. (But maybe in the future :-))

The other limitation is that you need to **define explicitly a no-parameter constructor** in every class that appear as type of properties.



## 4. Usage examples

With this tool you can easily turn your *Object* into a *Map* and back, this can be useful for exporting an *Object* or many of them into a readable format, e.g. CSV.

I use this tool also for building up an *Object* from a text matching a regexp, which contains named groups which are actually property names. :-)