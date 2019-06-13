package com.mind.dashboardui;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductsViewActivity extends AppCompatActivity {

    TabLayout tabMenu;
    ViewPager viewPager;
    List<ProductsClass> productClassList;
    AdapterPopularFood adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadProducts();
        tabMenu     = findViewById(R.id.tabMenu);
        viewPager   = findViewById(R.id.viewPager);
        productClassList = new ArrayList<>();
        setupViewPager(viewPager);
        tabMenu.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);  // 0 = drink , 1=food
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DrinkFragment(),"Drink");
        adapter.addFragment(new FoodFragment(),"Food");
        viewPager.setAdapter(adapter);
    }

    private void loadProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://172.16.27.21:8080/system/getAllProducts",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray products = new JSONArray(response);
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObject = products.getJSONObject(i);

                                int id = productObject.getInt("id");
                                String title = productObject.getString("title");
                                String shortDescription = productObject.getString("shortdesc");
                                double price = productObject.getDouble("price");
                                double rating = productObject.getDouble("rating");
                                String image = productObject.getString("image");
                                String status = productObject.getString("status");
                                ProductsClass ProductClass = new ProductsClass(id, title, shortDescription, rating, price, image, status, "null");
                                productClassList.add(ProductClass);
                            }
                            //adapter = new AdapterPopularFood(ProductsViewActivity(),productClassList);
                            //recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductsViewActivity.this);
                builder.setTitle("Warning!")
                        .setMessage("Server connection error").setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProductsViewActivity.super.onBackPressed();
                            }
                        }).create().show();

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
