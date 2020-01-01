package com.example.starter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import io.vertx.blog.first.Whisky;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {
  public static final String ANSI_GREEN  = "\u001B[32m";
  public static final String ANSI_RESET = "\u001B[0m";
  private Map<Integer, Whisky> products = new LinkedHashMap<>();
  
  private void createSomeData() {
	  Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
	  products.put(bowmore.getId(), bowmore);

	  Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
	  products.put(talisker.getId(), talisker);

	  Whisky buchan = new Whisky("BUCHANAN’S SCOTCH WHISKY", "Latin America");
	  products.put(buchan.getId(), buchan);

	  Whisky rich = new Whisky("RICH AND RARE CANADIAN WHISKY", "Canadian");
	  products.put(rich.getId(), rich);

	  Whisky kesler = new Whisky("KESSLER AMERICAN WHISKEY", "American ");
	  products.put(kesler.getId(), kesler);

	  Whisky black = new Whisky("BLACK VELVET CANADIAN WHISKY", "Canada ");
	  products.put(black.getId(), black);
  }
  
  private void getAll(RoutingContext routingContext) {
	  routingContext.response()
	      .putHeader("content-type", "application/json; charset=utf-8")
	      .end(Json.encodePrettily(products.values()));
  }
  private void addOne(RoutingContext routingContext) {
	  final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(),
		      Whisky.class);
	  System.out.print(routingContext.getBodyAsString());
		  products.put(whisky.getId(), whisky);
		  routingContext.response()
		      .setStatusCode(201)
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(whisky));
  }
  @Override
  public void start(Future<Void> fut) {
	  
	  createSomeData();
	  
	  Router router = Router.router(vertx);
	  Set<String> allowedHeaders = new HashSet<>();
	  allowedHeaders.add("x-requested-with");
	  allowedHeaders.add("Access-Control-Allow-Origin");
	  allowedHeaders.add("origin");
	  allowedHeaders.add("Content-Type");
	  allowedHeaders.add("accept");
	  allowedHeaders.add("X-PINGARUNER");
	  Set<HttpMethod> allowedMethods = new HashSet<>();
	  allowedMethods.add(HttpMethod.GET);
	  allowedMethods.add(HttpMethod.POST);
	  allowedMethods.add(HttpMethod.OPTIONS);
	  allowedMethods.add(HttpMethod.DELETE);
	  allowedMethods.add(HttpMethod.PATCH);
	  allowedMethods.add(HttpMethod.PUT);
	  router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));
	  // Bind "/" to our hello message - so we are still compatible.
	  router.route("/").handler(routingContext -> {
	    HttpServerResponse response = routingContext.response();
	    response
	        .putHeader("content-type", "text/html")
	        .end("<h1>Hello from my first Vert.x 3 application</h1>");
	  });
	  router.route("/assets/*").handler(StaticHandler.create("assets"));
	  router.get("/api/whiskies").handler(this::getAll);
	  router.route("/api/whiskies*").handler(BodyHandler.create());
	  router.post("/api/whiskies").handler(this::addOne);
	  vertx.createHttpServer()
	  .requestHandler(router::accept)
	  .listen(
			  config().getInteger("http.port", 8080),
			  result -> {
				  if(result.succeeded()) {
					  System.out.println(ANSI_GREEN + "API running on port :8080..."+ ANSI_RESET);
					  fut.complete();
					   
				  }else {
					  fut.fail(result.cause());
				  }
			  	}
			  );
	  
  }
}
