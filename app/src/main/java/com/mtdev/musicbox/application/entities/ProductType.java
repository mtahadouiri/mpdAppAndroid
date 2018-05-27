package com.mtdev.musicbox.application.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 22/01/2018.
 */

public class ProductType {
    private int id;
    private String name;
    private String imgUrl;

    public ProductType() {
    }

    public ProductType(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }
    public ProductType(int id,String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
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

    public static List<ProductType> ProtoProdyctType(){
        List<ProductType> lstProductTypes = new ArrayList<>();

        ProductType p1 = new ProductType("Burgers","https://www.bk.com/sites/default/files/02200-1-BK_Web_WHOPPER_300x270px_CR.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Salades","https://www.bk.com/sites/default/files/02001-2_BK_Web_ChickenGardenSalad_300x270px_0.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sandwichs","https://www.bk.com/sites/default/files/BK_Web_NEWCRISPYCHICKEN_300x270px_CR_0.png");
        lstProductTypes.add(p1);
        p1 = new ProductType("Coffees", "https://www.bk.com/sites/default/files/BK_Web_BKJOEHOTCOFFEE_300x270px%255b9%255d.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sweets", "https://www.bk.com/sites/default/files/Thumb_0006_Sweets-chocolate2.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sides & Milkshakes", "https://www.bk.com/sites/default/files/Thumb_0005_Sides.jpg");
        lstProductTypes.add(p1);

        return lstProductTypes;
    }
}
