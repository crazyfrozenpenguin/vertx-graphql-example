package com.cfp.customer.service;

import com.cfp.customer.service.impl.AuthLevel;
import io.engagingspaces.graphql.events.SchemaReferenceData;
import io.engagingspaces.graphql.servicediscovery.consumer.DiscoveryRegistrar;
import io.engagingspaces.graphql.servicediscovery.consumer.SchemaConsumer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

import static com.cfp.customer.service.impl.SecurityRealm.securityRealm;
import static java.util.Objects.isNull;

public class CustomerServiceVerticle extends AbstractVerticle implements SchemaConsumer {

  private static final Logger log = LoggerFactory.getLogger(CustomerServiceVerticle.class);

  private CustomerService customerService;
  private DiscoveryRegistrar registrar;

  @Override
  public void start() throws Exception {
    registrar = DiscoveryRegistrar.create(vertx);
    SchemaConsumer.startDiscovery(new ServiceDiscoveryOptions().setName(securityRealm.getRealm(AuthLevel.CUSTOMER)), this);
  }

  @Override
  public void stop(final Future<Void> future) throws Exception {
    super.stop(future);
    SchemaConsumer.close(this);
  }

  @Override
  public void schemaDiscoveryEvent(final Record record) {
    log.info("Record: " + record.toJson());

    if (record.match(new JsonObject().put("name", "StaysQueries").put("status", "UP"))) {
      log.info("Service " + record.getName() + " is UP");

      // Init Customer Service
      if (isNull(this.customerService)) {
        customerService = CustomerService.createService(this, record);
      }
      customerService.stays("0").setHandler(ar -> System.out.println(ar.result()));
    }
  }

  @Override
  public void schemaReferenceEvent(final SchemaReferenceData schemaReferenceData) {
    Record record = schemaReferenceData.getRecord();
    log.info("Service " + record.getName() + " was " + schemaReferenceData.getStatus());
  }

  @Override
  public DiscoveryRegistrar discoveryRegistrar() {
    return registrar;
  }
}