package com.mtdev.musicbox.application.utils;

import com.mtdev.musicbox.application.entities.Product;
import com.mtdev.musicbox.application.entities.ProductType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 27/05/2018.
 */

public class ProductTypeListJSONParser {
    static List<ProductType> productTypeList;

    public static List<ProductType> parseData(String content) {

        JSONArray games_arry = null;
        ProductType product = null;
        try {

            games_arry = new JSONArray(content);
            productTypeList = new ArrayList<>();

            for (int i = 0; i < games_arry.length(); i++) {

                JSONObject obj = games_arry.getJSONObject(i);
                product = new ProductType();

                product.setId(obj.getInt("id"));
                product.setName(obj.getString("name"));
                product.setImgUrl(obj.getString("image"));
                productTypeList.add(product);
            }
            return productTypeList;

        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
