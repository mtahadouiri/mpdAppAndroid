package com.mtdev.musicbox.Client.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taha on 22/04/2018.
 */

public class Product {
    private String name;
    private String imgUrl;
    private String pType;
    private String details;
    private int price;
    private int quantity;

    public Product() {
    }

    public Product(String name, String imgUrl, String pType, String details, int price, int quantity) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.pType = pType;
        this.details = details;
        this.price = price;
        this.quantity = quantity;
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

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", pType='" + pType + '\'' +
                ", details='" + details + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    public static List<Product> ProtoProducts() {
        List<Product> lstProducts = new ArrayList<>();
        Product p1 = new Product("BACON KING™ JR. SANDWICH", "https://www.bk.com/sites/default/files/02416-02_BK_Web_BaconKingJr_500x540px_CR.png", "Burger", "Introducing the BACON KING™ Jr. Sandwich, smaller package, same BIG taste. Two flame–grilled 100% beef patties topped with thick-cut smoked bacon, melted American cheese, ketchup and creamy mayonnaise on a toasted sesame seed bun.", 14, 1);
        lstProducts.add(p1);
        p1 = new Product("DOUBLE QUARTER POUND KING™", "https://www.bk.com/sites/default/files/02568-2%20BK_Web_DblQtrPndKing_300x270px_CR.png", "Burger", "Featuring more than ½ lb.* of flame-grilled 100% beef, topped with all of our classic favorites: American cheese, freshly sliced onions, zesty pickles, ketchup, & mustard all on a toasted sesame seed bun.", 15, 1);
        lstProducts.add(p1);
        p1 = new Product("BBQ BACON WHOPPER® SANDWICH", "https://www.bk.com/sites/default/files/Thumb_0004_BBQ_Bacon_Whopper_0.jpg", "Burger", "Featuring more than ½ lb.* of flame-grilled 100% beef, topped with all of our classic favorites: American cheese, freshly sliced onions, zesty pickles, ketchup, & mustard all on a toasted sesame seed bun.", 15, 1);
        lstProducts.add(p1);
        p1 = new Product("RODEO® KING™", "https://www.bk.com/sites/default/files/02109-2%20BK_Web_RodeoKing_500x540px.png", "Burger", "Flame-grilled to perfection.", 18, 1);
        lstProducts.add(p1);
        p1 = new Product("WHOPPER® SANDWICH", "https://www.bk.com/sites/default/files/02200-1-BK_Web_WHOPPER_500x540px_CR_R%255b8%255d.png", "Burger", "America's favorite burger®.", 17, 1);
        lstProducts.add(p1);
        p1 = new Product("BBQ BACON WHOPPER® SANDWICH", "https://www.bk.com/sites/default/files/BC-Whopper-detail.png", "Burger", "Enjoy the BBQ Flavor.\n", 17, 1);
        lstProducts.add(p1);

        return lstProducts;
    }
}
