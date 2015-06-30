package nl.xillio.xill.plugins.selenium;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import nl.xillio.xill.plugins.selenium.LoadPageConstruct.Options;

/**
 * @author Zbynek Hochmann
 * This class encapsulates the pooling mechanism for PhantomJS...exe processes (Selenium) used in loadpage() Xill function.
 */
public class PhantomJSPool {

	private Logger log = Logger.getLogger("XMT");
	
	Vector<Entity> poolEntities = new Vector<Entity>();
	int maxPoolSize = 10; //maximum of entities in pool (entity means running PhantomJS..exe process)
	
	/**
	 * It represents the options that have been used as CLI arguments of PhantomJS..exe process.
	 * It helps to identify the proper PhantomJS..exe process - i.e. existing process that has been started with the same CLI parameters as required.  
	 */
	public class Identifier {
		
		private Options options;
		
		public Identifier(Options options) {
			this.options = options;
		}
		
		public Options getOptions() {
			return this.options;
		}
	}
	
	/**
	 * It represents one item in the pool - i.e. existing PhantomJS..exe process with other related information 
	 */
	public class Entity implements AutoCloseable {
		
		private WebDriver driver;
		private Identifier id;
		private boolean used = false; //is this entity in use (true) or free to use (false)?
		
		public boolean isUsed() {
			return this.used;
		}
		
		/**
		 * Creates new entity with options given by Identifier
		 * @return Newly created WebDriver instance
		 */
		private WebDriver create(Identifier id) {
			this.id = id;
			this.driver = id.getOptions().createDriver();
			this.used = true;
			return this.driver;
		}
		
		/**
		 * Determines if the identifier 'id' matches this entity
		 */
		public boolean compare(Identifier id) {
			return this.id.getOptions().compareDCap(id.getOptions());
		}
		
		/**
		 * Reuse this entity
		 * @param id It is used for passing the options that are to be set after driver is created (not CLI options)
		 */
		public void reuse(Identifier id) {
			id.getOptions().setDriverOptions(this.driver); //re-set driver (non-CLI) options
			this.used = true; //set to be used
		}
		
		/**
		 * Terminates PhantomJS..exe process of this entity
		 */
		private void dispose() {
			this.driver.quit();
		}

		/**
		 * Stops using this entity (PhantomJS..exe process)
		 */
		public void release() {
			this.used = false;
		}
		
		/**
		 * @return WebDriver of this entity (PhantomJS..exe process)
		 */
		public WebDriver getDriver() {
			return this.driver;
		}

		@Override
		public void close() throws Exception {
			this.release();
		}
	}
	
	/**
	 * @param maxPoolSize Maximum amount of entities (PhantomJS..exe processes) that can be in the pool at one time
	 */
	public PhantomJSPool(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		Runtime.getRuntime().addShutdownHook(new Thread(this::close));
	}
	
	/**
	 * It tries to find any existing PhantomJS..exe process that has the same CLI options and it's currently not in use  
	 * @return Found entity or null if not found convenient process 
	 */
	private Entity findFirstFreeEntity(Identifier id) {
		for (Entity item : this.poolEntities) {
			if ( (item.compare(id)) && (!item.isUsed()) ) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Looks if the entity with provided identifier exists in pool and it's free to use - then it will return it, otherwise it creates new one
	 * @return WebDriver of PhantomJS..exe process with provided options that can be used
	 */
	public Entity get(Identifier id) {
		//System.out.println(String.format("PJS: PhantomJSPool get(), thread %1$d", Thread.currentThread().getId()));		
		log.debug("PJSPool: New request for WebDriver...");
		Entity item = this.findFirstFreeEntity(id);
		if (item == null) { //no proper found
			//we create new one
			if (poolEntities.size() >= maxPoolSize) {
				//pool reached full size - will try to release not used one entity (with different id - obviously because there are not any free with required id..)
				if (!this.freeUnusedEntity()) {
					//there is no unused entity
					log.error("PJSPool: No free slot for next PhantomJS process!");
					return null; //cannot create new entity!
				}
			}
			//else
			//create brand new entity
			log.debug("PJSPool: Creating new driver...");
			item = new Entity();
			item.create(id);
			this.poolEntities.add(item);
		} else {
			//found free entity with the same id
			log.debug("PJSPool: Reusing new driver...");
			item.reuse(id);
		}
		return item;
	}
	
	/**
	 * Tries to find existing entity in the pool having provided WebDriver
	 * @return Found entity or null if not found
	 */
	public Entity find(WebDriver driver) {
		for (Entity item : this.poolEntities) {
			if (item.driver == driver) {
				return item;
			}
		}
		return null;
	}

	/**
	 * It ends up randomly one of not currently used PhantomJS..exe process in the pool 
	 * @return true if entity has been freed or false if there is no any not used entity in the pool
	 */
	private boolean freeUnusedEntity() {
		for (Entity item : this.poolEntities) {
			if (!item.isUsed()) {
				item.dispose(); //it will end up PhantomJS..exe process
				return this.poolEntities.remove(item);
			}
		}
		return false;
	}

	/**
	 * Creates new Identifier object from SEL_LoadPageConstruct.Options object
	 */
	public Identifier createIdentifier(Options options) {
		return new Identifier(options);
	}
	
	/**
	 * Disposes entire pool - i.e. all pool entities (~all PhantomJS..exe processes in the pool will be terminated)
	 */
	public void dispose() {
		for (Entity item : this.poolEntities) {
			item.dispose();
		}
		this.poolEntities.clear();
	}
	
	private void close() {
		try {
			this.dispose();
		} catch (Exception e) {
			System.out.println("Error when closing PhantomJS instances! " + e.getMessage());
		} 
	}
}

