package com.cfp.customer.service;

import com.cfp.customer.service.impl.CustomerServiceImpl;
import io.engagingspaces.graphql.servicediscovery.consumer.SchemaConsumer;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * Customer service.
 */
public interface CustomerService {

  /**
   * Create a new customer service instance.
   *
   * @param record the service discovery record
   * @return a new customer service instance
   */
  static CustomerService createService(final SchemaConsumer schemaConsumer, final Record record) {
    return new CustomerServiceImpl(schemaConsumer, record);
  }

  /**
   * Increase the inventory amount of a certain product.
   *
   * @param customerId the id of the product
   * @return the asynchronous result
   */
  Future<JsonObject> stays(String customerId);
}
