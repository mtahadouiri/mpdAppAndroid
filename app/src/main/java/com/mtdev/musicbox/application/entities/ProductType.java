package com.mtdev.musicbox.application.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 22/01/2018.
 */

public class ProductType {
    private String name;
    private String desc;
    private String imgUrl;

    public ProductType() {
    }

    public ProductType(String name, String desc, String imgUrl) {
        this.name = name;
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public static List<ProductType> ProtoProdyctType(){
        List<ProductType> lstProductTypes = new ArrayList<>();
        ProductType p1 = new ProductType("Burgers","The best tasty burgers","https://www.bk.com/sites/default/files/02200-1-BK_Web_WHOPPER_300x270px_CR.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Salades","Tasty and healthy !","https://www.bk.com/sites/default/files/02001-2_BK_Web_ChickenGardenSalad_300x270px_0.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sandwichs","Just ...  !","https://www.bk.com/sites/default/files/BK_Web_NEWCRISPYCHICKEN_300x270px_CR_0.png");
        lstProductTypes.add(p1);
        p1 = new ProductType("Coffees", "From brazil with love !","https://www.bk.com/sites/default/files/BK_Web_BKJOEHOTCOFFEE_300x270px%255b9%255d.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sweets", "From italy with love !","https://www.bk.com/sites/default/files/Thumb_0006_Sweets-chocolate2.jpg");
        lstProductTypes.add(p1);
        p1 = new ProductType("Sides & Milkshakes", "From my brain with love !","https://www.bk.com/sites/default/files/Thumb_0005_Sides.jpg");
        lstProductTypes.add(p1);




        return lstProductTypes;
    }
}
