package vermillion;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import vermillion.database.DbVerticle;
import vermillion.http.HttpServerVerticle;

public class MainVerticle extends AbstractVerticle
{	
	public	final static Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture)throws Exception
	{	
		deployHelper(DbVerticle.class.getName())
		.setHandler(db -> {
			
			if(!db.succeeded())
			{
				logger.debug(db.cause());
				startFuture.fail(db.cause().toString());
			}
			
			deployHelper(HttpServerVerticle.class.getName())
		.setHandler(http -> {
			
			if(!http.succeeded())
			{
				logger.debug(http.cause());
				startFuture.fail(http.cause().toString());
			}
			
			startFuture.complete();
				});
		});	
	}
	private Future<Void> deployHelper(String name)
	{
		   final Future<Void> future = Future.future();
		   
		   if("iudx.http.HttpServerVerticle".equals(name))
		   {
			   vertx.deployVerticle(name, res -> {
			   if(res.succeeded()) 
			   {
				   logger.info("Deployed Verticle " + name);
				   future.complete();
			   }
			   else
			   {
				   logger.fatal("Failed to deploy verticle " + res.cause());
				   future.fail(res.cause());
			   }
					   						  
					   					  									});
		   }
		   else
		   {
			   vertx.deployVerticle(name, res -> 
			   {
			      if(res.failed())
			      {
			         logger.fatal("Failed to deploy verticle " + name + " Cause = "+res.cause());
			         future.fail(res.cause());
			      } 
			      else 
			      {
			    	 logger.info("Deployed Verticle " + name);
			         future.complete();
			      }
			   });
		   }
		   
		   return future;
}
}	
