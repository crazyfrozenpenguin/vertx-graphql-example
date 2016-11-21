package com.cfp.customer.service.impl;

import java.util.Optional;

import com.cfp.customer.service.CustomerService;
import io.engagingspaces.graphql.query.Queryable;
import io.engagingspaces.graphql.servicediscovery.consumer.SchemaConsumer;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

import static com.cfp.customer.service.impl.AuthLevel.CUSTOMER;
import static com.cfp.customer.service.impl.SecurityRealm.securityRealm;

/**
 * Implementation of {@link CustomerService}.
 */
public class CustomerServiceImpl implements CustomerService {

  private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

  private Record record;
  private SchemaConsumer schemaConsumer;

  public CustomerServiceImpl(SchemaConsumer schemaConsumer, Record record) {
    this.schemaConsumer = schemaConsumer;
    this.record = record;
  }

  @Override
  public Future<JsonObject> stays(String customerId) {
    log.info("Retrieving Stays: " + customerId);
    Future<JsonObject> future = Future.future();

    ServiceReference reference = discoveryFor(CUSTOMER).getReference(record);
    Queryable queryable = reference.cached() == null ? reference.get() : reference.cached();
    queryable.query("{staysById(id:" + Integer.valueOf(customerId) + ") {customer, stays {arrival, hotel {hotel, address, country}}}}", rh -> {
      if (rh.succeeded()) {
        future.complete(rh.result().getData());
      } else {
        future.fail(rh.cause());
      }
    });

    return future;
  }

  ServiceDiscovery discoveryFor(AuthLevel authLevel) {
    final Optional<ServiceDiscovery> discovery = schemaConsumer.getDiscovery(securityRealm.getRealm(authLevel));
    return discovery.get(); // Fails here
  }
}
