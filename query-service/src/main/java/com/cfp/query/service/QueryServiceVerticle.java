package com.cfp.query.service;

import com.cfp.query.service.schemas.StaysSchema;
import io.engagingspaces.graphql.servicediscovery.publisher.SchemaPublisher;
import io.engagingspaces.graphql.servicediscovery.publisher.SchemaRegistrar;
import io.engagingspaces.graphql.servicediscovery.publisher.SchemaRegistration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

import static io.vertx.core.logging.LoggerFactory.getLogger;

public class QueryServiceVerticle extends AbstractVerticle implements SchemaPublisher {

  private static final Logger log = getLogger(QueryServiceVerticle.class);

  private static final String SERVICE_DISCOVERY_NAME = "graphql-schema-discovery";

  private SchemaRegistrar registrar;

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    registrar = SchemaRegistrar.create(vertx);

    publish(new ServiceDiscoveryOptions().setName(SERVICE_DISCOVERY_NAME), StaysSchema.staysSchema(registrar), rh -> {
      if (rh.succeeded()) {
        log.info("Published Customer Stays schema...");
        startFuture.complete();
      } else {
        startFuture.fail(rh.cause());
      }
    });
  }

  @Override
  public void stop(final Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);

    SchemaPublisher.close(this, rh -> {
      if (rh.succeeded()) {
        log.info("Unpublished Customer Stays schema...");
        stopFuture.complete();
      } else {
        stopFuture.fail(rh.cause());
      }
    });
  }

  @Override
  public void schemaPublished(final SchemaRegistration registration) {
    log.info("Schema " + registration.getSchemaName() + " is now " + registration.getRecord().getStatus());
  }

  @Override
  public void schemaUnpublished(final SchemaRegistration registration) {
    log.info("Schema " + registration.getSchemaName() + " was " + registration.getRecord().getStatus());
  }

  @Override
  public SchemaRegistrar schemaRegistrar() {
    return registrar;
  }
}