package com.example.codiceprogetto.logic.appcontroller;

import com.example.codiceprogetto.logic.bean.ProductStockBean;
import com.example.codiceprogetto.logic.dao.ProductDAO;
import com.example.codiceprogetto.logic.entities.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrowseAccessoriesApplicativeController {
    public List<ProductStockBean> retrieveProduct() throws SQLException {
        List<Product> productList;

        List<ProductStockBean> productListBean = new ArrayList<>();
        ProductStockBean productStockBeans;

        productList = new ProductDAO().fetchAllProduct();

        for(Product prod : productList) {
            productStockBeans = new ProductStockBean(prod.getId(), prod.getImage(), prod.getPrice(), prod.getName());
            productListBean.add(productStockBeans);
        }

        return productListBean;
    }
}