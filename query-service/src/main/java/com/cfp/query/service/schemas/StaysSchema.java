package com.cfp.query.service.schemas;

import com.cfp.query.service.fetchers.StaysData;
import com.cfp.query.service.fetchers.HotelsData;
import graphql.Scalars;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import io.engagingspaces.graphql.servicediscovery.publisher.SchemaRegistrar;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;

public class StaysSchema {

  private static final Logger log = getLogger(StaysSchema.class);

  public static Vertx vertx;

  public static GraphQLObjectType hotelType = newObject()
    .name("Hotel")
    .description("Hotel")
    .field(newFieldDefinition()
      .name("id")
      .description("Hotel ID")
      .type(new GraphQLNonNull(Scalars.GraphQLLong)))
    .field(newFieldDefinition()
      .name("hotel")
      .description("Hotel name")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .field(newFieldDefinition()
      .name("address")
      .description("Hotel address")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .field(newFieldDefinition()
      .name("country")
      .description("Hotel country")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .build();

  private static GraphQLObjectType stayType = newObject()
    .name("Stay")
    .description("Hotel Stay")
    .field(newFieldDefinition()
      .name("hotel")
      .description("Hotel ID")
      .type(new GraphQLNonNull(new GraphQLTypeReference("Hotel")))
      .dataFetcher(HotelsData.getHotelsByIdDataFetcher))
    .field(newFieldDefinition()
      .name("arrival")
      .description("Arrival date")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .field(newFieldDefinition()
      .name("departure")
      .description("Departure date")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .build();

  private static GraphQLObjectType customerStayType = newObject()
    .name("CustomerStays")
    .description("Customer stays")
    .field(newFieldDefinition()
      .name("id")
      .description("Customer ID")
      .type(new GraphQLNonNull(Scalars.GraphQLLong)))
    .field(newFieldDefinition()
      .name("customer")
      .description("Customer name")
      .type(new GraphQLNonNull(Scalars.GraphQLString)))
    .field(newFieldDefinition()
      .name("stays")
      .description("Customer stays")
      .type(new GraphQLList(stayType)))
    .build();

  private static final GraphQLObjectType queryType = newObject()
    .name("StaysQueries")
    .description("Stays queries")
    .field(newFieldDefinition()
      .name("staysById")
      .description("Stays by customer ID")
      .type(customerStayType)
      .argument(newArgument()
        .name("id")
        .description("id of the customer")
        .type(new GraphQLNonNull(Scalars.GraphQLLong)))
      .dataFetcher(StaysData.getStaysByIdDataFetcher))
    .field(newFieldDefinition()
      .name("hotelsById")
      .description("Hotels by customer ID")
      .type(hotelType)
      .argument(newArgument()
        .name("id")
        .description("id of the hotel")
        .type(new GraphQLNonNull(Scalars.GraphQLLong)))
      .dataFetcher(HotelsData.getHotelsByIdDataFetcher))
    .field(newFieldDefinition()
      .name("hotels")
      .description("Find all hotels")
      .type(new GraphQLList(hotelType))
      .dataFetcher(HotelsData.getHotelsDataFetcher))
    .field(newFieldDefinition()
      .name("stays")
      .description("Find all stays")
      .type(new GraphQLList(customerStayType))
      .dataFetcher(StaysData.getStaysDataFetcher))
    .build();

  private static final GraphQLSchema digitalExperienceSchema = newSchema()
    .query(queryType)
    .build();

  public static GraphQLSchema staysSchema(final SchemaRegistrar registrar) {
    requireNonNull(registrar);
    StaysSchema.vertx = registrar.getVertx();
    requireNonNull(vertx);
    return digitalExperienceSchema;
  }

//  public static void main(final String[] args) {
//    log.info("Quering...");
//    final String query = "{hotelsById(id:0){id hotel address country}}";
//    final String query = "{hotelsById(id:1){id}, staysById(id:1){customer, stays {departure, arrival}}}";
//    final String query = "{hotels {hotel}}";
//    final String query = "{staysById(id:0) {customer, stays {departure, hotel {address}}}}";
//    final String query = "{stays {customer, stays {arrival, hotel {hotel, address, country}}}}";
//    final Map r = (Map) new GraphQL(digitalExperienceSchema).execute(query).getData();
//    log.info("" + r);
//  }
}