package models.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.App;
import models.animal.Animal;
import models.entities.components.EntityComponent;
import models.enums.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop extends EntityComponent {

    String name;
    private ArrayList<BuildingShopProduct> buildings;
    private ArrayList<AnimalShopProduct> animals;
    private ArrayList<OtherShopProduct> products;
    private int startHour;
    private int endHour;


    Shop() {

    }

    @JsonCreator
    public Shop(@JsonProperty("name") String name) {
        this.name = name;
        ShopData data = App.shopRegistry.getData(name);
        this.startHour = data.startHour;
        this.endHour = data.endHour;


        this.buildings = new ArrayList<>(data.buildings);
        this.animals = new ArrayList<>(data.animals);
        this.products = new ArrayList<>(data.products);
    }

    private Shop(Shop other) {
        this.name = other.name;
        this.startHour = other.startHour;
        this.endHour = other.endHour;
        this.buildings = new ArrayList<>(other.buildings);
        this.animals = new ArrayList<>(other.animals);
        this.products = new ArrayList<>(other.products);
    }


    public List<ShopProduct> getAllProducts() {
        List<ShopProduct> res = new ArrayList<>();
        res.addAll(buildings);
        res.addAll(products);
        res.addAll(animals);
        return res;
    }

    public List<ShopProduct> getAvailableProducts() {

        return getAllProducts().stream().filter(ShopProduct::isAvailable).toList();

    }

    private ShopProduct getProductByName(String name) {
        for (ShopProduct product : getAllProducts()) {
            if(product.getEntity().getEntityName().equalsIgnoreCase(name))
                return product;
        }
        return null;
    }

    public OtherShopProduct getOtherShopProduct(String name) {
        ShopProduct product =  getProductByName(name);
        if(product instanceof OtherShopProduct) {
            return (OtherShopProduct) product;
        }
        return null;
    }

    public BuildingShopProduct getBuildingShopProduct(String name) {
        ShopProduct product = getProductByName(name);
        if(product instanceof BuildingShopProduct)
            return (BuildingShopProduct) product;
        return null;
    }

    public AnimalShopProduct getAnimalShopProduct(String name) {
        ShopProduct product = getProductByName(name);
        if (product instanceof AnimalShopProduct)
            return (AnimalShopProduct) product;
        return null;
    }


    /*
    public String allProducts() {
        StringBuilder sb = new StringBuilder();
        sb.append("Permanent Products : \n");
        for (ShopProduct permanentProduct : permanentProducts) {
            sb.append(permanentProduct.toString()).append("\n");
        }
        sb.append("----------------------------------------------------------\n");
        for (Season value : Season.values()) {

            if (seasonalProducts.get(value) != null) {
                sb.append(value.toString().charAt(0)
                        + value.toString().substring(1).toLowerCase() + " Products : \n");
                for (ShopProduct shopProduct : seasonalProducts.get(value)) {
                    sb.append(shopProduct.toString()).append("\n");
                }
                sb.append("----------------------------------------------------------\n");
            }
        }
        return sb.toString();
    }*/

    public String getName() {
        return name;
    }

    @Override
    public EntityComponent clone() {
        return new Shop(this);

    }


}
