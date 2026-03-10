package depen;

import com.google.gson.Gson;

//Blueprints for DataLoader Class
public interface DataStorage {
    boolean store(Gson gson, Player[] players);
    Player[] load(Gson gson);
}
