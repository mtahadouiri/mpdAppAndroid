package com.mtdev.musicbox.application.utils;

import android.util.Log;

import com.mtdev.musicbox.application.entities.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 27/05/2018.
 */

public class ProductListJSONParser {

        static List<Product> productList;

        public static List<Product> parseData(String content) {

            JSONArray games_arry = null;
            Product product = null;
            try {

                games_arry = new JSONArray(content);
                productList = new ArrayList<>();

                for (int i = 0; i < games_arry.length(); i++) {

                    JSONObject obj = games_arry.getJSONObject(i);
                    product = new Product();

                    product.setId(obj.getInt("id"));
                    product.setName(obj.getString("name"));
                    product.setDetails(obj.getString("description"));
                    product.setPrice(obj.getInt("price"));
                    product.setImgUrl(obj.getString("image"));
                    product.setpType(obj.getInt("menu_id"));
                    productList.add(product);
                }
                return productList;

            }
            catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }
}
