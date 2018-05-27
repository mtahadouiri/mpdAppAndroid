package com.mtdev.musicbox.application.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 21/01/2018.
 */

public class Product {
    private int id;
    private String name;
    private String imgUrl;
    private int pType;
    private String details;
    private int price;
    private int quantity;

    public Product() {
    }

    public Product(String name, String imgUrl, int pType, String details, int price, int quantity) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.pType = pType;
        this.details = details;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(int id,String name, String imgUrl, int pType, String details, int price, int quantity) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.pType = pType;
        this.details = details;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getpType() {
        return pType;
    }

    public void setpType(int pType) {
        this.pType = pType;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", pType='" + pType + '\'' +
                ", details='" + details + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    public static List<Product> ProtoProducts() {
        List<Product> lstProducts = new ArrayList<>();
        Product p1 = new Product("BACON KING JR. SANDWICH", "https://www.bk.com/sites/default/files/02416-02_BK_Web_BaconKingJr_500x540px_CR.png", 1, "Introducing the BACON KING™ Jr. Sandwich, smaller package, same BIG taste. Two flame–grilled 100% beef patties topped with thick-cut smoked bacon, melted American cheese, ketchup and creamy mayonnaise on a toasted sesame seed bun.", 14, 1);
        lstProducts.add(p1);
         p1 = new Product("DOUBLE QUARTER POUND KING", "https://www.bk.com/sites/default/files/02568-2%20BK_Web_DblQtrPndKing_300x270px_CR.png", 2, "Featuring more than ½ lb.* of flame-grilled 100% beef, topped with all of our classic favorites: American cheese, freshly sliced onions, zesty pickles, ketchup, & mustard all on a toasted sesame seed bun.", 15, 1);
        lstProducts.add(p1);
        p1 = new Product("BBQ BACON WHOPPER SANDWICH", "https://www.bk.com/sites/default/files/Thumb_0004_BBQ_Bacon_Whopper_0.jpg", 3, "Featuring more than ½ lb.* of flame-grilled 100% beef, topped with all of our classic favorites: American cheese, freshly sliced onions, zesty pickles, ketchup, & mustard all on a toasted sesame seed bun.", 15, 1);
        lstProducts.add(p1);
        return lstProducts;
    }
}
