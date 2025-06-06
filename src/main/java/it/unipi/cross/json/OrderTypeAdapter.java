package it.unipi.cross.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.OrderType;
import it.unipi.cross.data.StopOrder;

public class OrderTypeAdapter implements JsonSerializer<Order>, JsonDeserializer<Order> {

   @Override
   public JsonElement serialize(Order order, Type type, JsonSerializationContext context) {
      // Just serialize normally - the orderType field is already there
      return context.serialize(order, order.getClass());
   }

   @Override
   public Order deserialize(JsonElement json, Type type, JsonDeserializationContext context)
         throws JsonParseException {

      JsonObject jsonObject = json.getAsJsonObject();

      // Read the existing orderType field
      if (!jsonObject.has("orderType")) {
         throw new JsonParseException("Missing orderType field in Order JSON");
      }

      String orderTypeStr = jsonObject.get("orderType").getAsString();
      OrderType orderType;

      try {
         orderType = OrderType.valueOf(orderTypeStr.toLowerCase());
      } catch (IllegalArgumentException e) {
         throw new JsonParseException("Unknown order type: " + orderTypeStr);
      }

      // Deserialize to the appropriate concrete class based on orderType
      switch (orderType) {
         case limit:
            return context.deserialize(jsonObject, LimitOrder.class);
         case stop:
            return context.deserialize(jsonObject, StopOrder.class);
         case market:
            return context.deserialize(jsonObject, MarketOrder.class);
         default:
            throw new JsonParseException("Unsupported order type: " + orderType);
      }
   }
}