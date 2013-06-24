//
// Sample code to show how to use a simple database connection factory with the 
// GStorm ORM
// see: https://github.com/kdabir/gstorm
//
// ----------------------------------------------------------------------------
//
// NOTE: The below doesn't work, as the original repo is built with a different 
// version of Java.
//
// Because of this, I build the jar locally and add it to the classpath as 
// follows:
//
// groovy -cp gstorm-master-0.4-dev.jar GStormFactory.groovy
//
//
// @GrabResolver(name='gstorm', root='http://kdabir.github.io/mavenrepo/') 
// @Grab('gstorm:gstorm:0.3')

import groovy.sql.*
import gstorm.*

@GrabConfig(systemClassLoader = true) 
@Grab('org.hsqldb:hsqldb:2.2.9')

// A Simple class to hold the information for an item to be tracked.
class Item {
  // GStorm will handle creating and managing the ID
  // long id
  String name
  String itemType
  String itemLocation_1
  String itemLocation_2
  String description
  Date createdAt
}

// Singleton class
// @Singleton
@Singleton(lazy = true)
class GStormFactory {

	private Sql sqlInstance = null
	private Gstorm g = null

    private GStormFactory() {
	  if (sqlInstance == null) {
		sqlInstance = Sql.newInstance("jdbc:hsqldb:mem:database", "sa", "", 
		  "org.hsqldb.jdbcDriver")
		this.g = new Gstorm(sqlInstance)		
	  }
    }
	
    public Gstorm getGstorm() { 
        return this.g;
    }	
}

//
// Now some code to test the above
// 

// Get a singleton instance and return the GStorm handle
def gstormInst = GStormFactory.instance.getGstorm()
gstormInst.stormify(Item) // table automatically gets created for this class

def item = new Item(name: "Groovy in Action", itemType: "Book", 
  createdAt: new Date())
item.save() // model automatically gets this method

println "Object for item ${item.name} is ${item.id}"

def otherItem = new Item(name: "Animal House", itemType: "VHS", 
  createdAt: new Date()).save() // save one more
println "Created  and saved ${otherItem.name} of type ${otherItem.itemType}"

otherItem.itemType = "DVD"
otherItem.save() // update it

def yetAnotherItem = new Item(name: "Ubuntu 12.04", itemType: "Software", 
  createdAt: new Date()).save() // save one more
println "Created and saved ${yetAnotherItem.name} of type " +
  "${yetAnotherItem.itemType}"

println "-".multiply(80)  // print 80 times
println "Display, all the current records"
println "-".multiply(80)
println "all records -> ${Item.all}"

println "Now find an item using id of 1"
//Integer id = new Integer(1)
// def foundItem = Item.get(id);
def foundItem = Item.get(1);
println "Found ${foundItem.name} of type ${foundItem.itemType}"

println "now deleting ${otherItem.name}"
otherItem.delete()

println "-".multiply(80)
println "Display, all the current records (note we deleted one)"
println "-".multiply(80)
println "all records -> ${Item.all}"

println "Now try to find the DELETED item using id of 1"
def foundItem_2 = Item.get(1);
if (foundItem_2 != null)
  println "Found ${foundItem_2.name} of type ${foundItem_2.itemType}"
else
  println "The item is indeed gone!!!"
