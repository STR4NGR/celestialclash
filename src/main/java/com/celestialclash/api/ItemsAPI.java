package com.celestialclash.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;

public class ItemsAPI {

    private Javalin app;
    private ObjectMapper objectMapper;

    public void start(int port) {
        app = Javalin.create().start(port);
        objectMapper = new ObjectMapper();

        app.get("/items", ctx -> {
            ctx.result(objectMapper.writeValueAsString(getItems()));
        });
    }

    public void stop() {
        if(app != null) {
            app.stop();
        }

    }

    private List<Item> getItems() {
        // Здесь реализуйте логику получения данных с сервера Minecraft
        // Например, запросы к вашему серверу Minecraft
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "Sword", 10.0));
        items.add(new Item(2, "Armor", 20.0));
        items.add(new Item(3, "Potion", 5.0));
        return items;
    }

    public class Item {
        private int id;
        private String name;
        private double price;
    
        public Item(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    
        // Геттеры для полей
        public int getId() {
            return id;
        }
    
        public String getName() {
            return name;
        }
    
        public double getPrice() {
            return price;
        }
    
        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
    
}
