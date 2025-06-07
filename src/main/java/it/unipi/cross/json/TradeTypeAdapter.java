package it.unipi.cross.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import it.unipi.cross.data.OrderType;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.Type;

public class TradeTypeAdapter extends TypeAdapter<Trade> {
   @Override
   public void write(JsonWriter out, Trade trade) throws IOException {
      out.beginObject();
      out.name("orderId").value(trade.getOrderId());
      if (trade.getUsername() != null && !trade.getUsername().isEmpty()) {
         out.name("username").value(trade.getUsername());
      }
      out.name("type").value(trade.getType() != null ? trade.getType().name() : null);
      out.name("orderType").value(trade.getOrderType() != null ? trade.getOrderType().name() : null);
      out.name("size").value(trade.getSize());
      out.name("price").value(trade.getPrice());
      out.name("timestamp").value(trade.getTimestamp());
      out.endObject();
   }

   @Override
   public Trade read(JsonReader in) throws IOException {
      Trade trade = new Trade();

      in.beginObject();
      while (in.hasNext()) {
         String name = in.nextName();
         switch (name) {
            case "orderId":
               trade.setOrderId(in.nextInt());
               break;
            case "username":
               trade.setUsername(in.nextString());
               break;
            case "type":
               // You may need to handle nulls and conversion
               String typeStr = in.nextString();
               if (typeStr != null)
                  trade.setType(Type.valueOf(typeStr));
               break;
            case "orderType":
               String orderTypeStr = in.nextString();
               if (orderTypeStr != null)
                  trade.setOrderType(OrderType.valueOf(orderTypeStr));
               break;
            case "size":
               trade.setSize(in.nextInt());
               break;
            case "price":
               trade.setPrice(in.nextInt());
               break;
            case "timestamp":
               trade.setTimestamp(in.nextLong());
               break;
            default:
               in.skipValue();
         }
      }
      in.endObject();

      return trade;
   }
}